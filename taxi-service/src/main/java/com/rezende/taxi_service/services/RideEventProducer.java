package com.rezende.taxi_service.services;

import com.rezende.taxi_service.event.DomainEvent;
import com.rezende.taxi_service.event.DriverRatedEvent;
import com.rezende.taxi_service.event.RideRequestedEvent;
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
    private final String rideEventsTopic;
    private final String driverRatedTopic;

    public RideEventProducer(
            final KafkaTemplate<String, DomainEvent> kafkaTemplate,
            @Value("${app.kafka.topics.ride-events}") final String rideEventsTopic,
            @Value("${app.kafka.topics.driver-rated}") String driverRatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.rideEventsTopic = rideEventsTopic;
        this.driverRatedTopic = driverRatedTopic;
    }

    public void sendRideRequestedEvent(final RideRequestedEvent event) {
        sendEvent(event, rideEventsTopic);
    }

    public void sendDriverRatedEvent(final DriverRatedEvent event) {
        sendEvent(event, driverRatedTopic);
    }

    private void sendEvent(final DomainEvent event, final String topic) {

        final String key = event.getAggregateId();
        final CompletableFuture<SendResult<String, DomainEvent>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                final RecordMetadata metadata = result.getRecordMetadata();
                log.info("Evento RideRequestedEvent enviado com sucesso para o tópico '{}', partição {}, offset {}. Chave: {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), key);
            } else {
                log.error("Falha ao enviar RideRequestedEvent para o Kafka com a chave {}. Erro: {}", key, ex.getMessage());
            }
        });
    }
}
