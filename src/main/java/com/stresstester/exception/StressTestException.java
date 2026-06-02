package com.stresstester.exception;

public class StressTestException extends RuntimeException {

    public StressTestException(String message) {
        super(message);
    }

    public StressTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
