package com.albany.vsm.controller;

import com.albany.vsm.dto.AdminLoginRequest;
import com.albany.vsm.dto.ApiResponse;
import com.albany.vsm.dto.CustomerLoginRequest;
import com.albany.vsm.dto.VerifyOtpRequest;
import com.albany.vsm.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication requests
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Display admin login page
     */
    @GetMapping("/auth/admin/login")
    public String adminLoginPage() {
        return "auth/admin/login";
    }

    /**
     * Display customer login page
     */
    @GetMapping("/auth/customer/login")
    public String customerLoginPage() {
        return "auth/customer/login";
    }

    /**
     * Redirect root to admin login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/auth/admin/login";
    }

    /**
     * Handle admin login (email/password)
     */
    @PostMapping("/api/auth/admin/login")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> adminLogin(@RequestBody AdminLoginRequest request, HttpSession session) {
        log.info("Admin login request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.loginAdmin(request, session));
    }

    /**
     * Send OTP for customer login
     */
    @PostMapping("/api/auth/login/otp")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> sendLoginOtp(@RequestBody CustomerLoginRequest request) {
        log.info("Login OTP request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.sendLoginOtp(request));
    }

    /**
     * Verify OTP for customer login
     */
    @PostMapping("/api/auth/login/verify")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> verifyLoginOtp(@RequestBody VerifyOtpRequest request, HttpSession session) {
        log.info("Login OTP verification request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.verifyLoginOtp(request, session));
    }

    /**
     * Send OTP for customer registration
     */
    @PostMapping("/api/auth/register/otp")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> sendRegistrationOtp(@RequestBody CustomerLoginRequest request) {
        log.info("Registration OTP request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.sendRegistrationOtp(request));
    }

    /**
     * Verify OTP for customer registration
     */
    @PostMapping("/api/auth/register/verify")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> verifyRegistrationOtp(@RequestBody VerifyOtpRequest request, HttpSession session) {
        log.info("Registration OTP verification request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.verifyRegistrationOtp(request, session));
    }

    /**
     * Resend OTP
     */
    @PostMapping("/api/auth/otp/resend")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestBody CustomerLoginRequest request) {
        log.info("OTP resend request received for email: {}", request.getEmail());
        return ResponseEntity.ok(authService.resendOtp(request));
    }
}