package com.rezende.driver_service.mapper;

import com.rezende.driver_service.entities.DriverProfile;
import com.rezende.driver_service.enums.ApprovalStatus;
import com.rezende.driver_service.enums.OperationalStatus;
import com.rezende.driver_service.events.UserRegisterEvent;

import java.util.UUID;

public class DriverProfileFactory {

    public static DriverProfile fromUserCreationEvent(final UserRegisterEvent event) {
        return DriverProfile.builder()
                .userId(UUID.fromString(event.userId()))
                .name(event.name())
                .email(event.email())
                .approvalStatus(ApprovalStatus.PENDING_APPROVAL)
                .operationalStatus(OperationalStatus.OFFLINE)
                .build();
    }
}

