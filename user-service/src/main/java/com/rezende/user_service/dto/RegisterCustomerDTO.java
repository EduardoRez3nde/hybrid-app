package com.rezende.user_service.dto;

import com.rezende.user_service.enums.RoleType;
import com.rezende.user_service.services.validation.UniqueEmailValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para o registo de um novo utilizador do tipo 'CUSTOMER'")
public record RegisterCustomerDTO(

        @Schema(description = "Primeiro Nome do utilizador.", example = "Fulano")
        @NotBlank(message = "{name.not.blank}")
        @Size(min = 2, max = 100, message = "{name.invalid.length}")
        String firstName,

        @Schema(description = "Segundo Nome do utilizador.", example = "da Silva")
        @NotBlank(message = "{name.not.blank}")
        @Size(min = 2, max = 100, message = "{name.invalid.length}")
        String lastName,

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
    public static RegisterCustomerDTO from(
            final String firstName,
            final String lastName,
            final String email,
            final String password
    ) {
        return new RegisterCustomerDTO(firstName, lastName, email, password);
    }

    public RoleType getRoleType() {
        return RoleType.CUSTOMER;
    }


    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
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
