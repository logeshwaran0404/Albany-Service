package com.albany.vsm.controller;

import com.albany.vsm.dto.OtpRequestDto;
import com.albany.vsm.dto.OtpVerificationDto;
import com.albany.vsm.exception.OtpVerificationException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.service.OtpService;
import com.albany.vsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for login process
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final UserRepository userRepository;

    /**
     * Request OTP for login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody OtpRequestDto request) {
        try {
            // Check if user exists
            if (!userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "message", "User not found", 
                        "success", false
                    ));
            }
            
            // Send OTP
            otpService.sendOtp(request.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully to " + request.getEmail(), 
                "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Failed to send OTP: " + e.getMessage(), 
                    "success", false
                ));
        }
    }

    /**
     * Verify OTP for login
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyLogin(@RequestBody OtpVerificationDto request) {
        try {
            // Check if user exists
            if (!userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "message", "User not found", 
                        "success", false
                    ));
            }
            
            // Verify OTP
            boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
            
            if (isValid) {
                // In a real app, you would create a session or token here
                return ResponseEntity.ok(Map.of(
                    "message", "Login successful", 
                    "email", request.getEmail(),
                    "success", true
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "message", "Invalid OTP", 
                        "success", false
                    ));
            }
        } catch (OtpVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "message", e.getMessage(), 
                    "success", false
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Error during verification: " + e.getMessage(), 
                    "success", false
                ));
        }
    }
}