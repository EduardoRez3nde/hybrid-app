package com.rezende.user_service.events;

import java.time.Instant;

public record DeviceRegisteredEvent(
        String userId,
        String deviceToken,
        Instant registeredAt
) implements DomainEvent {


    @Override
    public Instant occurredAt() {
        return registeredAt;
    }
}