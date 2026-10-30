package com.ead.authuser.configs.handlers;

import com.ead.authuser.configs.exceptions.ConflictException;
import com.ead.authuser.configs.exceptions.NotFoundException;
import com.ead.authuser.dto.error.CustomErrorDTO;
import com.ead.authuser.dto.error.ValidationErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorDTO> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomErrorDTO err = new CustomErrorDTO(Instant.now(), status.value(), ex.getMessage(), request.getRequestURI());
        logger.error("NotFoundException: {} on path {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDTO> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422 is more specific for validation
        ValidationErrorDTO err = new ValidationErrorDTO(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());
        for (FieldError f : ex.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        logger.error("MethodArgumentNotValidException: {} on path {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomErrorDTO> handleConflictException(ConflictException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomErrorDTO err = new CustomErrorDTO(Instant.now(), status.value(), ex.getMessage(), request.getRequestURI());
        logger.error("ConflictException: {} on path {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorDTO> handleAccesDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        CustomErrorDTO err = new CustomErrorDTO(Instant.now(), status.value(), "Acesso negado", request.getRequestURI());
        logger.error("AccessDeniedException: {} on path {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String paramName = ex.getName();
        Object invalidValue = ex.getValue();

        String message = String.format("O parâmetro '%s' recebeu um valor inválido: '%s'.", paramName, invalidValue);

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String possibleValues = Arrays.toString(ex.getRequiredType().getEnumConstants());
            message += String.format(" Os valores aceitos são: %s.", possibleValues);
        }

        CustomErrorDTO err = new CustomErrorDTO(Instant.now(), status.value(), message, request.getRequestURI());
        logger.error("MethodArgumentTypeMismatchException: {} on path {}", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
