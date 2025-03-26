package com.albany.vsm.exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String entityType, Long id) {
        super(entityType + " with ID " + id + " not found");
    }
    
    public ResourceNotFoundException(String entityType, String identifier) {
        super(entityType + " with identifier " + identifier + " not found");
    }
}