package com.albany.vsm.dto;

import lombok.Data;

@Data
public class RegistrationResponse {
    private Integer userId;
    private String email;
    private String name;
    private String message;
}