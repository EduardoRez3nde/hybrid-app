package com.rezende.vehicle_service.dto;

import com.rezende.vehicle_service.entity.Vehicle;
import com.rezende.vehicle_service.enums.VehicleStatus;
import com.rezende.vehicle_service.enums.VehicleType;

public record VehicleResponseDTO(
        String id,
        String driverId,
        String plate,
        String make,
        String model,
        String color,
        Integer year,
        VehicleType type,
        VehicleStatus status
) {
    public static VehicleResponseDTO of(final Vehicle vehicle) {
        return new VehicleResponseDTO(
                String.valueOf(vehicle.getId()),
                vehicle.getDriverId().toString(),
                vehicle.getPlate(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getYear(),
                vehicle.getType(),
                vehicle.getStatus()
        );
    }
}
