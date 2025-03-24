package com.albany.vsm.util;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

public class OtpUtil {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Generate a random numeric OTP
    public static String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(RANDOM.nextInt(10));
        }
        return otp.toString();
    }

    // Generate a random alphanumeric OTP (more secure)
    public static String generateAlphanumericOtp() {
        final String ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(ALPHA_NUMERIC.charAt(RANDOM.nextInt(ALPHA_NUMERIC.length())));
        }
        return otp.toString();
    }

    // Validate if OTP is expired
    public static boolean isOtpExpired(LocalDateTime otpGeneratedTime) {
        if (otpGeneratedTime == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(otpGeneratedTime, now);
        return duration.toMinutes() > OTP_EXPIRY_MINUTES;
    }
}