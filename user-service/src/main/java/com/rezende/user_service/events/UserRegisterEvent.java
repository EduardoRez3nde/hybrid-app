package com.rezende.user_service.events;

import com.rezende.user_service.enums.RoleType;

import java.time.Instant;

public record UserRegisterEvent(
        String userId,
        String name,
        String email,
        RoleType roleType,
        Instant eventTimestamp
) { }
