package com.rezende.user_service.events;

import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.enums.RoleType;

import java.time.Instant;

public record UserRegisterEvent(
        String userId,
        String firstName,
        String lastName,
        String email,
        RoleType roleType,
        AccountStatus accountStatus,
        Instant occurredAt
) implements DomainEvent {

    public static UserRegisterEvent of(final User user) {
        return new UserRegisterEvent(
                user.getId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoleType(),
                user.getAccountStatus(),
                Instant.now()
        );
    }

    public static UserRegisterEvent of(final UserResponseDTO user) {
        return new UserRegisterEvent(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.email(),
                user.roleType(),
                user.accountStatus(),
                Instant.now()
        );
    }
}
