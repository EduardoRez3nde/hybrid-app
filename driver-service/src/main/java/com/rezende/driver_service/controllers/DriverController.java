package com.rezende.driver_service.controllers;

import com.rezende.driver_service.dto.AccountStatusRequestDTO;
import com.rezende.driver_service.dto.CoordinatesDTO;
import com.rezende.driver_service.dto.DriverProfileResponseDTO;
import com.rezende.driver_service.dto.OnboardDriverRequestDTO;
import com.rezende.driver_service.services.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(final DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<DriverProfileResponseDTO> submitOnboarding(
            @RequestHeader("X-User-ID") final String userId,
            @RequestBody final OnboardDriverRequestDTO dto
    ) {
        final DriverProfileResponseDTO response = driverService.submitOnboarding(userId, dto);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.userId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<DriverProfileResponseDTO> findMyProfile(@RequestHeader("X-User-ID") final String userId) {
        final DriverProfileResponseDTO response = driverService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/status")
    public ResponseEntity<DriverProfileResponseDTO> updateOperationalStatus(
            @RequestHeader("X-User-ID") final String userId,
            @RequestBody final AccountStatusRequestDTO dto
    ) {
        final DriverProfileResponseDTO response = driverService.changeStatus(userId, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{driverId}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable final String driverId,
            @RequestBody final CoordinatesDTO coordinates
    ) {
        driverService.updateLocation(driverId, coordinates);
        return ResponseEntity.noContent().build();
    }
}
