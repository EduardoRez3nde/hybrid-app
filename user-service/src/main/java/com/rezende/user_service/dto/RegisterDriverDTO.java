package com.rezende.user_service.dto;

import com.rezende.user_service.enums.RoleType;
import com.rezende.user_service.services.validation.UniqueEmailValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para o registo de um novo utilizador do tipo 'DRIVER'")
public record RegisterDriverDTO(

        @Schema(description = "Primeiro Nome do utilizador.", example = "Fulano")
        @NotBlank(message = "{name.not.blank}")
        @Size(min = 2, max = 100, message = "{name.invalid.length}")
        String name,

        @Schema(description = "Endereço de e-mail único do utilizador.", example = "fulano.silva@email.com")
        @NotBlank(message = "{email.not.blank}")
        @Email(message = "{email.not.valid}")
        @UniqueEmailValid(message = "{email.already.exists}")
        @Size(max = 255, message = "{email.too.long}")
        String email,

        @Schema(description = "Palavra-passe do utilizador. Mínimo 8 caracteres.", example = "senhaForte123")
        @NotBlank(message = "{password.too.short}")
        @Size(min = 8, message = "{password.too.short}")
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
