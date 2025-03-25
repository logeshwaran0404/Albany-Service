package com.albany.vsm.exception;

/**
 * Exception thrown when OTP verification fails
 */
public class OtpVerificationException extends OtpException {
    
    public OtpVerificationException(String message) {
        super(message);
    }
    
    public OtpVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}