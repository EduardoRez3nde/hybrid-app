package com.rezende.taxi_service.dto;

public record RequestRideDTO(
        LocationDTO origin,
        LocationDTO destination
) {}