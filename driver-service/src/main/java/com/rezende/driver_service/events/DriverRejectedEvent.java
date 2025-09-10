package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;

import java.time.Instant;

public record DriverRejectedEvent(
        String userId,
        Instant occurredAt
) implements DomainEvent{

    public static DriverRejectedEvent of(final DriverProfile driver) {
        return new DriverRejectedEvent(String.valueOf(driver.getUserId()), Instant.now());
    }

    @Override
    public Instant occurredAt() {
        return this.occurredAt;
    }
}
