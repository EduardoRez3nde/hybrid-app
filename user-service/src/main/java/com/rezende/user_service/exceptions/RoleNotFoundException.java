package com.rezende.user_service.exceptions;

public class RoleNotFoundException extends NoStackTraceException {

    public RoleNotFoundException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
