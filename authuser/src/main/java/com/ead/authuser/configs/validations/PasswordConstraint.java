package com.ead.authuser.configs.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintImpl.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraint {

    String message() default """
            Invalid password! Password must contain at least one digit [0-9],\
             at least one lowercase Latin character [a-z],\
             at least one uppercase Latin character [A-Z],\
             at least one special character like [!@#&()–[{}]:;',?/*~$^+=<>],\
             must not contain whitespace,\
             and must be between 6 and 20 characters long.\
            """;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
