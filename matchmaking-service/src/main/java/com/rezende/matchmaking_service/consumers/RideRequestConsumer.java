package com.rezende.matchmaking_service.consumers;

import com.rezende.matchmaking_service.dto.ActiveDriverDTO;
import com.rezende.matchmaking_service.dto.RideRequestedEventDTO;
import com.rezende.matchmaking_service.enums.OperationalStatus;
import com.rezende.matchmaking_service.services.MatchmakingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RideRequestConsumer {

    private final MatchmakingService matchmakingService;

    public RideRequestConsumer(final MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.ride-requested-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleRideRequestedEvent(@Payload final RideRequestedEventDTO event, final Acknowledgment ack) {

        log.info("Pedido de corrida recebido {} para matchmaking.", event.rideId());

        try {
            matchmakingService.findBestDriverForRide(event);

            ack.acknowledge();
            log.info("Evento de solicitação de corrida para o rideId: {} processado com sucesso.", event.rideId());

        } catch (Exception e) {
            log.error("Falha no processo de matchmaking para a corrida {}. ", event.rideId(), e);
            throw new RuntimeException("Matchmaking Failed.", e);
        }
    }
}
