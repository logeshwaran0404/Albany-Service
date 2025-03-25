package com.albany.vsm.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class OtpService {
    private final CacheManager cacheManager;
    private final EmailService emailService;
    private final Random random = new Random();

    public OtpService(CacheManager cacheManager, EmailService emailService) {
        this.cacheManager = cacheManager;
        this.emailService = emailService;
    }

    /**
     * Generate and send OTP to the user's email
     * @param email User email address
     * @return Generated OTP
     */
    public String generateAndSendOtp(String email) {
        // Generate 4-digit OTP
        String otp = String.format("%04d", random.nextInt(10000));

        // Store OTP in cache
        Cache otpCache = cacheManager.getCache("otpCache");
        otpCache.put(email, otp);

        // Send email with OTP
        String subject = "Your OTP for Vehicle Service Management";
        String body = "Your OTP is: " + otp + ". It will expire in 5 minutes.";
        emailService.sendEmail(email, subject, body);

        return otp;
    }

    /**
     * Verify if OTP is valid
     * @param email User email
     * @param otp OTP to validate
     * @return true if OTP is valid
     */
    public boolean verifyOtp(String email, String otp) {
        Cache otpCache = cacheManager.getCache("otpCache");
        Cache.ValueWrapper valueWrapper = otpCache.get(email);

        if (valueWrapper != null) {
            String storedOtp = (String) valueWrapper.get();
            return storedOtp.equals(otp);
        }

        return false;
    }

    /**
     * Clear OTP from cache
     * @param email User email
     */
    public void clearOtp(String email) {
        Cache otpCache = cacheManager.getCache("otpCache");
        otpCache.evict(email);
    }
}