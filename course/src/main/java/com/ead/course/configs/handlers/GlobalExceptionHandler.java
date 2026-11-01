package com.ead.course.configs.handlers;

import com.ead.course.configs.exceptions.ConflictException;
import com.ead.course.configs.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.Instant;
import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorDTO> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomErrorDTO errorResponse = new CustomErrorDTO(Instant.now(), status.value(), ex.getMessage(), request.getRequestURI());
        logger.error("NotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationErrorDTO errorResponse = new ValidationErrorDTO(Instant.now(), status.value(), "Validation failed", request.getRequestURI());

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorResponse.addError(error.getField(), error.getDefaultMessage());
        });

        logger.error("MethodArgumentNotValidException: {}", ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidFormatInputEnumException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException &&
                invalidFormatException.getTargetType() != null && invalidFormatException.getTargetType().isEnum()) {

            String fieldName = invalidFormatException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .findFirst().orElse("unknown_field");
            
            String acceptedValues = Arrays.toString(invalidFormatException.getTargetType().getEnumConstants());
            String errorMessage = String.format("Invalid value '%s'. Accepted values are: %s", invalidFormatException.getValue(), acceptedValues);

            ValidationErrorDTO errorResponse = new ValidationErrorDTO(Instant.now(), status.value(), "Invalid input for enum value", request.getRequestURI());
            errorResponse.addError(fieldName, errorMessage);
            logger.error("InvalidFormatException for enum: {}", errorMessage);
            return ResponseEntity.status(status).body(errorResponse);
        }
        
        CustomErrorDTO errorResponse = new CustomErrorDTO(Instant.now(), status.value(), "Malformed JSON request. Check the request body structure.", request.getRequestURI());
        logger.error("HttpMessageNotReadableException: {}", ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomErrorDTO> handleConflictException(ConflictException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomErrorDTO errorResponse = new CustomErrorDTO(Instant.now(), status.value(), ex.getMessage(), request.getRequestURI());
        logger.error("ConflictException: {}", ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
