package com.ead.payment.configs.handlers;

import com.ead.payment.configs.exceptions.BusinessException;
import com.ead.payment.configs.exceptions.ConflictException;
import com.ead.payment.configs.exceptions.NotFoundException;
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

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

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
        ValidationErrorDTO errorResponse = new ValidationErrorDTO(Instant.now(), status.value(), "Invalid data", request.getRequestURI());
        for (FieldError f : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addError(f.getField(), f.getDefaultMessage());
        }
        logger.error("MethodArgumentNotValidException: {}", ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomErrorDTO> handleInvalidFormatInputEnumException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException &&
                invalidFormatException.getTargetType() != null && invalidFormatException.getTargetType().isEnum()) {

            ValidationErrorDTO errorResponse = new ValidationErrorDTO(Instant.now(), status.value(), "Invalid enum value", request.getRequestURI());
            String fieldName = invalidFormatException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .findFirst().orElse("unknown_field");

            String acceptedValues = Arrays.toString(invalidFormatException.getTargetType().getEnumConstants());
            String errorMessage = String.format("Invalid value '%s'. Accepted values are: %s", invalidFormatException.getValue(), acceptedValues);
            errorResponse.addError(fieldName, errorMessage);

            logger.error("InvalidFormatException for enum: {}", ex.getMessage());
            return ResponseEntity.status(status).body(errorResponse);
        }
        // Caso seja outro tipo de erro de leitura de JSON (ex: JSON malformado)
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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomErrorDTO> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        CustomErrorDTO errorResponse = new CustomErrorDTO(Instant.now(), status.value(), ex.getMessage(), request.getRequestURI());
        logger.error("BusinessException: {}", ex.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

}
