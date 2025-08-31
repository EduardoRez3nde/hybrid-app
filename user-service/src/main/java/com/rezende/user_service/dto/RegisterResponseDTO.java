package com.rezende.user_service.dto;

import com.rezende.user_service.entities.User;
import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.enums.RoleType;

public record RegisterResponseDTO(
        String id,
        String name,
        String email,
        RoleType roleType,
        AccountStatus accountStatus
) {
    public static RegisterResponseDTO from(
            final String id,
            final String name,
            final String email,
            final RoleType roleType,
            AccountStatus accountStatus

    ) {
        return new RegisterResponseDTO(id, name, email, roleType, accountStatus);
    }

    public static RegisterResponseDTO of(final User user) {
        return RegisterResponseDTO.from(
                String.valueOf(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getRoleType(),
                user.getAccountStatus()
        );
    }
}
