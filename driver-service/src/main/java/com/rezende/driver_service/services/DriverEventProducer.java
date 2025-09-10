package com.rezende.driver_service.services;

import com.rezende.driver_service.events.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class DriverEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final String driverOperationalStatus;
    private final String driverLifecycle;

    public DriverEventProducer(
            @Value("${app.kafka.topics.driver-operational-status}") final String driverOperationalStatus,
            @Value("${app.kafka.topics.driver-lifecycle}") final String driverLifecycle,
            final KafkaTemplate<String, DomainEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.driverLifecycle = driverLifecycle;
        this.driverOperationalStatus = driverOperationalStatus;
    }

    public void sendDriverOnboardingSubmittedEvent(final DriverOnboardingSubmittedEvent event) {
        sendEvent(event, driverLifecycle);
    }

    public void sendDriverApprovedEvent(final DriverApprovedEvent event) {
        sendEvent(event, driverLifecycle);
    }

    public void sendDriverRejectedEvent(final DriverRejectedEvent event) {
        sendEvent(event, driverLifecycle);
    }

    public void sendOperationalStatusChanged(final DriverOperationalStatusChangedEvent event) {
        sendEvent(event, driverOperationalStatus);
    }

    private void sendEvent(final DomainEvent event, final String topic) {

        final String key = event.userId();

        final CompletableFuture<SendResult<String, DomainEvent>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                final RecordMetadata metadata = result.getRecordMetadata();
                log.info("Evento {} foi enviado com sucesso para o topico '{}', partição {}, offset {}. Chave: {}",
                        event, metadata.topic(), metadata.partition(), metadata.offset(), key);
            } else {
                log.error("Falha ao enviar o evento {} com chave {}. Erro: {}", event, key, ex.getMessage());
            }
        });
    }
}

