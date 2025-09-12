package com.rezende.vehicle_service.events;

import java.time.Instant;

public record VehicleApprovedEvent(
        String vehicleId,
        String driverId,
        Instant occurredAt
) implements DomainEvent { }
