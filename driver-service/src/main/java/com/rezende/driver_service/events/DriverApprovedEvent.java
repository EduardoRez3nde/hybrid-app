package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;

import java.time.Instant;

public record DriverApprovedEvent(
        String userId,
        Instant occurredAt
) implements DomainEvent{

    public static DriverApprovedEvent of(final DriverProfile driver) {
        return new DriverApprovedEvent(String.valueOf(driver.getUserId()), driver.getCreatedAt());
    }

    @Override
    public Instant occurredAt() {
        return this.occurredAt;
    }
}
