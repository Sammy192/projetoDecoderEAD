package com.ead.course.configs.handlers;

public class FieldMessageDTO {

    private final String fieldName;
    private final String message;

    public FieldMessageDTO(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }
}