package com.albany.vsm.exception;

/**
 * Exception thrown when attempting to register a user with an email or mobile number that already exists
 */
public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}