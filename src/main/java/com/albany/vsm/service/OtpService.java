package com.albany.vsm.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@EnableCaching
public class OtpService {

    private final CacheManager cacheManager;
    private static final String OTP_CACHE_NAME = "otpCache";
    private static final int OTP_VALID_DURATION = 5; // 5 minutes

    @Autowired
    public OtpService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Generate OTP and store in cache (not in database)
     * @param mobileNumber User's mobile number
     * @return Generated OTP
     */
    public String generateOTP(String mobileNumber) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Store OTP in cache with expiration
        cacheManager.getCache(OTP_CACHE_NAME).put(mobileNumber, otp);
        
        // Return OTP for sending via SMS
        return otp;
    }

    /**
     * Validate OTP against cached value
     * @param mobileNumber User's mobile number
     * @param otp OTP to validate
     * @return true if valid, false otherwise
     */
    public boolean validateOTP(String mobileNumber, String otp) {
        // Get from cache
        String cachedOtp = cacheManager.getCache(OTP_CACHE_NAME).get(mobileNumber, String.class);
        
        // Validate
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            // Clear the OTP from cache after successful validation
            cacheManager.getCache(OTP_CACHE_NAME).evict(mobileNumber);
            return true;
        }
        
        return false;
    }

    /**
     * Clear OTP from cache
     * @param mobileNumber User's mobile number
     */
    public void clearOTP(String mobileNumber) {
        cacheManager.getCache(OTP_CACHE_NAME).evict(mobileNumber);
    }
}