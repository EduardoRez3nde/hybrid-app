package com.rezende.user_service.exceptions;

public class KeycloakUserCreationException extends RuntimeException {

    public KeycloakUserCreationException(final String message) {
        super(message);
    }
}
