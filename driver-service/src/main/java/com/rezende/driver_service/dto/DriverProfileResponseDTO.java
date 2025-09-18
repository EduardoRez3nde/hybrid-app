package com.rezende.driver_service.dto;

import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.enums.OperationalStatus;

import java.time.Instant;

public record DriverProfileResponseDTO(
        String userId,
        String firsName,
        String lastName,
        String email,
        String cnhNumber,
        ApprovalStatus approvalStatus,
        OperationalStatus operationalStatus,
        boolean hasApprovedVehicle,
        Instant createdAt
) {
    public static DriverProfileResponseDTO of(final DriverProfile driver) {
        return new DriverProfileResponseDTO(
                String.valueOf(driver.getUserId()),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getEmail(),
                driver.getCnhNumber(),
                driver.getApprovalStatus(),
                driver.getOperationalStatus(),
                driver.isHasApprovedVehicle(),
                driver.getCreatedAt()
        );
    }
}
