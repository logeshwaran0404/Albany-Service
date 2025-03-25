package com.albany.vsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequestDto {
    private String email;
}