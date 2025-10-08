package com.rezende.taxi_service.dto;

import com.rezende.taxi_service.event.DomainEvent;

import java.time.Instant;

public record RideRequestedEventDTO(
        String rideId,
        String passengerId,
        LocationDTO origin,
        LocationDTO destination,
        Instant eventTimestamp
) implements DomainEvent {
    @Override
    public String getAggregateId() {
        return rideId;
    }

    @Override
    public Instant getOccurredAt() {
        return eventTimestamp;
    }
}