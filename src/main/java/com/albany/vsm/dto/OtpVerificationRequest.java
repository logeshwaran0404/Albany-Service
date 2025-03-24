package com.albany.vsm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.processing.Pattern;

@Getter
@Setter
@Data
public class OtpVerificationRequest {
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;
    
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String otp;
    
    // Only needed for registration
    private String name;
    
    // Only needed for registration
    private String email;
}