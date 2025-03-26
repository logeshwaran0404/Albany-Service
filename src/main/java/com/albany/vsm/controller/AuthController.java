package com.albany.vsm.controller;

import com.albany.vsm.dto.ApiResponse;
import com.albany.vsm.dto.LoginRequest;
import com.albany.vsm.dto.RegistrationRequest;
import com.albany.vsm.dto.VerifyOtpRequest;
import com.albany.vsm.exception.InvalidOtpException;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.service.AuthService;
import com.albany.vsm.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.albany.vsm.dto.AuthDTO.AuthResponse;
import com.albany.vsm.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * REST controller for handling user authentication operations
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;

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

    /**
     * Show admin login page
     */
    @GetMapping("/admin/login")
    public String showAdminLoginPage(HttpSession session) {
        // Redirect to dashboard if already logged in as admin
        if (sessionService.isLoggedIn(session) && sessionService.isAdmin(session)) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    /**
     * Show service advisor login page
     */
    @GetMapping("/advisor/login")
    public String showAdvisorLoginPage(HttpSession session) {
        // Redirect to dashboard if already logged in as service advisor
        if (sessionService.isLoggedIn(session) && sessionService.isServiceAdvisor(session)) {
            return "redirect:/advisor/dashboard";
        }
        return "advisor/login";
    }

    /**
     * Show customer login page
     */
    @GetMapping("/customer/login")
    public String showCustomerLoginPage(HttpSession session) {
        // Redirect to dashboard if already logged in as customer
        if (sessionService.isLoggedIn(session) && sessionService.isCustomer(session)) {
            return "redirect:/customer/dashboard";
        }
        return "customer/login";
    }

    /**
     * Process admin login
     */
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        log.info("Admin login attempt for email: {}", request.getEmail());

        // Try to authenticate user
        Optional<User> userOpt = authService.login(request.getEmail(), request.getPassword());

        // If authentication fails
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(new AuthResponse(
                    false,
                    "Invalid email or password",
                    null,
                    null
            ));
        }

        User user = userOpt.get();

        // Check if user has admin role
        if (!authService.isAdmin(user)) {
            return ResponseEntity.ok(new AuthResponse(
                    false,
                    "You don't have admin privileges",
                    null,
                    null
            ));
        }

        // Store user in session
        sessionService.storeUserInSession(session, user, request.isRememberMe());

        return ResponseEntity.ok(new AuthResponse(
                true,
                "Login successful",
                "/admin/dashboard",
                null
        ));
    }

    /**
     * Process service advisor login
     */
    @PostMapping("/advisor/login")
    public ResponseEntity<AuthResponse> advisorLogin(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        log.info("Service advisor login attempt for email: {}", request.getEmail());

        // Try to authenticate user
        Optional<User> userOpt = authService.login(request.getEmail(), request.getPassword());

        // If authentication fails
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(new AuthResponse(
                    false,
                    "Invalid email or password",
                    null,
                    null
            ));
        }

        User user = userOpt.get();

        // Check if user has service advisor role
        if (!authService.isServiceAdvisor(user)) {
            return ResponseEntity.ok(new AuthResponse(
                    false,
                    "You don't have service advisor privileges",
                    null,
                    null
            ));
        }

        // Store user in session
        sessionService.storeUserInSession(session, user, request.isRememberMe());

        return ResponseEntity.ok(new AuthResponse(
                true,
                "Login successful",
                "/advisor/dashboard",
                null
        ));
    }

    /**
     * Process customer login
     */
    @PostMapping("/customer/login")
    public ResponseEntity<AuthResponse> customerLogin(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        log.info("Customer login attempt for email: {}", request.getEmail());

        // Try to authenticate user
        Optional<User> userOpt = authService.login(request.getEmail(), request.getPassword());

        // If authentication fails
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(new AuthResponse(
                    false,
                    "Invalid email or password",
                    null,
                    null
            ));
        }

        User user = userOpt.get();

        // Check if user has customer role
        if (!authService.isCustomer(user)) {
            return ResponseEntity.ok(new AuthResponse(
                    false,
                    "This account is not registered as a customer",
                    null,
                    null
            ));
        }

        // Store user in session
        sessionService.storeUserInSession(session, user, request.isRememberMe());

        return ResponseEntity.ok(new AuthResponse(
                true,
                "Login successful",
                "/customer/dashboard",
                null
        ));
    }

    /**
     * Log out the current user
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpSession session) {
        sessionService.logout(session);

        return ResponseEntity.ok(new AuthResponse(
                true,
                "Logout successful",
                "/",
                null
        ));
    }
}