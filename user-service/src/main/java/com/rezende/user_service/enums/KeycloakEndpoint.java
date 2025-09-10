package com.rezende.user_service.enums;

import lombok.Getter;

@Getter
public enum KeycloakEndpoint {

    GET_TOKEN("/realms/{realm}/protocol/openid-connect/token"),
    CREATE_USER("/admin/realms/{realm}/users"),
    DELETE_USER("/admin/realms/{realm}/users/{userId}"),
    SEND_VERIFY_EMAIL("/admin/realms/{realm}/users/{userId}/send-verify-email"),
    GET_REALM_ROLE_BY_NAME("/admin/realms/{realm}/roles/{roleName}"),
    ASSIGN_REALM_ROLE_TO_USER("/admin/realms/{realm}/users/{userId}/role-mappings/realm");

    private final String path;

    KeycloakEndpoint(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
