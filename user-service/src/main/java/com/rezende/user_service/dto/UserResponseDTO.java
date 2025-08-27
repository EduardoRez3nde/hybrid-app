package com.rezende.user_service.dto;

import com.rezende.user_service.entities.User;
import com.rezende.user_service.entities.enums.AccountStatus;
import com.rezende.user_service.entities.enums.RoleType;

public record UserResponseDTO(
        String id,
        String name,
        String email,
        RoleType roleType,
        AccountStatus accountStatus
) {
    public static UserResponseDTO from(
            final String id,
            final String name,
            final String email,
            final RoleType roleType,
            final AccountStatus accountStatus
    ) {
        return new UserResponseDTO(id, name, email, roleType, accountStatus);
    }

    public static UserResponseDTO of(final User user) {
        return UserResponseDTO.from(
                String.valueOf(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getRoleType(),
                user.getAccountStatus()
        );
    }
}
