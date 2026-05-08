package com.ead.course.configs.exceptions;


public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}