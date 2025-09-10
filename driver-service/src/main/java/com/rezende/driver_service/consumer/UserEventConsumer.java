package com.rezende.driver_service.consumer;

import com.rezende.driver_service.services.DriverService;
import com.rezende.driver_service.dto.UserRegisterEventDTO;
import com.rezende.driver_service.exceptions.UserEventProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventConsumer {

    private final DriverService driverService;

    public UserEventConsumer(final DriverService driverService) {
        this.driverService = driverService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.user-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handlerUserCreation(@Payload final UserRegisterEventDTO event, final Acknowledgment ack) {

        log.info("Evento UserCreatedEvent recebido para o userId: {}", event.userId());

        try {
            driverService.processNewUserEvent(event);
            ack.acknowledge();
            log.info("Evento para o userId: {} processado e confirmado (acknowledged) com sucesso.", event.userId());
        } catch (Exception e) {
            log.error("Erro ao processar o evento UserCreatedEvent para o userId: {}. A mensagem será reenviada.", event.userId(), e);
            throw new UserEventProcessingException("Error processing User Created Event", e);
        }
    }

    @DltHandler
    public void handleDlt(final UserRegisterEventDTO event) {
        log.error("[DLT] Mensagem recebida na Dead Letter Topic. Evento: {}", event);
    }
}
