package com.rezende.taxi_service.event;

import com.rezende.taxi_service.entities.Location;

import java.time.Instant;
import java.util.UUID;

public record RideRequestedEvent(
        UUID rideId,
        UUID passengerId,
        Location origin,
        Location destination,
        Instant eventTimestamp
) implements DomainEvent {

    @Override
    public String getAggregateId() {
        return this.rideId.toString();
    }

    @Override
    public Instant getOccurredAt() {
        return this.eventTimestamp;
    }
}