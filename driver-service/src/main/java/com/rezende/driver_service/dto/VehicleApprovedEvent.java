package com.rezende.driver_service.dto;

import com.rezende.driver_service.events.DomainEvent;
import com.rezende.vehicle_service.entity.Vehicle;

import java.time.Instant;

public record VehicleApprovedEvent(
        String vehicleId,
        String driverId,
        String vehicleType,
        Instant eventTimestamp
) implements DomainEvent {

    @Override
    public String userId() {
        return driverId;
    }

    @Override
    public Instant occurredAt() {
        return eventTimestamp;
    }
}