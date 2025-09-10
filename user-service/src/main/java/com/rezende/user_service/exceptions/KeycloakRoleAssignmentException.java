package com.rezende.user_service.exceptions;

public class KeycloakRoleAssignmentException extends NoStackTraceException {

    public KeycloakRoleAssignmentException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
