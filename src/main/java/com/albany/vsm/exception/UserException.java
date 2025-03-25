package com.albany.vsm.exception;

/**
 * Custom exception for user-related errors
 */
public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}