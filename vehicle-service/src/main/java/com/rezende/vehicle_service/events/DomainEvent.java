package com.rezende.vehicle_service.events;

import java.time.Instant;

public interface DomainEvent {

    String driverId();
    String vehicleId();
    Instant occurredAt();
}
