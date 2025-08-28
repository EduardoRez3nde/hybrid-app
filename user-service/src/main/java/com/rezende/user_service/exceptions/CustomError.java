package com.rezende.user_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CustomError {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public CustomError() { }
}
