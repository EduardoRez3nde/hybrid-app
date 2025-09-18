package com.rezende.driver_service.mapper;

import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.enums.OperationalStatus;
import com.rezende.driver_service.dto.UserRegisterEventDTO;

import java.util.UUID;

public class DriverProfileFactory {

    public static DriverProfile fromUserCreationEvent(final UserRegisterEventDTO event) {
        return DriverProfile.builder()
                .userId(UUID.fromString(event.userId()))
                .firstName(event.firstName())
                .lastName(event.lastName())
                .email(event.email())
                .hasApprovedVehicle(false)
                .approvalStatus(ApprovalStatus.PENDING_APPROVAL)
                .operationalStatus(OperationalStatus.OFFLINE)
                .build();
    }
}

