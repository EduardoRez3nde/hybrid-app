package com.rezende.vehicle_service.controllers;

import com.rezende.vehicle_service.dto.VehicleResponseDTO;
import com.rezende.vehicle_service.services.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/vehicle")
public class AdminController {

    private final VehicleService vehicleService;

    public AdminController(
            final VehicleService vehicleService
    ) {
        this.vehicleService = vehicleService;
    }

    @PatchMapping("/{vehicleId}/approve")
    public ResponseEntity<VehicleResponseDTO> approvedVehicle(@PathVariable final String vehicleId) {
        final VehicleResponseDTO response = vehicleService.approvedVehicle(vehicleId);
        return ResponseEntity.ok(response);
    }
}
