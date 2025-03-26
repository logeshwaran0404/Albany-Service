package com.albany.vsm.exception;

class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException() {
        super("You are not authorized to access this resource");
    }
}