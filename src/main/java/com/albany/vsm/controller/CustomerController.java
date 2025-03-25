package com.albany.vsm.controller;

import com.albany.vsm.dto.CustomerRegistrationDto;
import com.albany.vsm.dto.OtpRequestDto;
import com.albany.vsm.dto.OtpVerificationDto;
import com.albany.vsm.exception.OtpVerificationException;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.model.User;
import com.albany.vsm.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for customer registration and OTP verification
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final RegistrationService registrationService;

    /**
     * Start customer registration process
     * Collects basic info and sends OTP
     */
    @PostMapping("/register")
    public ResponseEntity<?> startRegistration(@Valid @RequestBody CustomerRegistrationDto request,
                                         BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Validation errors",
                "errors", errors,
                "success", false
            ));
        }
        
        try {
            // Start registration process (sending OTP)
            registrationService.startRegistration(request.getFullName(), request.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "message", "Registration started. Please verify your email with the OTP sent.",
                "email", request.getEmail(),
                "success", true
            ));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "message", e.getMessage(),
                "success", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Registration failed: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * Complete customer registration with OTP verification
     */
    @PostMapping("/verify")
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody OtpVerificationDto request) {
        try {
            // Complete registration with OTP verification
            User registeredUser = registrationService.completeRegistration(request.getEmail(), request.getOtp());
            
            return ResponseEntity.ok(Map.of(
                "message", "Registration completed successfully",
                "userId", registeredUser.getId(),
                "email", registeredUser.getEmail(),
                "success", true
            ));
        } catch (OtpVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "message", e.getMessage(),
                "success", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Registration verification failed: " + e.getMessage(),
                "success", false
            ));
        }
    }
}