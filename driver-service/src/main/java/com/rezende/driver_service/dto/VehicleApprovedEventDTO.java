package com.rezende.driver_service.dto;

import java.time.Instant;

public record VehicleApprovedEventDTO(
        String vehicleId,
        String driverId,
        Instant occurredAt
) { }
