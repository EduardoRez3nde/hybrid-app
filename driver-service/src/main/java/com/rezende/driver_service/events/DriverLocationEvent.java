package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;

import java.time.Instant;

public record DriverLocationEvent(
        String driverId,
        double latitude,
        double longitude,
        Instant occurredAt
) implements DomainEvent {

    public static DriverLocationEvent of(final DriverProfile driver) {
        return new DriverLocationEvent(
                driver.getUserId().toString(),
                driver.getCurrentLocation().getY(),
                driver.getCurrentLocation().getX(),
                Instant.now()
        );
    }

    @Override
    public String userId() {
        return driverId;
    }
}
