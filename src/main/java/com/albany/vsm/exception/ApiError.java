package com.albany.vsm.exception;

class ApiError {
    private int status;
    private String message;
    private String path;
    private long timestamp;
    
    public ApiError() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public ApiError(int status, String message, String path) {
        this();
        this.status = status;
        this.message = message;
        this.path = path;
    }
    
    // Getters and setters
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}