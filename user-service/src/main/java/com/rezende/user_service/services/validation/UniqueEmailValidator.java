package com.rezende.user_service.services.validation;

import com.rezende.user_service.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmailValid, String> {

    private final UserRepository userRepository;
    private String message;

    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(final UniqueEmailValid constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext constraintValidatorContext) {

        if (email == null || email.isBlank()) return true;

        if (userRepository.findByEmail(email).isPresent()) {
            return addConstraintViolation(constraintValidatorContext, "email", message);
        }
        return true;
    }

    private boolean addConstraintViolation(
            ConstraintValidatorContext context,
            String fieldName,
            String message
    ) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(fieldName)
                .addConstraintViolation();
        return false;
    }
}
