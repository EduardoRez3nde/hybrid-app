package com.rezende.notification_service.services;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.rezende.notification_service.entities.DeviceToken;
import com.rezende.notification_service.repositories.DeviceTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class PushNotificationService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final ExecutorService executorService;

    public PushNotificationService(
            final DeviceTokenRepository deviceTokenRepository,
            final ExecutorService notificationExecutorService
    ) {
        this.deviceTokenRepository = deviceTokenRepository;
        this.executorService = notificationExecutorService;
    }

    public void sendNotificationToUser(final String userId, final String title, final String body) {

        final List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByUserId(UUID.fromString(userId));

        if (deviceTokens.isEmpty()) {
            log.warn("Nenhum token de dispositivo encontrado para o utilizador {}", userId);
            return;
        }
        deviceTokens.forEach(deviceToken -> executorService.submit(() -> sendNotificationAsync(deviceToken, title, body)));
    }

    /**
     * Envia a notificação de forma assíncrona usando o metodo sendAsync do Firebase
     * e converte o resultado para um CompletableFuture.
     */
    private CompletableFuture<String> sendNotificationAsync(final DeviceToken deviceToken, final String title, final String body) {

        final Message message = buildMessage(deviceToken, title, body);

        final ApiFuture<String> apiFuture = FirebaseMessaging.getInstance().sendAsync(message);

        final CompletableFuture<String> completableFuture = new CompletableFuture<>();

        ApiFutures.addCallback(apiFuture, new ApiFutureCallback<String>() {
            @Override
            public void onSuccess(String response) {
                log.info("Notificação enviada com sucesso para o token {}: {}", deviceToken.getToken(), response);
                completableFuture.complete(response);
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("Erro ao enviar notificação para o token {}: {}", deviceToken.getToken(), t.getMessage());
                completableFuture.completeExceptionally(t);
                // TODO: Lógica para remover tokens inválidos
            }
        }, MoreExecutors.directExecutor());

        return completableFuture;
    }

    private Message buildMessage(final DeviceToken deviceToken, final String title, final String body) {
        final Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        return Message.builder()
                .setNotification(notification)
                .setToken(deviceToken.getToken())
                .build();
    }
}
