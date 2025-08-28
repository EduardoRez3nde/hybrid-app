package com.rezende.user_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class ValidationError extends CustomError {

    private final List<FieldError> errors = new ArrayList<>();

    public ValidationError() { super(); }

    public ValidationError(
            final Instant timestamp,
            final Integer status,
            final String error,
            final String message,
            final String path
    ) {
        super(timestamp, status, error, message, path);
    }

    public void addError(final String fieldName, final String fieldMessage) {
        errors.add(new FieldError(fieldName, fieldMessage));
    }
}
