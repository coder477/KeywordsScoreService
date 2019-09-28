package com.keyword.scoreestimation.exception;

public class ExternalApiException extends RuntimeException {

    private String value;
    private String message;

    public ExternalApiException(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return this.message;
    }
}
