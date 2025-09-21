package com.rezende.matchmaking_service.dto;

import com.rezende.matchmaking_service.event.DomainEvent;

import java.time.Instant;

public record DriverLocationUpdatesEvent(
        String driverId,
        double latitude,
        double longitude,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String getAggregateId() {
        return driverId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}