package com.albany.vsm.service;

import com.albany.vsm.dto.AdminLoginRequest;
import com.albany.vsm.dto.ApiResponse;
import com.albany.vsm.dto.CustomerLoginRequest;
import com.albany.vsm.dto.VerifyOtpRequest;
import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Service for handling authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // In-memory OTP storage - in a real app, use Redis or another cache solution
    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, CustomerLoginRequest> registrationStore = new HashMap<>();

    /**
     * Admin login with email and password
     */
    public ApiResponse<String> loginAdmin(AdminLoginRequest request, HttpSession session) {
        log.info("Admin login attempt for email: {}", request.getEmail());

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            log.warn("Admin login failed: User not found for email: {}", request.getEmail());
            return ApiResponse.error("Invalid email or password");
        }

        User user = userOpt.get();

        // Check role - must be admin
        if (!"admin".equals(user.getRole())) {
            log.warn("Admin login failed: User is not an admin. Email: {}, Role: {}", request.getEmail(), user.getRole());
            return ApiResponse.error("Unauthorized access");
        }

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Admin login failed: Invalid password for email: {}", request.getEmail());
            return ApiResponse.error("Invalid email or password");
        }

        // Login successful - store user info in session
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", user.getName());

        log.info("Admin login successful for user: {}", user.getEmail());

        // Return token or authenticated flag
        return ApiResponse.success("Login successful", "admin_token");
    }

    /**
     * Send OTP for customer login
     */
    public ApiResponse<Void> sendLoginOtp(CustomerLoginRequest request) {
        log.info("Sending login OTP for email: {}", request.getEmail());

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            log.warn("Login OTP failed: User not found for email: {}", request.getEmail());
            return ApiResponse.error("User not found. Please register first.");
        }

        User user = userOpt.get();

        // Check role - must be customer
        if (!"customer".equals(user.getRole())) {
            log.warn("Login OTP failed: User is not a customer. Email: {}, Role: {}", request.getEmail(), user.getRole());
            return ApiResponse.error("Invalid user type for customer login");
        }

        // Generate and store OTP
        String otp = generateOtp();
        otpStore.put(request.getEmail(), otp);

        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), otp);

        log.info("Login OTP sent successfully to: {}", request.getEmail());

        return ApiResponse.success("OTP sent successfully to your email");
    }

    /**
     * Verify OTP for customer login
     */
    public ApiResponse<String> verifyLoginOtp(VerifyOtpRequest request, HttpSession session) {
        log.info("Verifying login OTP for email: {}", request.getEmail());

        // Check if OTP exists
        String storedOtp = otpStore.get(request.getEmail());

        if (storedOtp == null) {
            log.warn("OTP verification failed: OTP not found for email: {}", request.getEmail());
            return ApiResponse.error("OTP expired or not found. Please request a new one.");
        }

        // Verify OTP
        if (!storedOtp.equals(request.getOtp())) {
            log.warn("OTP verification failed: Invalid OTP for email: {}", request.getEmail());
            return ApiResponse.error("Invalid OTP. Please try again.");
        }

        // Find user
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            log.warn("OTP verification failed: User not found for email: {}", request.getEmail());
            return ApiResponse.error("User not found. Please register first.");
        }

        User user = userOpt.get();

        // Login successful - store user info in session
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("userName", user.getName());

        // Remove used OTP
        otpStore.remove(request.getEmail());

        log.info("OTP verification successful for user: {}", user.getEmail());

        // Return token or authenticated flag
        return ApiResponse.success("Login successful", "customer_token");
    }

    /**
     * Send OTP for customer registration
     */
    public ApiResponse<Void> sendRegistrationOtp(CustomerLoginRequest request) {
        log.info("Sending registration OTP for email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration OTP failed: Email already exists: {}", request.getEmail());
            return ApiResponse.error("Email already exists. Please log in instead.");
        }

        // Generate and store OTP
        String otp = generateOtp();
        otpStore.put(request.getEmail(), otp);

        // Store registration data
        registrationStore.put(request.getEmail(), request);

        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), otp);

        log.info("Registration OTP sent successfully to: {}", request.getEmail());

        return ApiResponse.success("OTP sent successfully to your email");
    }

    /**
     * Verify OTP for customer registration - does not generate password
     */
    public ApiResponse<String> verifyRegistrationOtp(VerifyOtpRequest request, HttpSession session) {
        log.info("Verifying registration OTP for email: {}", request.getEmail());

        // Check if OTP exists
        String storedOtp = otpStore.get(request.getEmail());

        if (storedOtp == null) {
            log.warn("OTP verification failed: OTP not found for email: {}", request.getEmail());
            return ApiResponse.error("OTP expired or not found. Please request a new one.");
        }

        // Verify OTP
        if (!storedOtp.equals(request.getOtp())) {
            log.warn("OTP verification failed: Invalid OTP for email: {}", request.getEmail());
            return ApiResponse.error("Invalid OTP. Please try again.");
        }

        // Get registration data
        CustomerLoginRequest regData = registrationStore.get(request.getEmail());

        if (regData == null) {
            log.warn("OTP verification failed: Registration data not found for email: {}", request.getEmail());
            return ApiResponse.error("Registration data not found. Please try again.");
        }

        // Create user - without password
        User newUser = new User();
        newUser.setName(regData.getName());
        newUser.setEmail(regData.getEmail());
        newUser.setMobileNumber(regData.getMobileNumber());
        newUser.setRole("customer");
        newUser.setPassword(null); // Don't set password as requested
        newUser.setCreatedAt(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(newUser);

        // Registration successful - store user info in session
        session.setAttribute("userId", savedUser.getId());
        session.setAttribute("userRole", savedUser.getRole());
        session.setAttribute("userEmail", savedUser.getEmail());
        session.setAttribute("userName", savedUser.getName());

        // Remove used OTP and registration data
        otpStore.remove(request.getEmail());
        registrationStore.remove(request.getEmail());

        // Send welcome email
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());

        log.info("Registration successful for user: {}", savedUser.getEmail());

        // Return token or authenticated flag
        return ApiResponse.success("Registration successful", "customer_token");
    }

    /**
     * Resend OTP
     */
    public ApiResponse<Void> resendOtp(CustomerLoginRequest request) {
        log.info("Resending OTP for email: {}", request.getEmail());

        // Generate new OTP
        String otp = generateOtp();
        otpStore.put(request.getEmail(), otp);

        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), otp);

        log.info("OTP resent successfully to: {}", request.getEmail());

        return ApiResponse.success("OTP resent successfully to your email");
    }

    /**
     * Generate a 4-digit OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // 4-digit number between 1000-9999
        return String.valueOf(otp);
    }
}