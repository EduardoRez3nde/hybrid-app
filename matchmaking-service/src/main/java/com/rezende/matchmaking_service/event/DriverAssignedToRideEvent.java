package com.rezende.matchmaking_service.event;

import com.rezende.matchmaking_service.dto.RideRequestedEventDTO;

import java.time.Instant;

public record DriverAssignedToRideEvent(
        String rideId,
        String driverId,
        Instant assignedAt
) implements DomainEvent {

    @Override
    public String getAggregateId() {
        return driverId;
    }

    @Override
    public Instant getOccurredAt() {
        return assignedAt;
    }
}