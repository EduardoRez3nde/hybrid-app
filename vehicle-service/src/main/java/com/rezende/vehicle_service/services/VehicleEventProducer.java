package com.rezende.vehicle_service.services;

import com.rezende.vehicle_service.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class VehicleEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final String topic;

    public VehicleEventProducer(
            @Value("${app.kafka.topics.vehicle-events}") final String topic,
            final KafkaTemplate<String, DomainEvent> kafkaTemplate
    ) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(final DomainEvent event) {

        final String key = event.vehicleId();
        final CompletableFuture<SendResult<String, DomainEvent>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {

            final String eventType = event.getClass().getSimpleName();

            if (ex == null){
                final RecordMetadata metadata = result.getRecordMetadata();
                log.info("Evento '{}' enviado com sucesso para o tópico '{}', partição {}, offset {}. Chave: {}",
                        eventType, metadata.topic(), metadata.partition(), metadata.offset(), key);
            } else {
                log.error("Falha ao enviar evento '{}' para o Kafka com a chave {}. Erro: {}",
                        eventType, key, ex.getMessage());
            }
        });
    }
}