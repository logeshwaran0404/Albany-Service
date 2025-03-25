package com.albany.vsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP verification request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationDto {
    private String email;
    private String otp;
}