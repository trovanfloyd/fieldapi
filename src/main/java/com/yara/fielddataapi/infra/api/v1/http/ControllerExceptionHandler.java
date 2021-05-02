package com.yara.fielddataapi.infra.api.v1.http;

import com.yara.fielddataapi.application.domain.exception.FieldNotFoundException;
import com.yara.fielddataapi.application.domain.exception.InvalidFieldRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FieldNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(FieldNotFoundException ex, WebRequest request) {
        log.info("Requested Field not found " + ex);
        return handleExceptionInternal(ex, "Requested Field not found", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidFieldRequestException.class)
    public ResponseEntity<Object> handleBadRequest(InvalidFieldRequestException ex, WebRequest request) {
        log.info("Invalid value in request: " + ex);
        String body = "Invalid value in request: " + ex.getMessage();
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralError(Exception ex, WebRequest request) {
        log.info("An error occurred procesing request: " + ex);
        return handleExceptionInternal(ex, "An error occurred when procesing request", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info("HttpMessageNotReadableException happened", ex);
        return handleExceptionInternal(ex, "Unable to parse the payload", headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info("MethodArgumentNotValidException happened", ex);
        List<String> details = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            details.add(error.getDefaultMessage());
        });
        return handleExceptionInternal(ex, details, headers, HttpStatus.BAD_REQUEST, request);
    }
}
