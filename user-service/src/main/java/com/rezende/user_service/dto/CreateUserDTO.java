package com.rezende.user_service.dto;

import com.rezende.user_service.entities.enums.RoleType;

public record CreateUserDTO(
        String name,
        String email,
        String password,
        RoleType roleType
) { }
