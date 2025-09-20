package com.rezende.driver_service.dto;

import com.rezende.driver_service.events.DomainEvent;

import java.time.Instant;

public record DriverRatedEventDTO(
        String driverId,
        String rideId,
        int rating,
        String comment,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String userId() {
        return driverId;
    }
}
