package com.rezende.user_service.services;

import com.rezende.user_service.events.DomainEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventProducerTests {

    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Mock
    private SendResult<String, DomainEvent> sendResult;

    private UserEventProducer userEventProducer;
    private final String topic = "user.events.v1";

    @BeforeEach
    void setup() {
        userEventProducer = new UserEventProducer(topic, kafkaTemplate);
    }

    @Test
    @DisplayName("Deve enviar evento com sucesso para o Kafka")
    void shouldSendUserCreatedEventSuccessfully() {
        final DomainEvent event = mock(DomainEvent.class);
        when(event.userId()).thenReturn("123");

        final CompletableFuture<SendResult<String, DomainEvent>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(eq(topic), eq("123"), eq(event))).thenReturn(future);

        userEventProducer.sendUserCreatedEvent(event);

        verify(kafkaTemplate, times(1)).send(topic, "123", event);
    }

    @Test
    @DisplayName("Deve lidar com falha no envio do evento")
    void shouldHandleSendFailureGracefully() {
        final DomainEvent event = mock(DomainEvent.class);
        when(event.userId()).thenReturn("123");

        final CompletableFuture<SendResult<String, DomainEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka down"));

        when(kafkaTemplate.send(anyString(), anyString(), any(DomainEvent.class)))
                .thenReturn(future);

        assertDoesNotThrow(() -> userEventProducer.sendUserCreatedEvent(event));

        verify(kafkaTemplate, times(1)).send(topic, "123", event);
    }
}

