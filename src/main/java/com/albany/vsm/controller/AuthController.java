package com.albany.vsm.controller;

import com.albany.vsm.dto.UserDto.*;
import com.albany.vsm.model.User;
import com.albany.vsm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            // Map DTO to entity
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setMobileNumber(request.getMobileNumber());
            user.setPassword(request.getPassword());
            user.setRole(request.getRole() != null ? request.getRole() : User.UserRole.CUSTOMER);
            
            // Register user
            User savedUser = userService.registerUser(user);
            
            // Map entity to response DTO
            UserResponse response = UserResponse.fromUser(savedUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/login/otp/initiate")
    public ResponseEntity<?> initiateOtpLogin(@RequestBody OtpLoginRequest request) {
        boolean otpSent = userService.initiateOtpLogin(request.getMobileNumber());
        
        if (otpSent) {
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "OTP sent successfully to your mobile number",
                "mobileNumber", request.getMobileNumber()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Failed to send OTP or mobile number not registered"
            ));
        }
    }
    
    @PostMapping("/login/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        try {
            User user = userService.verifyOtp(request.getMobileNumber(), request.getOtp());
            
            // Map entity to response DTO
            UserResponse response = UserResponse.fromUser(user);
            
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "OTP verified successfully",
                "user", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/login/email")
    public ResponseEntity<?> loginWithEmail(@RequestBody EmailLoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());
            
            // Map entity to response DTO
            UserResponse response = UserResponse.fromUser(user);
            
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Login successful",
                "user", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Invalid email or password"
            ));
        }
    }
    
    // Additional endpoint to get user info after OAuth login
    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser(@RequestParam String userId) {
        try {
            // Logic to fetch user by ID
            // Note: In a real app, you'd get this from security context instead
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "User details fetched successfully"
                // Additional user details would be added here
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "User not authenticated"
            ));
        }
    }
}