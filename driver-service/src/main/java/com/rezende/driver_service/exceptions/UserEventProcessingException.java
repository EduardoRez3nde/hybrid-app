package com.rezende.driver_service.exceptions;

public class UserEventProcessingException extends NoStackTraceException {

    public UserEventProcessingException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
