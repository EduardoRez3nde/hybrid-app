package com.rezende.matchmaking_service.event;

import java.time.Instant;

public record NoDriverFoundEvent(
        String rideId,
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
