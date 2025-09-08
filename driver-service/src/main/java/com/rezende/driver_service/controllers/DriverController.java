package com.rezende.driver_service.controllers;

import com.rezende.driver_service.dto.DriverProfileResponse;
import com.rezende.driver_service.dto.OnboardDriverRequestDTO;
import com.rezende.driver_service.services.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(final DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<DriverProfileResponse> submitOnboarding(
            @RequestHeader("X-User-ID") final String userId,
            final OnboardDriverRequestDTO dto
    ) {
        final DriverProfileResponse response = driverService.submitOnboarding(userId, dto);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.userId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<DriverProfileResponse> findMyProfile(@RequestHeader("X-User-ID") final String userId) {
        final DriverProfileResponse response = driverService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/status")
    public ResponseEntity<DriverProfileResponse> approveDriver(@RequestHeader("X-User-ID") final String userId) {
        final DriverProfileResponse response = driverService.approveDriver(userId);
        return ResponseEntity.ok(response);
    }
}
