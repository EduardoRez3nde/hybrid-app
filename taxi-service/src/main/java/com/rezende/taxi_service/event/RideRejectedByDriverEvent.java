package com.rezende.taxi_service.event;

import java.time.Instant;

public record RideRejectedByDriverEvent(
        String rideId,
        String driverId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String getAggregateId() {
        return rideId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}
