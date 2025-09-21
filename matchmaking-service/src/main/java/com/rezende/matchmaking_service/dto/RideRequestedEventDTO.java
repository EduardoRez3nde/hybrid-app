package com.rezende.matchmaking_service.dto;

import java.time.Instant;

public record RideRequestedEventDTO(
        String rideId,
        String passengerId,
        LocationDTO origin,
        LocationDTO destination,
        Instant eventTimestamp
) { }