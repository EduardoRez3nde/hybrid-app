package com.rezende.user_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldError {

    private String fieldName;
    private String fieldMessage;

    public FieldError() { }

    public FieldError(final String fieldName, final String fieldMessage) {
        this.fieldName = fieldName;
        this.fieldMessage = fieldMessage;
    }
}
