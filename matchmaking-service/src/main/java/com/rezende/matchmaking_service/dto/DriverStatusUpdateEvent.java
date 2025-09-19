package com.rezende.matchmaking_service.dto;


import com.rezende.matchmaking_service.enums.OperationalStatus;

import java.time.Instant;

public record DriverStatusUpdateEvent(
        String driverId,
        double latitude,
        double longitude,
        double rating,
        String vehicleType,
        OperationalStatus newStatus,
        Instant eventTimestamp
) { }