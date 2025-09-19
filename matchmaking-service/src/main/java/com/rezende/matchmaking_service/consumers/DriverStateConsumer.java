package com.rezende.matchmaking_service.consumers;

import com.rezende.driver_service.dto.UserRegisterEventDTO;
import com.rezende.driver_service.dto.VehicleApprovedEventDTO;
import com.rezende.driver_service.services.DriverService;
import com.rezende.matchmaking_service.dto.ActiveDriverDTO;
import com.rezende.matchmaking_service.dto.DriverStatusUpdateEvent;
import com.rezende.matchmaking_service.enums.OperationalStatus;
import com.rezende.matchmaking_service.repositories.DriverRedisRepository;
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
public class DriverStateConsumer {

    private final DriverRedisRepository driverRedisRepository;

    public DriverStateConsumer(final DriverRedisRepository driverRedisRepository) {
        this.driverRedisRepository = driverRedisRepository;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.driver-status-updates}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleDriverStatusUpdates(@Payload final DriverStatusUpdateEvent event, final Acknowledgment ack) {
        log.info("Evento DriverStatusUpdateEvent recebido para o driverId: {}", event.driverId());
        try {
            if (event.newStatus() == OperationalStatus.ONLINE)
                driverRedisRepository.saveOrUpdate(ActiveDriverDTO);
            ack.acknowledge();
            log.info("Evento de criação de utilizador para o userId: {} processado com sucesso.", event.driverId());
        } catch (Exception e) {
            log.error("Erro ao processar o evento UserRegisterEventDTO para o userId: {}. A mensagem será reenviada.", event.driverId(), e);
            throw new RuntimeException("Error processing User Created Event", e);
        }
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.vehicle-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleVehicleApproval(@Payload final VehicleApprovedEventDTO event, final Acknowledgment ack) {
        log.info("Evento VehicleApprovedEventDTO recebido para o driverId: {}", event.driverId());
        try {
            driverService.processVehicleApprovalEvent(event);
            ack.acknowledge();
            log.info("Evento de aprovação de veículo para o driverId: {} processado com sucesso.", event.driverId());
        } catch (Exception e) {
            log.error("Erro ao processar o evento VehicleApprovedEventDTO para o driverId: {}. A mensagem será reenviada.", event.driverId(), e);
            throw new RuntimeException("Error processing Vehicle Approved Event", e);
        }
    }

    @DltHandler
    public void handleDlt(final Object event) {
        log.error("[DLT] Mensagem recebida na Dead Letter Topic. Evento: {}", event);
    }
}