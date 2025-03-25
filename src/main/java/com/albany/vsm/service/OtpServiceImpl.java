package com.albany.vsm.service;

import com.albany.vsm.config.TwilioConfig;
import com.albany.vsm.exception.OtpExpiredException;
import com.albany.vsm.exception.OtpInvalidException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.repository.UserRepository;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of OTP service
 */
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final CacheManager cacheManager;
    private final UserRepository userRepository;
    private final TwilioConfig twilioConfig;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Value("${otp.expiry.minutes:5}")
    private int otpExpiryMinutes;

    @Override
    public String generateAndSendOtp(String mobileNumber) {
        // Check if user exists with given mobile number
        if (!userRepository.existsByMobileNumber(mobileNumber)) {
            throw new UserNotFoundException("No user found with mobile number: " + mobileNumber);
        }

        // Generate 4-digit OTP
        String otp = generateRandomOtp();

        // Store OTP in cache
        Cache otpCache = cacheManager.getCache("otpCache");
        if (otpCache != null) {
            otpCache.put(mobileNumber, otp);

            // Schedule OTP expiration
            scheduler.schedule(() -> {
                otpCache.evict(mobileNumber);
            }, otpExpiryMinutes, TimeUnit.MINUTES);
        }

        // Send OTP via SMS (simulated)
        sendSms(mobileNumber, "Your OTP for Albany Vehicle Service is: " + otp);

        return otp; // Return OTP for testing purposes
    }

    @Override
    public boolean verifyOtp(String mobileNumber, String otp) {
        Cache otpCache = cacheManager.getCache("otpCache");
        if (otpCache == null) {
            throw new RuntimeException("OTP cache not available");
        }

        // Get stored OTP from cache
        Cache.ValueWrapper storedOtpWrapper = otpCache.get(mobileNumber);

        if (storedOtpWrapper == null) {
            throw new OtpExpiredException("OTP has expired or not requested. Please request a new one.");
        }

        String storedOtp = (String) storedOtpWrapper.get();

        if (!otp.equals(storedOtp)) {
            throw new OtpInvalidException("Invalid OTP. Please try again.");
        }

        // OTP verified, remove from cache
        otpCache.evict(mobileNumber);

        return true;
    }

    /**
     * Generate a random 4-digit OTP
     */
    private String generateRandomOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000); // Generates a 4-digit number between 1000 and 9999
        return String.valueOf(otp);
    }

    /**
     * Send SMS with OTP using Twilio
     */
    private void sendSms(String mobileNumber, String message) {
        try {
            // Check if Twilio is configured
            if (twilioConfig.isConfigured()) {
                // Format the phone number to E.164 format (required by Twilio)
                String formattedNumber = formatPhoneNumber(mobileNumber);

                // Send the SMS
                Message.creator(
                        new com.twilio.type.PhoneNumber(formattedNumber),
                        new com.twilio.type.PhoneNumber(twilioConfig.getPhoneNumber()),
                        message
                ).create();

                System.out.println("SMS sent successfully to " + mobileNumber);
            } else {
                // Simulated SMS for development
                System.out.println("SIMULATED SMS to " + mobileNumber + ": " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());

            // For development purposes, still log the OTP
            System.out.println("DEVELOPMENT: OTP for " + mobileNumber + " is: " + message);
        }
    }

    /**
     * Format phone number to E.164 format (e.g., +91XXXXXXXXXX for India)
     */
    private String formatPhoneNumber(String mobileNumber) {
        // Remove any non-digit characters
        String digits = mobileNumber.replaceAll("\\D", "");

        // Add country code if not present (assuming India's +91 code)
        if (digits.length() == 10) {
            return "+91" + digits;
        } else if (digits.startsWith("91") && digits.length() == 12) {
            return "+" + digits;
        }

        // If already in international format
        return "+" + digits;
    }
}