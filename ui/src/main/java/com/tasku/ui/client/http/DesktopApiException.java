package com.tasku.ui.client.http;

public class DesktopApiException extends RuntimeException {
    private final Integer statusCode;

    public DesktopApiException(String message) {
        super(message);
        this.statusCode = null;
    }

    public DesktopApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }

    public DesktopApiException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}