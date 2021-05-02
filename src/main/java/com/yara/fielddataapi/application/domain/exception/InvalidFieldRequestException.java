package com.yara.fielddataapi.application.domain.exception;

public class InvalidFieldRequestException extends RuntimeException {
    public InvalidFieldRequestException(final String message) {
        super(message);
    }
}
