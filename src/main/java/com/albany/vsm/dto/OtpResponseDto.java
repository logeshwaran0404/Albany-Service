package com.albany.vsm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OTP response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponseDto {
    private String message;
    private boolean success;
    
    // Static factory methods for common responses
    public static OtpResponseDto success(String message) {
        return new OtpResponseDto(message, true);
    }
    
    public static OtpResponseDto failure(String message) {
        return new OtpResponseDto(message, false);
    }
}