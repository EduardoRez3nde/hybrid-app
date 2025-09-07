package com.rezende.driver_service.exceptions;

public class DriverNotFoundException extends NoStackTraceException {

    public DriverNotFoundException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
