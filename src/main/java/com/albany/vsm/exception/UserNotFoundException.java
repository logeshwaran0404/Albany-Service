package com.albany.vsm.exception;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}