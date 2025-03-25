package com.albany.vsm.service;

/**
 * Service interface for OTP operations
 */
public interface OtpService {
    /**
     * Generate and send OTP to the given mobile number
     * @param mobileNumber The mobile number to send OTP
     * @return The generated OTP (for testing purposes only)
     */
    String generateAndSendOtp(String mobileNumber);
    
    /**
     * Verify the OTP for the given mobile number
     * @param mobileNumber The mobile number
     * @param otp The OTP to verify
     * @return true if OTP is valid, throws exception otherwise
     */
    boolean verifyOtp(String mobileNumber, String otp);
}