package com.happymapleday.recommendation.exception;

public class BossDataException extends RuntimeException {
    
    public BossDataException(String message) {
        super(message);
    }
    
    public BossDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 