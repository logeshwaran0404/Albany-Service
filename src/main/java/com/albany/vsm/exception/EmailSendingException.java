package com.albany.vsm.exception;

/**
 * Exception thrown when email sending fails
 */
public class EmailSendingException extends OtpException {
    
    public EmailSendingException(String message) {
        super(message);
    }
    
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}