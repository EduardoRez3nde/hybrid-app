package com.rezende.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDTO(

        @JsonProperty("access_token")
        String token,

        @JsonProperty("expires_in")
        String expireIn
) {

    public static LoginResponseDTO from(final String token, final String expireIn) {
        return new LoginResponseDTO(token, expireIn);
    }
}
