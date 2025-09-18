package com.rezende.taxi_service.controllers;

import com.rezende.taxi_service.dto.RequestRideDTO;
import com.rezende.taxi_service.dto.RideCreationResponseDTO;
import com.rezende.taxi_service.services.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taxi/ride")
public class RideController {

    private final RideService rideService;

    public RideController(final RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideCreationResponseDTO> requestRide(
            @RequestBody final RequestRideDTO dto,
            @RequestHeader("X-User-ID") final String passengerId
    ) {
        final RideCreationResponseDTO response = rideService.requestRide(passengerId, dto);
        return ResponseEntity.ok(response);
    }
}