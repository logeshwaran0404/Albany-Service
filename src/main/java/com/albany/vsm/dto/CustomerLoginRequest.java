package com.albany.vsm.dto;

import lombok.Data;

@Data
public class CustomerLoginRequest {
    private String email;
    private String name;
    private String mobileNumber;
}