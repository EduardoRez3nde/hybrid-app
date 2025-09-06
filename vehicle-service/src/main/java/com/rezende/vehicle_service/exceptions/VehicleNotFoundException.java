package com.rezende.vehicle_service.exceptions;

public class VehicleNotFoundException extends NoStackTraceException {

    public VehicleNotFoundException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
