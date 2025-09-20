package com.rezende.taxi_service.event;

import com.rezende.taxi_service.dto.RatingRequestDTO;
import com.rezende.taxi_service.entities.Ride;

import java.time.Instant;

public record DriverRatedEvent(
        String driverId,
        String rideId,
        int rating,
        String comment,
        Instant occurredAt
) implements DomainEvent {

    public static DriverRatedEvent of(final Ride ride, final RatingRequestDTO dto) {
        return new DriverRatedEvent(
                ride.getDriverId().toString(),
                dto.rideId(),
                dto.rating(),
                dto.comment(),
                Instant.now()
        );
    }

    @Override
    public String getAggregateId() {
        return driverId;
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }
}
