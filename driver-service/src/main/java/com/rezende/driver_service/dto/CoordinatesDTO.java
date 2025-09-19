package com.rezende.driver_service.dto;

import com.rezende.driver_service.entities.DriverProfile;

public record CoordinatesDTO(
        double latitude,
        double longitude
) {
    public static CoordinatesDTO of(final DriverProfile driver) {
        return new CoordinatesDTO(
                driver.getCurrentLocation().getY(),
                driver.getCurrentLocation().getX());
    }
}
