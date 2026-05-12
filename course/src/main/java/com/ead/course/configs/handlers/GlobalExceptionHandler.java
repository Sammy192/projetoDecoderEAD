package com.ead.course.configs.handlers;

import com.ead.course.configs.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        logger.error("NotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Error: Validation failed", errors);
        logger.error("MethodArgumentNotValidException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormatInputEnumException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException &&
                invalidFormatException.getTargetType() != null && invalidFormatException.getTargetType().isEnum()) {

            String fieldName = invalidFormatException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .findFirst().orElse("unknown_field");

            String acceptedValues = Arrays.toString(invalidFormatException.getTargetType().getEnumConstants());
            String errorMessage = String.format("Invalid value '%s'. Accepted values are: %s", invalidFormatException.getValue(), acceptedValues);
            errors.put(fieldName, errorMessage);

            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid input for enum value", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        // Caso seja outro tipo de erro de leitura de JSON (ex: JSON malformado)
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Malformed JSON request. Check the request body structure.", null);
        logger.error("HttpMessageNotReadableException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

    }


    //mais simples sem tratamento na mensagem de erro
    /*@ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormatInputEnumException(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException &&
                invalidFormatException.getTargetType() != null && invalidFormatException.getTargetType().isEnum()) {
            String fieldName = invalidFormatException.getPath().get(invalidFormatException.getPath().size()-1).getFieldName();
            String errorMessage = ex.getMessage();
            errors.put(fieldName, errorMessage);
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid input enum value", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }*/
}
