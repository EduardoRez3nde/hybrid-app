package com.rezende.matchmaking_service.dto;

import java.time.Instant;

public record DriverProfileUpdateEventDTO(
        String driverId,
        double rating,
        Instant occurredAt

) implements DomainEvent {

    @Override
    public String userId() {
        return driverId;
    }
}
