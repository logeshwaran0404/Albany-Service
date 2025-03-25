package com.albany.vsm.service;

import com.albany.vsm.exception.OtpVerificationException;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for the registration process
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    
    /**
     * Start the registration process for a customer
     * 
     * @param fullName Customer's full name
     * @param email Customer's email
     * @return true if OTP sent successfully
     * @throws UserAlreadyExistsException if a user with the email already exists
     */
    public boolean startRegistration(String fullName, String email) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        
        // Store registration data in cache
        otpService.storeRegistrationData(email, fullName);
        
        // Send OTP
        otpService.sendOtp(email);
        
        return true;
    }
    
    /**
     * Complete the registration process after OTP verification
     * 
     * @param email Customer's email
     * @param otp OTP to verify
     * @return The registered user
     * @throws OtpVerificationException if OTP verification fails
     */
    public User completeRegistration(String email, String otp) {
        // Verify OTP
        boolean isValid = otpService.verifyOtp(email, otp);
        if (!isValid) {
            throw new OtpVerificationException("Invalid OTP");
        }
        
        // Get registration data from cache
        String fullName = otpService.getRegistrationData(email);
        if (fullName == null) {
            throw new OtpVerificationException("Registration data expired or not found");
        }
        
        // Generate a random password (user won't need to know this)
        String randomPassword = UUID.randomUUID().toString();
        
        // Create the user
        User user = new User();
        user.setName(fullName);
        user.setEmail(email);
        user.setPassword(randomPassword); // This won't be used since login is OTP-based
        user.setRole(User.Role.customer);
        user.setIsActive(true);
        
        // Save and return the user
        return userRepository.save(user);
    }
}