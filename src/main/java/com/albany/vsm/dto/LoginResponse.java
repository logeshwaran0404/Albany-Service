package com.albany.vsm.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Integer userId;
    private String name;
    private String email;
    private String role;
    private String token;  // For session tracking
    private String message;
}