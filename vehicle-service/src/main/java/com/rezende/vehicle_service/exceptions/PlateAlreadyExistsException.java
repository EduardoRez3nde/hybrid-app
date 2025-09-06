package com.rezende.vehicle_service.exceptions;

public class PlateAlreadyExistsException extends NoStackTraceException {

    public PlateAlreadyExistsException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
