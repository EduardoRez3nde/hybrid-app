package com.rezende.notification_service.consumer;

import com.rezende.notification_service.dto.DeviceRegisteredEventDTO;
import com.rezende.notification_service.event.DriverAssignedEvent;
import com.rezende.notification_service.services.DeviceTokenService;
import com.rezende.notification_service.services.PushNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumerService {

    private final DeviceTokenService deviceTokenService;
    private final PushNotificationService pushNotificationService;

    public KafkaConsumerService(
            final DeviceTokenService deviceTokenService,
            final PushNotificationService pushNotificationService
    ) {
        this.deviceTokenService = deviceTokenService;
        this.pushNotificationService = pushNotificationService;
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.user-device-registrations}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleDeviceRegisteredEvent(@Payload final DeviceRegisteredEventDTO event, final Acknowledgment ack) {

        log.info("Recebido novo token de dispositivo para o utilizador {}", event.userId());

        try {
            deviceTokenService.registerOrUpdateToken(event);
            ack.acknowledge();
            log.info("Token para o utilizador {} guardado com sucesso.", event.userId());

        } catch (Exception e) {
            log.error("Erro ao guardar o token para o utilizador {}: {}", event.userId(), e.getMessage());
            throw new RuntimeException("Falha ao processar o registo do dispositivo", e);
        }
    }

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltTopicSuffix = ".DLT")
    @KafkaListener(
            topics = "${app.kafka.topics.driver-assigned-ride-event}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleDriverAssignedEvent(@Payload final DriverAssignedEvent event, final Acknowledgment ack) {

        log.info("Recebido evento de atribuição para a corrida {}. Notificando o motorista {}",
                event.rideId(), event.driverId());
        try {
            String title = "Você tem uma nova Corrida!";
            String body = "Você tem uma nova corrida. Toque para ver os detalhes e aceitar.";

            pushNotificationService.sendNotificationToUser(event.driverId(), title, body);
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Falha ao processar evento de notificação para o motorista {}", event.driverId(), e);
            throw new RuntimeException("Falha ao notificar motorista", e);
        }
    }
}
