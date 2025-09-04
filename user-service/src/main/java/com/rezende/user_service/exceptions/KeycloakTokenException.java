package com.rezende.user_service.exceptions;

public class KeycloakTokenException extends RuntimeException {

    public KeycloakTokenException(final String message) {
        super(message);
    }
}
