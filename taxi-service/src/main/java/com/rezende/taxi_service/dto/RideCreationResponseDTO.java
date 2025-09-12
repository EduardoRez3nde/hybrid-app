package com.rezende.taxi_service.dto;

import com.rezende.taxi_service.enums.RideStatus;

import java.time.Instant;

public record RideCreationResponseDTO(
        String rideId,
        String passengerId,
        RideStatus status,
        Instant requestedAt
) {}