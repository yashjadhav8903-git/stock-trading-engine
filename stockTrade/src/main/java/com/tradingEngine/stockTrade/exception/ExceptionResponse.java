package com.tradingEngine.stockTrade.exception;

import java.time.LocalDateTime;

public class ExceptionResponse {

    private LocalDateTime timestamp;
    private Integer statusCode;
    private String message;
    private String errorMessage;
    private String path;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ExceptionResponse(LocalDateTime timestamp, Integer statusCode, String message, String errorMessage, String path) {
        this.timestamp = timestamp;
        this.statusCode = statusCode;
        this.message = message;
        this.errorMessage = errorMessage;
        this.path = path;
    }
}
