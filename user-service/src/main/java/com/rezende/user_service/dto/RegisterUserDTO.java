package com.rezende.user_service.dto;

public record RegisterUserDTO(
        String name,
        String email,
        String password
) { }
