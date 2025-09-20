package com.rezende.taxi_service.dto;

public record RatingRequestDTO(
        String rideId,
        int rating,
        String comment
) { }
