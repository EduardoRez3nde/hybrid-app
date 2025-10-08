package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.OperationalStatus;

import java.time.Instant;

public record DriverStatusUpdateEvent(
        String driverId,
        OperationalStatus newStatus,
        double rating,
        Instant eventTimestamp,
        double latitude,
        double longitude
) implements DomainEvent {

    public static DriverStatusUpdateEvent of(final DriverProfile driver) {
        return new DriverStatusUpdateEvent(
                driver.getUserId().toString(),
                driver.getOperationalStatus(),
                driver.getAverageRating(),
                Instant.now(),
                driver.getCurrentLocation().getY(),
                driver.getCurrentLocation().getX()
        );
    }

    @Override
    public String userId() {
        return driverId;
    }

    @Override
    public Instant occurredAt() {
        return this.eventTimestamp;
    }
}
