package com.rezende.user_service.services;

import com.rezende.user_service.events.DomainEvent;
import com.rezende.user_service.events.UserRegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final String userEvents;
    private final String userDeviceRegistrations;

    public UserEventProducer(
            @Value("${app.kafka.topics.user-events}") final String userEvents,
            @Value("${app.kafka.topics.user-device-registrations}") final String userDeviceRegistrations,
            final KafkaTemplate<String, DomainEvent> kafkaTemplate
    ) {
        this.userEvents = userEvents;
        this.userDeviceRegistrations = userDeviceRegistrations;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(final DomainEvent event) {
        sendEvent(event, userEvents);
    }

    public void sendRegisterDevice(final DomainEvent event) {
        sendEvent(event, userDeviceRegistrations);
    }

    public void sendEvent(final DomainEvent event, final String topic) {

        final String key = event.userId();
        final CompletableFuture<SendResult<String, DomainEvent>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null){
                final RecordMetadata metadata = result.getRecordMetadata();
                log.info("Evento {} enviado com sucesso para o tópico '{}', partição {}, offset {}. Chave: {}",
                        event.getClass().getName(), metadata.topic(), metadata.partition(), metadata.offset(), key);
            } else {
                log.error("Falha ao enviar {} para o Kafka com a chave {}. Erro: {}",
                        event.getClass().getName(), key, ex.getMessage());
            }
        });
    }
}
