package com.albany.vsm.exception;

/**
 * Exception thrown when a user with the same email or mobile already exists
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}