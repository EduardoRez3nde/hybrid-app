package com.rezende.driver_service.controllers;

import com.rezende.driver_service.dto.DriverProfileResponseDTO;
import com.rezende.driver_service.dto.UpdateApprovalStatusRequestDTO;
import com.rezende.driver_service.services.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/drivers")
public class AdminDriverController {

    private final DriverService driverService;

    public AdminDriverController(final DriverService driverService) {
        this.driverService = driverService;
    }

    @PatchMapping("/{driverId}/approval-status")
    public ResponseEntity<DriverProfileResponseDTO> updateApprovalStatus(
            @PathVariable final String driverId,
            @RequestBody final UpdateApprovalStatusRequestDTO dto
    ) {
        final DriverProfileResponseDTO response = driverService.updateApprovalStatus(driverId, dto);
        return ResponseEntity.ok(response);
    }
}
