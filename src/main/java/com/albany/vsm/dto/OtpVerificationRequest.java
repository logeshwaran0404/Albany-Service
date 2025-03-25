package com.albany.vsm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationRequest {
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;
    
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "OTP must be 4 digits")
    private String otp;
}