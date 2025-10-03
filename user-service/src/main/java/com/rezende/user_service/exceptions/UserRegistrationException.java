package com.rezende.user_service.exceptions;

public class UserRegistrationException extends RuntimeException {

    public UserRegistrationException(final String message, Exception e) {
        super(message);
    }
}
