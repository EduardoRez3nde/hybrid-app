package com.rezende.user_service.dto;

import com.rezende.user_service.entities.enums.RoleType;

public record RegisterDriverDTO(
        String name,
        String email,
        String password
) implements RegisterUser {
    public static RegisterDriverDTO from(
            final String name,
            final String email,
            final String password
    ) {
        return new RegisterDriverDTO(name, email, password);
    }

    public RoleType getRoleType() {
        return RoleType.DRIVER;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
