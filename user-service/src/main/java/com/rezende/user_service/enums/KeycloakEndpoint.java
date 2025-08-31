package com.rezende.user_service.enums;

import lombok.Getter;

@Getter
public enum KeycloakEndpoint {

    TOKEN("/protocol/openid-connect/token"),
    LOGOUT("/protocol/openid-connect/logout"),
    INTROSPECT("/protocol/openid-connect/introspect"),
    USER_INFO("/protocol/openid-connect/userInfo");

    private final String path;

    KeycloakEndpoint(final String path) {
        this.path = path;
    }
}
