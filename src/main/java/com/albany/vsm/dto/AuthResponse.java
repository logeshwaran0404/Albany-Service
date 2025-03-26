package com.albany.vsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private boolean success;
    private String message;
    private String redirectUrl;
    private UserDTO user;
    
    /**
     * DTO for user information
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