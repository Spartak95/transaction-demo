package com.xcoder.transaction.exception;

public class DatabaseCrashException extends RuntimeException {
    public DatabaseCrashException(String message) {
        super(message);
    }
}
