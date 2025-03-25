package com.albany.vsm.service;

import com.albany.vsm.config.EmailConfig;
import com.albany.vsm.exception.EmailSendingException;
import com.albany.vsm.exception.OtpGenerationException;
import com.albany.vsm.exception.OtpVerificationException;
import com.albany.vsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for generating, sending, and verifying OTPs via email
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;

    private final Random random = new Random();

    // Fallback storage in case cache is not working
    private final ConcurrentHashMap<String, String> otpMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> registrationMap = new ConcurrentHashMap<>();

    /**
     * Generate a 4-digit OTP
     * @return 4-digit OTP
     */
    private String generateOtp() {
        // Generate a 4-digit OTP
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    /**
     * Send OTP to an email address
     *
     * @param email The email to send the OTP to
     * @return The generated OTP
     * @throws OtpGenerationException if there's an error generating the OTP
     * @throws EmailSendingException if there's an error sending the email
     */
    public String sendOtp(String email) {
        try {
            // Normalize email (convert to lowercase)
            String normalizedEmail = email.toLowerCase().trim();

            // Generate OTP
            String otp = generateOtp();

            // Store OTP in cache
            Cache otpCache = cacheManager.getCache("otpCache");
            if (otpCache != null) {
                otpCache.put(normalizedEmail, otp);
                log.info("OTP stored in cache for email: {}", normalizedEmail);
            } else {
                log.warn("OTP cache not available, using fallback storage");
            }

            // Also store in fallback map
            otpMap.put(normalizedEmail, otp);

            // Send OTP via email
            if (emailConfig.isConfigured()) {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(emailConfig.getSenderEmail());
                    message.setTo(normalizedEmail);
                    message.setSubject("Your OTP for Vehicle Service Management");
                    message.setText("Your OTP is: " + otp + ". It will expire in 5 minutes.");

                    mailSender.send(message);
                    log.info("OTP email sent to: {}", normalizedEmail);
                } catch (MailException e) {
                    log.error("Failed to send OTP via email to {}: {}", normalizedEmail, e.getMessage());
                    throw new EmailSendingException("Failed to send OTP via email", e);
                }
            } else {
                // If email is not configured, just log the OTP (for development/testing only)
                log.info("SIMULATION MODE: OTP for {} is: {}", normalizedEmail, otp);
                System.out.println("SIMULATION MODE: OTP for " + normalizedEmail + " is: " + otp);
            }

            return otp;
        } catch (Exception e) {
            log.error("Error in sendOtp: {}", e.getMessage());
            if (e instanceof OtpGenerationException || e instanceof EmailSendingException) {
                throw e;
            }
            throw new OtpGenerationException("Error generating OTP", e);
        }
    }

    /**
     * Verify the OTP provided by the user
     *
     * @param email The user's email
     * @param userOtp The OTP provided by the user
     * @return true if OTP is valid, false otherwise
     * @throws OtpVerificationException if there's an error verifying the OTP
     */
    public boolean verifyOtp(String email, String userOtp) {
        try {
            // Normalize email (convert to lowercase)
            String normalizedEmail = email.toLowerCase().trim();

            log.info("Verifying OTP for email: {}", normalizedEmail);

            // Get from cache first
            Cache otpCache = cacheManager.getCache("otpCache");
            String storedOtp = null;

            if (otpCache != null) {
                storedOtp = otpCache.get(normalizedEmail, String.class);
                log.info("OTP from cache: {}", storedOtp != null ? "found" : "not found");
            }

            // If not in cache, try fallback map
            if (storedOtp == null) {
                storedOtp = otpMap.get(normalizedEmail);
                log.info("OTP from fallback storage: {}", storedOtp != null ? "found" : "not found");
            }

            if (storedOtp == null) {
                log.warn("OTP not found for email: {}", normalizedEmail);
                throw new OtpVerificationException("OTP expired or not generated for this email");
            }

            // Verify OTP
            boolean isValid = storedOtp.equals(userOtp);
            log.info("OTP validation result: {}", isValid ? "valid" : "invalid");

            // If valid, remove OTP from storage to prevent reuse
            if (isValid) {
                if (otpCache != null) {
                    otpCache.evict(normalizedEmail);
                }
                otpMap.remove(normalizedEmail);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Error in verifyOtp: {}", e.getMessage());
            if (e instanceof OtpVerificationException) {
                throw e;
            }
            throw new OtpVerificationException("Error verifying OTP: " + e.getMessage());
        }
    }

    /**
     * Store registration data in cache
     *
     * @param email User's email
     * @param fullName User's full name
     */
    public void storeRegistrationData(String email, String fullName) {
        // Normalize email (convert to lowercase)
        String normalizedEmail = email.toLowerCase().trim();

        Cache registrationCache = cacheManager.getCache("registrationCache");
        if (registrationCache != null) {
            registrationCache.put(normalizedEmail, fullName);
            log.info("Registration data stored in cache for email: {}", normalizedEmail);
        } else {
            log.warn("Registration cache not available, using fallback storage");
        }

        // Also store in fallback map
        registrationMap.put(normalizedEmail, fullName);
    }

    /**
     * Get registration data from cache
     *
     * @param email User's email
     * @return User's full name
     */
    public String getRegistrationData(String email) {
        // Normalize email (convert to lowercase)
        String normalizedEmail = email.toLowerCase().trim();

        String fullName = null;

        // Try cache first
        Cache registrationCache = cacheManager.getCache("registrationCache");
        if (registrationCache != null) {
            fullName = registrationCache.get(normalizedEmail, String.class);
            log.info("Registration data from cache: {}", fullName != null ? "found" : "not found");
        }

        // If not in cache, try fallback map
        if (fullName == null) {
            fullName = registrationMap.get(normalizedEmail);
            log.info("Registration data from fallback storage: {}", fullName != null ? "found" : "not found");
        }

        // Remove data from storage after retrieving
        if (fullName != null) {
            if (registrationCache != null) {
                registrationCache.evict(normalizedEmail);
            }
            registrationMap.remove(normalizedEmail);
        }

        return fullName;
    }
}