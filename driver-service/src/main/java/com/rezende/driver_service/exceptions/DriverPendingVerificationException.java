package com.rezende.driver_service.exceptions;

public class DriverPendingVerificationException extends NoStackTraceException {

    public DriverPendingVerificationException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
