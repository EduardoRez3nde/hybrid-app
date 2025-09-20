package com.rezende.matchmaking_service.consumers;

import com.rezende.matchmaking_service.dto.ActiveDriverDTO;
import com.rezende.matchmaking_service.dto.DriverLocationUpdatesEvent;
import com.rezende.matchmaking_service.dto.DriverProfileUpdateEventDTO;
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
            if (event.newStatus() == OperationalStatus.ONLINE) {
                driverRedisRepository.saveOrUpdate(ActiveDriverDTO.of(event));
                log.info("Motorista {} adicionado/atualizado no cache de matchmaking.", event.driverId());
            }
            else {
                driverRedisRepository.delete(event.driverId());
                log.info("Motorista {} removido do cache de matchmaking devido ao status: {}", event.driverId(), event.newStatus());
            }

            ack.acknowledge();
            log.info("Evento de status para o driverId: {} processado com sucesso.", event.driverId());

        } catch (Exception e) {
            log.error("Erro ao processar o evento de status para o driverId: {}. A mensagem será reenviada.", event.driverId(), e);
            throw new RuntimeException("Erro a processar evento de status do motorista", e);
        }
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.driver-location-updates}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleDriverLocationUpdates(@Payload final DriverLocationUpdatesEvent event, final Acknowledgment ack) {

        log.info("Evento DriverLocationUpdatesEvent recebido para o driverId: {}", event.driverId());

        try {
            driverRedisRepository.updateLocation(event.driverId(), event.latitude(), event.longitude());

            ack.acknowledge();
            log.info("Evento de localização para o driverId: {} processado com sucesso.", event.driverId());

        } catch (Exception e) {
            log.error("Erro ao processar o evento de status para o driverId: {}. A mensagem será reenviada.", event.driverId(), e);
            throw new RuntimeException("Erro a processar evento de localização do motorista", e);
        }
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.driver-average-rating}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleDriverAverageRating(@Payload final DriverProfileUpdateEventDTO event, final Acknowledgment ack) {

        log.info("Evento DriverProfileUpdateEventDTO recebido para o driverId: {}", event.driverId());

        try {
            driverRedisRepository.updateRating(event.driverId(), event.rating());

            ack.acknowledge();
            log.info("Evento de avaliação para o driverId: {} processado com sucesso.", event.driverId());

        } catch (Exception e) {
            log.error("Erro ao processar o evento de avaliação para o driverId: {}. A mensagem será reenviada.", event.driverId(), e);
            throw new RuntimeException("Erro a processar evento de avaliação do motorista", e);
        }
    }

    @DltHandler
    public void handleDlt(final Object event) {
        log.error("[DLT] Mensagem recebida na Dead Letter Topic. Evento: {}", event);
    }
}