package com.rezende.taxi_service.controllers;

import com.rezende.taxi_service.dto.RatingRequestDTO;
import com.rezende.taxi_service.dto.RequestRideDTO;
import com.rezende.taxi_service.dto.RideCreationResponseDTO;
import com.rezende.taxi_service.services.RatingService;
import com.rezende.taxi_service.services.RideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/taxi/ride")
public class RideController {

    private final RideService rideService;
    private final RatingService ratingService;

    public RideController(
            final RideService rideService,
            final RatingService ratingService
    ) {
        this.rideService = rideService;
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RideCreationResponseDTO> requestRide(
            @RequestBody final RequestRideDTO dto,
            @RequestHeader("X-User-ID") final String passengerId
    ) {
        final RideCreationResponseDTO response = rideService.requestRide(passengerId, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{rideId}/accept")
    public ResponseEntity<Void> acceptRide(
            @RequestHeader("X-User-ID") final String driverId,
            @PathVariable final UUID rideId
    ) {
        rideService.acceptRide(UUID.fromString(driverId), rideId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{rideId}/reject")
    public ResponseEntity<Void> rejectRide(
            @RequestHeader("X-User-ID") final String driverId,
            @PathVariable final UUID rideId
    ) {
        rideService.rejectRide(UUID.fromString(driverId), rideId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{rideId}/rate")
    public ResponseEntity<Void> submitRating(
            @RequestHeader("X-User-ID") final String passengerId,
            @PathVariable("rideId") final String rideId,
            @RequestBody final RatingRequestDTO dto
    ) {
        ratingService.submitRating(passengerId, rideId, dto);
        return ResponseEntity.noContent().build();
    }
}