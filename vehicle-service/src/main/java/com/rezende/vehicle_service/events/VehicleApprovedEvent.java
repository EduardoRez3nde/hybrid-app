package com.rezende.vehicle_service.events;

import com.rezende.vehicle_service.entity.Vehicle;

import java.time.Instant;

public record VehicleApprovedEvent(
        String vehicleId,
        String driverId,
        String vehicleType,
        Instant eventTimestamp
) implements DomainEvent {

    public static VehicleApprovedEvent of(final Vehicle vehicle) {
        return new VehicleApprovedEvent(
                vehicle.getId().toString(),
                vehicle.getDriverId(),
                vehicle.getType().toString(),
                Instant.now()
        );
    }

    @Override
    public Instant occurredAt() {
        return eventTimestamp;
    }
}