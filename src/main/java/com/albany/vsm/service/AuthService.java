package com.albany.vsm.service;

import com.albany.vsm.dto.RegistrationRequest;
import com.albany.vsm.entity.User;
import com.albany.vsm.exception.InvalidOtpException;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for handling authentication operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    /**
     * Send OTP for login
     */
    public void sendLoginOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);

        // We'll send OTP even if user doesn't exist, to avoid leaking user existence info
        // But we log a message for monitoring
        if (user == null) {
            log.info("Login OTP requested for non-existent user: {}", email);
        }

        // Generate and send OTP
        String otp = generateOtp();
        cacheManager.getCache("otpCache").put(email, otp);

        // Send email with OTP
        String subject = "Your Login OTP for Albany Vehicle Service";
        String body = createOtpEmailBody(otp, "login to", false);
        emailService.sendEmail(email, subject, body);
    }

    /**
     * Validate registration request and send OTP
     */
    public void validateAndSendRegistrationOtp(RegistrationRequest request) {
        // Check if user already exists by email only
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        // If mobile number is provided, check if it's already in use
        String mobileNumber = request.getMobileNumber();
        if (mobileNumber != null && !mobileNumber.trim().isEmpty() &&
                userRepository.findByMobileNumber(mobileNumber).isPresent()) {
            throw new UserAlreadyExistsException("User with this mobile number already exists");
        }

        // Store registration data temporarily
        cacheManager.getCache("registrationCache").put(request.getEmail(), request);

        // Generate and send OTP
        String otp = generateOtp();
        cacheManager.getCache("otpCache").put(request.getEmail(), otp);

        // Send email with OTP
        String subject = "Your Registration OTP for Albany Vehicle Service";
        String body = createOtpEmailBody(otp, "register with", true);
        emailService.sendEmail(request.getEmail(), subject, body);
    }

    /**
     * Verify OTP for login and generate token
     */
    public String verifyLoginOtp(String email, String otpInput) {
        // Get stored OTP
        String storedOtp = getStoredOtp(email);

        // Verify OTP
        if (!storedOtp.equals(otpInput)) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // Check if user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // OTP is valid and user exists
        invalidateOtp(email);

        // Generate simple token (for demonstration purposes)
        // In a real app, use JWT or OAuth
        return UUID.randomUUID().toString();
    }

    /**
     * Verify OTP for registration and create user
     */

    // Update the verifyRegistrationOtp method in AuthService.java

    @Transactional
    public void verifyRegistrationOtp(String email, String otpInput) {
        // Get stored OTP
        String storedOtp = getStoredOtp(email);

        // Verify OTP
        if (!storedOtp.equals(otpInput)) {
            throw new InvalidOtpException("Invalid OTP");
        }

        // Get registration data
        Cache.ValueWrapper wrapper = cacheManager.getCache("registrationCache").get(email);
        if (wrapper == null) {
            throw new InvalidOtpException("Registration session expired");
        }

        RegistrationRequest request = (RegistrationRequest) wrapper.get();

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Only set mobile number if it's not null or empty
        String mobileNumber = request.getMobileNumber();
        if (mobileNumber != null && !mobileNumber.trim().isEmpty()) {
            user.setMobileNumber(mobileNumber);
        }

        user.setRole("customer");

        // Set isActive to true
        user.setIsActive(true);

        // Generate a random password (user can reset it later)
        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(randomPassword));

        // Save user
        userRepository.save(user);

        // Clear data from cache
        invalidateOtp(email);
        cacheManager.getCache("registrationCache").evict(email);

        // Send welcome email
        emailService.sendEmail(
                email,
                "Welcome to Albany Vehicle Service",
                "Dear " + request.getName() + ",\n\n" +
                        "Thank you for registering with Albany Vehicle Service. Your account has been created successfully.\n\n" +
                        "Your temporary password is: " + randomPassword + "\n\n" +
                        "Please login and change your password at your earliest convenience.\n\n" +
                        "Best regards,\nThe Albany Vehicle Service Team"
        );
    }


    /**
     * Generate a 4-digit OTP
     */
    private String generateOtp() {
        int otp = 1000 + random.nextInt(9000); // 4-digit OTP between 1000 and 9999
        return String.valueOf(otp);
    }

    /**
     * Get stored OTP from cache
     */
    private String getStoredOtp(String email) {
        Cache.ValueWrapper wrapper = cacheManager.getCache("otpCache").get(email);
        if (wrapper == null) {
            throw new InvalidOtpException("OTP expired or invalid");
        }
        return (String) wrapper.get();
    }

    /**
     * Invalidate OTP after successful verification
     */
    private void invalidateOtp(String email) {
        cacheManager.getCache("otpCache").evict(email);
    }

    /**
     * Create email body for OTP
     */
    private String createOtpEmailBody(String otp, String action, boolean isRegistration) {
        StringBuilder body = new StringBuilder();

        body.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        body.append("<div style='background-color: #722F37; padding: 20px; color: white; text-align: center;'>");
        body.append("<h1>Albany Vehicle Service</h1>");
        body.append("</div>");
        body.append("<div style='padding: 20px; border: 1px solid #ddd; border-top: none;'>");
        body.append("<h2>Your Verification Code</h2>");
        body.append("<p>Use the following OTP to ").append(action).append(" Albany Vehicle Service:</p>");
        body.append("<div style='font-size: 24px; font-weight: bold; text-align: center; padding: 15px; background-color: #f5f5f5; margin: 20px 0;'>");
        body.append(otp);
        body.append("</div>");
        body.append("<p>This code will expire in 5 minutes.</p>");
        body.append("<p>If you didn't request this code, you can safely ignore this email.</p>");

        if (isRegistration) {
            body.append("<p>Thank you for registering with Albany Vehicle Service.</p>");
        }

        body.append("</div>");
        body.append("<div style='text-align: center; padding: 10px; color: #777; font-size: 12px;'>");
        body.append("<p>© 2025 Albany Vehicle Service. All rights reserved.</p>");
        body.append("</div>");
        body.append("</div>");

        return body.toString();
    }
}