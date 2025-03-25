package com.albany.vsm.exception;

/**
 * Base exception for OTP-related errors
 */
public class OtpException extends RuntimeException {
    
    public OtpException(String message) {
        super(message);
    }
    
    public OtpException(String message, Throwable cause) {
        super(message, cause);
    }
}