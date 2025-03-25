package com.albany.vsm.controller;

import com.albany.vsm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication via Email OTP
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Step 1: Initiates login by sending OTP to the provided email
     * @param request containing user email
     * @return message indicating OTP has been sent
     */
    @PostMapping("/login/email")
    public ResponseEntity<String> initiateEmailLogin(@Valid @RequestBody EmailLoginRequest request) {
        authService.sendLoginOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to your email. Valid for 5 minutes.");
    }

    /**
     * Step 2: Verifies the OTP and completes the login process
     * @param request containing email and OTP
     * @return user details with authentication token
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<UserResponseDTO> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        UserResponseDTO response = authService.verifyOtpAndLogin(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(response);
    }
}