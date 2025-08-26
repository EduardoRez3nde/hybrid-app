package com.rezende.user_service.dto;

public record RegisterUserDTO(
        String name,
        String email,
        String password
) {
    public static RegisterUserDTO from(
            final String name,
            final String email,
            final String password
    ) {
        return new RegisterUserDTO(name, email, password);
    }
}
