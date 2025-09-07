package com.rezende.driver_service.exceptions;

public class DriverNotPendingVerificationException extends NoStackTraceException {

    public DriverNotPendingVerificationException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
