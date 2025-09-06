package com.rezende.vehicle_service.dto;

import com.rezende.vehicle_service.enums.VehicleType;

public record CreateVehicleRequestDTO(
        String plate,
        String make,
        String model,
        String color,
        Integer year,
        VehicleType type
) {
    public static CreateVehicleRequestDTO form(
            final String plate,
            final String make,
            final String model,
            final String color,
            final Integer year,
            final VehicleType type
    ) {
        return new CreateVehicleRequestDTO(plate, make, model, color, year, type);
    }
}
