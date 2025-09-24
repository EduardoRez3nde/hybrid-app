package com.rezende.taxi_service.dto;

import java.time.Instant;

public record DriverAssignedToRideEventDTO(
        String rideId,
        String driverId,
        Instant assignedAt
) { }