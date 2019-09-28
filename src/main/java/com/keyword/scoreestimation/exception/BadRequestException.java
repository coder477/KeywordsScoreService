package com.keyword.scoreestimation.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {

    private HttpStatus value;
    private String message;

    public BadRequestException(HttpStatus value, String message) {
        this.value = value;
        this.message = message;
    }

    public HttpStatus getValue() {
        return value;
    }

    public String getMessage() {
        return this.message;
    }

}
