package com.rezende.user_service.dto;

public record KeycloakRoleDTO(
        String id,
        String name,
        boolean composite,
        boolean clientRole,
        String containerId
) { }