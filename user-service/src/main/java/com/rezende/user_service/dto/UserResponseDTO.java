package com.rezende.user_service.dto;

import com.rezende.user_service.entities.User;
import com.rezende.user_service.entities.enums.RoleType;

public record UserResponseDTO(
        String id,
        String name,
        String email,
        RoleType roleType
) {
    public static UserResponseDTO from(final String id, final String name, final String email, final RoleType roleType) {
        return new UserResponseDTO(id, name, email, roleType);
    }

    public static UserResponseDTO of(final User user) {
        return UserResponseDTO.from(
                String.valueOf(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getRoleType()
        );
    }
}
