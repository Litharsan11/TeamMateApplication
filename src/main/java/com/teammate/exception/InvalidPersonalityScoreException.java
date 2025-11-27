package com.teammate.exception;

public class InvalidPersonalityScoreException extends IllegalArgumentException {
    public InvalidPersonalityScoreException(String message) {
        super(message);
    }

    public InvalidPersonalityScoreException(String message, Throwable cause) {
        super(message, cause);
    }
}