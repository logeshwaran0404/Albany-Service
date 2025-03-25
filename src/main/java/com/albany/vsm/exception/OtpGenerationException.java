package com.albany.vsm.exception;

/**
 * Exception thrown when OTP generation fails
 */
public class OtpGenerationException extends OtpException {
    
    public OtpGenerationException(String message) {
        super(message);
    }
    
    public OtpGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}