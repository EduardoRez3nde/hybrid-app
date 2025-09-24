package com.rezende.driver_service.consumer;

import com.rezende.driver_service.dto.DriverRatedEventDTO;
import com.rezende.driver_service.dto.UserRegisterEventDTO;
import com.rezende.driver_service.dto.VehicleApprovedEventDTO;
import com.rezende.driver_service.services.DriverService;
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
public class KafkaConsumerService {

    private final DriverService driverService;

    public KafkaConsumerService(final DriverService driverService) {
        this.driverService = driverService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.user-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserCreation(@Payload final UserRegisterEventDTO event, final Acknowledgment ack) {

        log.info("Evento UserRegisterEventDTO recebido para o userId: {}", event.userId());

        try {
            driverService.processNewUserEvent(event);
            ack.acknowledge();
            log.info("Evento de criação de utilizador para o userId: {} processado com sucesso.", event.userId());

        } catch (Exception e) {
            log.error("Erro ao processar o evento UserRegisterEventDTO para o userId: {}. A mensagem será reenviada.", event.userId(), e);
            throw new RuntimeException("Error processing User Created Event", e);
        }
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.approved-vehicle-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleVehicleApproval(@Payload final VehicleApprovedEventDTO event, final Acknowledgment ack) {

        log.info("Evento VehicleApprovedEvent recebido para o driverId: {}", event.driverId());

        try {
            driverService.processVehicleApprovalEvent(event);
            ack.acknowledge();
            log.info("Evento de aprovação de veículo para o driverId: {} processado com sucesso.", event.driverId());

        } catch (Exception e) {
            log.error("Erro ao processar o evento VehicleApprovedEventDTO para o driverId: {}. A mensagem será reenviada.", event.driverId(), e);
            throw new RuntimeException("Error processing Vehicle Approved Event", e);
        }
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.driver-rated-event}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleDriverRatedEvent(@Payload final DriverRatedEventDTO event, final Acknowledgment ack) {

        log.info("Evento DriverRatedEventDTO recebido para o driverId: {}", event.driverId());

        try {
            driverService.processDriverRatedEvent(event);
            ack.acknowledge();
            log.info("Evento de avaliação do motorista para o driverId: {} processado com sucesso.", event.driverId());

        } catch (Exception e) {
            log.error("Erro ao processar o evento DriverRatedEventDTO para o driverId: {}. A mensagem será reenviada.", event.driverId(), e);
            throw new RuntimeException("Error processing Vehicle Approved Event", e);
        }
    }


    @DltHandler
    public void handleDlt(final Object event) {
        log.error("[DLT] Mensagem recebida na Dead Letter Topic. Evento: {}", event);
    }
}