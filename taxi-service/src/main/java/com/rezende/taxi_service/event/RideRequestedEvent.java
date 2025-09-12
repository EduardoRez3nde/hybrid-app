package com.rezende.taxi_service.event;

import com.rezende.taxi_service.entities.Location;

import java.time.Instant;

public record RideRequestedEvent(
        String rideId,
        String passengerId,
        Location origin,
        Location destination,
        Instant eventTimestamp
) implements DomainEvent {

    @Override
    public String getAggregateId() {
        return this.rideId;
    }

    @Override
    public Instant getOccurredAt() {
        return this.eventTimestamp;
    }
}