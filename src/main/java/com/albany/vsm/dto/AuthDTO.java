package com.albany.vsm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTOs for authentication requests and responses
 */
public class AuthDTO {

    /**
     * Request DTO for login with email and password
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        
        @NotEmpty(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        private String email;
        
        @NotEmpty(message = "Password cannot be empty")
        private String password;
        
        private boolean rememberMe;
    }
    
    /**
     * Response DTO for authentication result
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private boolean success;
        private String message;
        private String redirectUrl;
        private UserDTO user;
    }
    
    /**
     * DTO with basic user information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private int id;
        private String name;
        private String email;
        private String role;
    }
}