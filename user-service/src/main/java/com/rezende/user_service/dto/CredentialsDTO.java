package com.rezende.user_service.dto;

public record CredentialsDTO(
        String type,
        String value,
        boolean temporary
) {
}
