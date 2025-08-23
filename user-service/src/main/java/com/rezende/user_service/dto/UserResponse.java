package com.rezende.user_service.dto;

import com.rezende.user_service.entities.enums.RoleType;

public record UserResponse(
        String name,
        String email,
        RoleType roleType
) { }
