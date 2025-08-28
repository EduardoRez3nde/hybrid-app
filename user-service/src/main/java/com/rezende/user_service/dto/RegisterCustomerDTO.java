package com.rezende.user_service.dto;

import com.rezende.user_service.entities.enums.RoleType;
import com.rezende.user_service.services.validation.UniqueEmailValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCustomerDTO(

        @NotBlank(message = "{name.not.blank}")
        @Size(min = 2, max = 100, message = "{name.invalid.length}")
        String name,

        @NotBlank(message = "{email.not.blank}")
        @Email(message = "{email.not.valid}")
        @UniqueEmailValid(message = "{email.already.exists}")
        @Size(max = 255, message = "{email.too.long}")
        String email,

        @NotBlank(message = "{password.too.short}")
        @Size(min = 8, message = "{password.too.short}")
        String password
) implements RegisterUser {
    public static RegisterCustomerDTO from(
            final String name,
            final String email,
            final String password
    ) {
        return new RegisterCustomerDTO(name, email, password);
    }

    public RoleType getRoleType() {
        return RoleType.CUSTOMER;
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
