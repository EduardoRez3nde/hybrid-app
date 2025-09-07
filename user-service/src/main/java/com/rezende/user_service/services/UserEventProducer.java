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
    private final String topic;

    public UserEventProducer(
            @Value("${spring.kafka.topics.user-events}") final String topic,
            final KafkaTemplate<String, DomainEvent> kafkaTemplate
    ) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(final DomainEvent event) {

        final String key = event.userId();
        final CompletableFuture<SendResult<String, DomainEvent>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null){
                final RecordMetadata metadata = result.getRecordMetadata();
                log.info("Evento UserCreatedEvent enviado com sucesso para o tópico '{}', partição {}, offset {}. Chave: {}",
                        metadata.topic(), metadata.partition(), metadata.offset(), key);
            } else {
                log.error("Falha ao enviar UserCreatedEvent para o Kafka com a chave {}. Erro: {}",
                        key, ex.getMessage());
            }
        });
    }
}
