package com.rezende.matchmaking_service.services;

import com.rezende.matchmaking_service.event.DomainEvent;
import com.rezende.matchmaking_service.event.DriverAssignedToRideEvent;
import com.rezende.matchmaking_service.event.NoDriverFoundEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class MatchmakingEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final String driverAssignedRideTopicName;
    private final String noDriverFoundTopicName;

    public MatchmakingEventProducer(
            @Value("${app.kafka.topics.driver-assigned-ride-event}") final String driverAssignedRideTopicName,
            @Value("${app.kafka.topics.no-driver-found-event}") final String noDriverFoundTopicName,
            final KafkaTemplate<String, DomainEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.driverAssignedRideTopicName = driverAssignedRideTopicName;
        this.noDriverFoundTopicName = noDriverFoundTopicName;
    }

    public void sendDriverAssignedRideEvent(final DriverAssignedToRideEvent event) {
        sendEvent(event, driverAssignedRideTopicName);
    }

    public void sendNoDriverFoundEvent(final NoDriverFoundEvent event) {
        sendEvent(event, noDriverFoundTopicName);
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
