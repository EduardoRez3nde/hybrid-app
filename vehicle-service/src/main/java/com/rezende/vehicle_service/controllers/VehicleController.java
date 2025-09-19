package com.rezende.vehicle_service.controllers;

import com.rezende.vehicle_service.dto.CreateVehicleRequestDTO;
import com.rezende.vehicle_service.dto.VehicleResponseDTO;
import com.rezende.vehicle_service.services.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(
            final VehicleService vehicleService
    ) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/register")
    public ResponseEntity<VehicleResponseDTO> register(
            @RequestHeader("X-User-ID") final String driverId,
            @RequestBody CreateVehicleRequestDTO dto
    ) {
        final VehicleResponseDTO response = vehicleService.register(driverId, dto);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/my-vehicles")
    public ResponseEntity<Page<VehicleResponseDTO>> findVehiclesByDriver(
            @RequestHeader("X-User-ID") final String driverId
    ) {
        final Page<VehicleResponseDTO> response = vehicleService.getVehiclesByDriver(driverId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("$/{vehicleId}/approve")
    public ResponseEntity<VehicleResponseDTO> approvedVehicle(@PathVariable final String vehicleId) {
        final VehicleResponseDTO response = vehicleService.approvedVehicle(vehicleId);
        return ResponseEntity.ok(response);
    }
}
