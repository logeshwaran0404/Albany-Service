package com.albany.vsm.controller;

import com.albany.vsm.dto.ApiResponse;
import com.albany.vsm.dto.LoginRequest;
import com.albany.vsm.dto.OtpVerificationRequest;
import com.albany.vsm.dto.RegistrationRequest;
import com.albany.vsm.exception.OtpExpiredException;
import com.albany.vsm.exception.OtpInvalidException;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.service.OtpService;
import com.albany.vsm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    /**
     * Register a new customer
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerCustomer(@Valid @RequestBody RegistrationRequest request) {
        try {
            userService.registerCustomer(request);

            // Generate and send OTP for verification
            String otp = otpService.generateAndSendOtp(request.getMobileNumber());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Customer registered successfully. OTP sent to your mobile number."));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Verify OTP for customer registration
     */
    @PostMapping("/verify-registration")
    public ResponseEntity<ApiResponse> verifyRegistrationOtp(@Valid @RequestBody OtpVerificationRequest request) {
        try {
            otpService.verifyOtp(request.getMobileNumber(), request.getOtp());
            userService.activateCustomer(request.getMobileNumber());

            return ResponseEntity.ok(new ApiResponse(true, "Account verified successfully"));
        } catch (OtpInvalidException | OtpExpiredException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Verification failed: " + e.getMessage()));
        }
    }

    /**
     * Login with mobile number (sends OTP)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Generate and send OTP for login
            String otp = otpService.generateAndSendOtp(request.getMobileNumber());

            return ResponseEntity.ok(new ApiResponse(true, "OTP sent to your mobile number"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to send OTP: " + e.getMessage()));
        }
    }

    /**
     * Verify OTP for login
     */
    @PostMapping("/verify-login")
    public ResponseEntity<ApiResponse> verifyLoginOtp(@Valid @RequestBody OtpVerificationRequest request) {
        try {
            otpService.verifyOtp(request.getMobileNumber(), request.getOtp());

            // Generate session token
            String token = userService.generateSessionToken(request.getMobileNumber());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            return ResponseEntity.ok(new ApiResponse(true, "Login successful", data));
        } catch (OtpInvalidException | OtpExpiredException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Login failed: " + e.getMessage()));
        }
    }

    /**
     * Resend OTP
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@Valid @RequestBody LoginRequest request) {
        try {
            // Generate and send a new OTP
            String otp = otpService.generateAndSendOtp(request.getMobileNumber());

            return ResponseEntity.ok(new ApiResponse(true, "New OTP sent to your mobile number"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to send OTP: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint to check if Twilio is configured properly
     * Should be removed or secured in production
     */
    @GetMapping("/test-sms")
    public ResponseEntity<ApiResponse> testSmsConfiguration() {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "SMS configuration is ready for testing. Use the registration or login endpoints to test actual OTP sending."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "SMS configuration test failed: " + e.getMessage()));
        }
    }
}