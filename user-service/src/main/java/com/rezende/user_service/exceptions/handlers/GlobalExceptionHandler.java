package com.rezende.user_service.exceptions.handlers;

import com.rezende.user_service.exceptions.CustomError;
import com.rezende.user_service.exceptions.EmailAlreadyExistsException;
import com.rezende.user_service.exceptions.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<CustomError>handleEmailAlreadyExistsException(
            final EmailAlreadyExistsException e,
            final HttpServletRequest request
    )  {
        log.error("Email already exists: {}", e.getMessage());

        final HttpStatus status = HttpStatus.CONFLICT;
        final CustomError error = CustomError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Email Already Exists!")
                .message(e.getMessage())
                .path(request.getContextPath())
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> handleGenericException(
            final Exception e,
            final HttpServletRequest request
    ) {
        log.error("Unhandled exception: {}", e.getMessage(), e);

        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final CustomError error = CustomError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error("Internal Server Error")
                .message(e.getMessage())
                .path(request.getContextPath())
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationExceptions(
            final MethodArgumentNotValidException e,
            final HttpServletRequest request
    ) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final ValidationError validationError = new ValidationError(
                Instant.now(),
                status.value(),
                "Validation Failed",
                "One or more fields are invalid",
                request.getContextPath()
        );
        e.getBindingResult().getFieldErrors()
                .forEach(error -> validationError.addError(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(status).body(validationError);
    }
}
