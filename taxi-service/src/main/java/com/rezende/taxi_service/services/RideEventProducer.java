package com.rezende.taxi_service.services;

import com.rezende.taxi_service.event.DomainEvent;
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
    private final String topic;

    public RideEventProducer(
            final KafkaTemplate<String, DomainEvent> kafkaTemplate,
            @Value("${app.kafka.topics.ride-events}") final String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendRideRequestedEvent(final RideRequestedEvent event) {

        final String key = event.rideId().toString();
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
