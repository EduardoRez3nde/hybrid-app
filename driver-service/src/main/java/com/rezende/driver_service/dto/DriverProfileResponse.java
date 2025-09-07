package com.rezende.driver_service.dto;

import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.enums.OperationalStatus;

import java.time.Instant;

public record DriverProfileResponse(
        String userId,
        String name,
        String email,
        String cnhNumber,
        ApprovalStatus approvalStatus,
        OperationalStatus operationalStatus,
        Instant createdAt
) {
    public static DriverProfileResponse of(final DriverProfile driver) {
        return new DriverProfileResponse(
                String.valueOf(driver.getUserId()),
                driver.getName(),
                driver.getEmail(),
                driver.getCnhNumber(),
                driver.getApprovalStatus(),
                driver.getOperationalStatus(),
                driver.getCreatedAt()
        );
    }
}
