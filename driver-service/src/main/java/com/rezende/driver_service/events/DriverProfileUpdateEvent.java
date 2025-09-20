package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;

import java.time.Instant;

public record DriverProfileUpdateEvent(
        String driverId,
        double rating,
        Instant occurredAt

) implements DomainEvent {

    public static DriverProfileUpdateEvent of(final DriverProfile driver) {
        return new DriverProfileUpdateEvent(
                driver.getUserId().toString(),
                driver.getAverageRating(),
                Instant.now()
        );
    }

    @Override
    public String userId() {
        return driverId;
    }
}
