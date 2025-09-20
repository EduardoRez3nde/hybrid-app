package com.rezende.taxi_service.exceptions;

public class BusinessRuleException extends NoStackTraceException {

    public BusinessRuleException(final String message, final Object... args) {
        super(String.format(message, args));
    }
}
