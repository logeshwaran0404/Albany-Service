package com.albany.vsm.controller;

import com.albany.vsm.dto.*;
import com.albany.vsm.model.User;
import com.albany.vsm.service.AuthenticationService;
import com.albany.vsm.service.OAuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @Value("${oauth.token.validity.seconds:3600}")
    private long tokenValiditySeconds;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Initiate registration by sending OTP
     */
    @PostMapping("/register/initiate")
    public ResponseEntity<ApiResponse<Void>> initiateRegistration(
            @Valid @RequestBody RegistrationRequest request) {

        boolean otpSent = authenticationService.initiateRegistration(request);

        if (otpSent) {
            return ResponseEntity.ok(ApiResponse.success(
                    "OTP sent to mobile number. Please verify to complete registration."));
        } else {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("Failed to send OTP. Please try again."));
        }
    }

    /**
     * Complete registration with OTP verification
     */
    @PostMapping("/register/complete")
    public ResponseEntity<ApiResponse<TokenResponse>> completeRegistration(
            @Valid @RequestBody OtpVerificationRequest request) {

        User user = authenticationService.completeRegistration(request);
        String token = authenticationService.completeLogin(request);

        TokenResponse tokenResponse = new TokenResponse(token, tokenValiditySeconds);

        return ResponseEntity.ok(ApiResponse.success(
                "Registration successful", tokenResponse));
    }

    /**
     * Initiate login by sending OTP
     */
    @PostMapping("/login/initiate")
    public ResponseEntity<ApiResponse<Void>> initiateLogin(
            @Valid @RequestBody OtpRequest request) {

        boolean otpSent = authenticationService.initiateLogin(request);

        if (otpSent) {
            return ResponseEntity.ok(ApiResponse.success(
                    "OTP sent to mobile number. Please verify to login."));
        } else {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("Failed to send OTP. Please try again."));
        }
    }

    /**
     * Complete login with OTP verification
     */
    @PostMapping("/login/complete")
    public ResponseEntity<ApiResponse<TokenResponse>> completeLogin(
            @Valid @RequestBody OtpVerificationRequest request) {

        String token = authenticationService.completeLogin(request);

        TokenResponse tokenResponse = new TokenResponse(token, tokenValiditySeconds);

        return ResponseEntity.ok(ApiResponse.success(
                "Login successful", tokenResponse));
    }

    /**
     * Logout - revoke token
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {

        // Extract token from Authorization header
        String token = authHeader.replace("Bearer ", "");

        // Revoke token logic would be called here
        // oAuthService.revokeToken(token);

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}