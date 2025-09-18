package com.rezende.user_service.dto;

import java.util.List;

public record UserPayloadDTO(
        String username,
        String email,
        boolean enabled,
        String firstName,
        List<CredentialsDTO> credentials
) {
    public static UserPayloadDTO of(final RegisterUser user) {
        return new UserPayloadDTO(
                user.getEmail(),
                user.getEmail(),
                true,
                user.getFirstName(),
                List.of(new CredentialsDTO("password", user.getPassword(), false))
        );
    }
}

