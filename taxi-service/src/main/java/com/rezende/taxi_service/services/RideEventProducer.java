package com.rezende.taxi_service.services;

import com.rezende.taxi_service.dto.RideRequestedEventDTO;
import com.rezende.taxi_service.event.*;
import com.rezende.taxi_service.mapper.RideEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RideEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final String rideRequestEventsTopic;
    private final String driverRatedTopic;
    private final String rideAcceptEvent;
    private final String rideRejectEvent;

    public RideEventProducer(
            @Value("${app.kafka.topics.ride-accept-event}") final String rideAcceptEvent,
            @Value("${app.kafka.topics.ride-reject-event}") final String rideRejectEvent,
            @Value("${app.kafka.topics.ride-request-event}") final String rideRequestEventsTopic,
            @Value("${app.kafka.topics.driver-rated-event}") String driverRatedTopic,
            final KafkaTemplate<String, DomainEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.rideRequestEventsTopic = rideRequestEventsTopic;
        this.driverRatedTopic = driverRatedTopic;
        this.rideAcceptEvent = rideAcceptEvent;
        this.rideRejectEvent = rideRejectEvent;
    }

    public void sendRideRequestedEvent(final RideRequestedEventDTO event) {
        sendEvent(event, rideRequestEventsTopic);
    }

    public void sendDriverRatedEvent(final DriverRatedEvent event) {
        sendEvent(event, driverRatedTopic);
    }

    public void sendRideAcceptedEvent(final RideAcceptedByDriverEvent event) {
        sendEvent(event, rideAcceptEvent);
    }

    public void sendRideRejectEvent(final RideRejectedByDriverEvent event) {
        sendEvent(event, rideRejectEvent);
    }

    private void sendEvent(final DomainEvent event, final String topic) {

        final String key = event.getAggregateId();
        final CompletableFuture<SendResult<String, DomainEvent>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                final RecordMetadata metadata = result.getRecordMetadata();
                log.info("Evento {} enviado com sucesso para o tópico '{}', partição {}, offset {}. Chave: {}",
                        event.getClass().getName(), metadata.topic(), metadata.partition(), metadata.offset(), key);
            } else {
                log.error("Falha ao enviar {} para o Kafka com a chave {}. Erro: {}", event.getClass().getName(), key, ex.getMessage());
            }
        });
    }
}
