package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;

import java.time.Instant;

public record DriverOnboardingSubmittedEvent(
        String userId,
        String cnhNumber,
        Instant submittedAt
) implements DomainEvent {
    public static DriverOnboardingSubmittedEvent of(final DriverProfile driver) {
        return new DriverOnboardingSubmittedEvent(String.valueOf(driver.getUserId()), driver.getCnhNumber(), Instant.now());
    }

    @Override
    public Instant occurredAt() {
        return submittedAt;
    }
}
