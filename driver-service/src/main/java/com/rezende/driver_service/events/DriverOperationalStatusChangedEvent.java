package com.rezende.driver_service.events;

import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.OperationalStatus;

import java.time.Instant;

public record DriverOperationalStatusChangedEvent(
        String driverId,
        OperationalStatus newStatus,
        Instant occurredAt
) implements DomainEvent{

    public static DriverOperationalStatusChangedEvent of(final DriverProfile driver) {
        return new DriverOperationalStatusChangedEvent(
                String.valueOf(driver.getUserId()),
                driver.getOperationalStatus(),
                Instant.now()
        );
    }

    @Override
    public String userId() {
        return driverId;
    }

    @Override
    public Instant occurredAt() {
        return this.occurredAt;
    }
}
