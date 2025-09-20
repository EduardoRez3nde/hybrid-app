package com.rezende.taxi_service.exceptions;

public class RideNotFoundException extends NoStackTraceException {

    public RideNotFoundException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
