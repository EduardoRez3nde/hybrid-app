package com.rezende.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(

        @NotBlank(message = "{email.not.blank}")
        @Size(max = 255, message = "{email.too.long}")
        String username,

        @NotBlank(message = "{password.too.short}")
        @Size(min = 8, message = "{password.too.short}")
        String password
) {

    public static LoginRequestDTO from(final String username, final String password) {
        return new LoginRequestDTO(username, password);
    }
}
