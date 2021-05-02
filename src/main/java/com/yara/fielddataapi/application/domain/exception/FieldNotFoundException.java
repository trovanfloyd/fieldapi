package com.yara.fielddataapi.application.domain.exception;

public class FieldNotFoundException extends RuntimeException {
    public FieldNotFoundException(final String message) {
        super(message);
    }
}
