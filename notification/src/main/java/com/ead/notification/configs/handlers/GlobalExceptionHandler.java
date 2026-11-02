package com.ead.notification.configs.handlers;

import com.ead.notification.configs.handlers.dto.CustomErrorDTO;
import com.ead.notification.configs.handlers.dto.ValidationErrorDTO;
import com.ead.notification.configs.exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorDTO> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomErrorDTO errorResponse = new CustomErrorDTO(Instant.now(), status.value(), ex.getMessage(), request.getRequestURI());
        logger.error("NotFoundException: {} on path {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDTO> methodArgumentNotValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationErrorDTO err = new ValidationErrorDTO(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());

        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        logger.warn("MethodArgumentNotValidException: Invalid data on path {}. Details: {}", request.getRequestURI(), err.getErrors());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorDTO> handleGeneralException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        CustomErrorDTO err = new CustomErrorDTO(Instant.now(), status.value(), "Ocorreu um erro inesperado no servidor.", request.getRequestURI());
        logger.error("Unhandled Exception on path {}: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.status(status).body(err);
    }
}
