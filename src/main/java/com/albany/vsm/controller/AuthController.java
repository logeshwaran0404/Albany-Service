package com.albany.vsm.controller;

import com.albany.vsm.dto.ApiResponse;
import com.albany.vsm.dto.LoginRequest;
import com.albany.vsm.dto.RegistrationRequest;
import com.albany.vsm.dto.VerifyOtpRequest;
import com.albany.vsm.exception.InvalidOtpException;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Send OTP for login
     */
    @PostMapping("/login/otp")
    public ResponseEntity<ApiResponse> sendLoginOtp(@Valid @RequestBody LoginRequest request) {
        authService.sendLoginOtp(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "OTP sent to your email"));
    }

    /**
     * Verify OTP and login
     */
    @PostMapping("/login/verify")
    public ResponseEntity<ApiResponse> verifyLoginOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            String token = authService.verifyLoginOtp(request.getEmail(), request.getOtp());
            return ResponseEntity.ok(new ApiResponse(true, "Login successful", token));
        } catch (InvalidOtpException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Send OTP for registration
     */
    @PostMapping("/register/otp")
    public ResponseEntity<ApiResponse> sendRegistrationOtp(@Valid @RequestBody RegistrationRequest request) {
        try {
            authService.validateAndSendRegistrationOtp(request);
            return ResponseEntity.ok(new ApiResponse(true, "OTP sent to your email"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Verify OTP and complete registration
     */
    @PostMapping("/register/verify")
    public ResponseEntity<ApiResponse> verifyRegistrationOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            authService.verifyRegistrationOtp(request.getEmail(), request.getOtp());
            return ResponseEntity.ok(new ApiResponse(true, "Registration successful"));
        } catch (InvalidOtpException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    /**
     * Resend OTP
     */
    @PostMapping("/otp/resend")
    public ResponseEntity<ApiResponse> resendOtp(@RequestBody LoginRequest request) {
        authService.sendLoginOtp(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "OTP resent to your email"));
    }
}