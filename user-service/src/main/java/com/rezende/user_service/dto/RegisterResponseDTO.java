package com.rezende.user_service.dto;

import com.rezende.user_service.entities.User;
import com.rezende.user_service.entities.enums.RoleType;

public record RegisterResponseDTO(
        String id,
        String name,
        String email,
        RoleType roleType
) {
    public static RegisterResponseDTO from(
            final String id,
            final String name,
            final String email,
            final RoleType roleType

    ) {
        return new RegisterResponseDTO(id, name, email, roleType);
    }

    public static RegisterResponseDTO of(final User user) {
        return RegisterResponseDTO.from(
                String.valueOf(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getRoleType()
        );
    }
}
