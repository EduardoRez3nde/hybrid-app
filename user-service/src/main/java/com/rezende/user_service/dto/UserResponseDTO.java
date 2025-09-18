package com.rezende.user_service.dto;

import com.rezende.user_service.entities.User;
import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.enums.RoleType;

public record UserResponseDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        RoleType roleType,
        AccountStatus accountStatus
) {
    public static UserResponseDTO from(
            final String id,
            final String firstName,
            final String lastName,
            final String email,
            final RoleType roleType,
            AccountStatus accountStatus

    ) {
        return new UserResponseDTO(id, firstName, lastName, email, roleType, accountStatus);
    }

    public static UserResponseDTO of(final User user) {
        return UserResponseDTO.from(
                String.valueOf(user.getId()),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoleType(),
                user.getAccountStatus()
        );
    }
}
