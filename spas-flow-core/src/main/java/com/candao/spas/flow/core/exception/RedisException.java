package com.candao.spas.flow.core.exception;

public class RedisException extends RuntimeException {
    private static final long serialVersionUID = 5779784174832926767L;

    public RedisException() {
        this("DatabaseFailure");
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(Throwable cause) {
        super(/* "DatabaseFailure" */cause.getMessage(), cause);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}