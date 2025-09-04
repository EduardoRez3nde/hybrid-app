package com.rezende.user_service.dto;

public record TokenResponseDTO(
        String access_token,
        int expires_in,
        int refresh_expires_in,
        String token_type,
        int not_before_policy,
        String scope
) {}
