package com.rezende.matchmaking_service.dto;

public record ActiveDriverDTO(
        String id,
        double longitude,
        double latitude,
        double rating,
        String vehicleType
) {
    public static ActiveDriverDTO of(final DriverStatusUpdateEvent event) {
        return new ActiveDriverDTO(
                event.driverId(),
                event.longitude(),
                event.latitude(),
                event.rating(),
                event.vehicleType()
        );
    }
}
