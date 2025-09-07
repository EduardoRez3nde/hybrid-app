package com.rezende.driver_service.events;

import com.rezende.driver_service.enums.AccountStatus;
import com.rezende.driver_service.enums.RoleType;

import java.time.Instant;

public record UserRegisterEvent(
        String userId,
        String name,
        String email,
        RoleType roleType,
        AccountStatus accountStatus,
        Instant eventTimestamp
) { }
