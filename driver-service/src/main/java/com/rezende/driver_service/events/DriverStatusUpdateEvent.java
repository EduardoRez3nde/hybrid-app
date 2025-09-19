package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.OperationalStatus;

import java.time.Instant;

public record DriverStatusUpdateEvent(
        String driverId,
        OperationalStatus newStatus,
        Instant eventTimestamp,
        double latitude,
        double longitude,
        String vehicleType
) implements DomainEvent {

    public static DriverStatusUpdateEvent of(final DriverProfile driver) {
        return new DriverStatusUpdateEvent(
                driver.getUserId().toString(),
                driver.getOperationalStatus(),
                Instant.now(),
                driver.getCurrentLocation().getX(),
                driver.getCurrentLocation().getY(),
                null
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
