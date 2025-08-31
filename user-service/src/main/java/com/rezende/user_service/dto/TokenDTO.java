package com.rezende.user_service.dto;

public record TokenDTO(String Token) {

    public static TokenDTO from(final String token) {
        return new TokenDTO(token);
    }
}
