package com.rezende.taxi_service.consumer;

import com.rezende.taxi_service.dto.DriverAssignedToRideEventDTO;
import com.rezende.taxi_service.repositories.RideRepository;
import com.rezende.taxi_service.services.RideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MatchmakingEventsConsumer {

    private final RideService rideService;

    public MatchmakingEventsConsumer(final RideService rideService) {
        this.rideService = rideService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.driver-assigned-ride-event}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleDriverAssignedEvent(@Payload final DriverAssignedToRideEventDTO event, final Acknowledgment ack) {

        log.info("Recebido evento de atribuição para a corrida {}. Notificando o motorista {}",
                event.rideId(), event.driverId());
        try {
            rideService.driverAssigned(event);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Falha ao processar evento de notificação para o motorista {}", event.driverId(), e);
            throw new RuntimeException("Falha ao notificar motorista", e);
        }
    }
}
