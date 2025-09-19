package com.rezende.vehicle_service.services;

import com.rezende.vehicle_service.events.DomainEvent;
import com.rezende.vehicle_service.events.VehicleApprovedEvent;
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
    private final String approvedVehicleTopic;

    public VehicleEventProducer(
            @Value("${app.kafka.topics.approved-vehicle}") final String approvedVehicleTopic,
            final KafkaTemplate<String, DomainEvent> kafkaTemplate
    ) {
        this.approvedVehicleTopic = approvedVehicleTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendVehicleApprovedEvent(final VehicleApprovedEvent event) {
        sendEvent(event, approvedVehicleTopic);
    }

    private void sendEvent(final DomainEvent event, final String topic) {

        final String key = event.vehicleId();

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