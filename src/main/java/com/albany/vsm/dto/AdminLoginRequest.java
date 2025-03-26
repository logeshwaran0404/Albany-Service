package com.albany.vsm.dto;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String email;
    private String password;
    private boolean rememberMe;
}