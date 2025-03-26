package com.albany.vsm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    /**
     * Send OTP email asynchronously
     */
    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP for Albany Vehicle Service");
            message.setText(String.format(
                "Hello,\n\n" +
                "Your OTP for Albany Vehicle Service is: %s\n\n" +
                "This code will expire in 30 minutes.\n\n" +
                "If you did not request this code, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Albany Vehicle Service Team", otp));
            
            mailSender.send(message);
            log.info("OTP email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending OTP email to {}: {}", to, e.getMessage());
        }
    }
    
    /**
     * Send welcome email asynchronously
     */
    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to Albany Vehicle Service");
            message.setText(String.format(
                "Hello %s,\n\n" +
                "Welcome to Albany Vehicle Service! Your account has been created successfully.\n\n" +
                "You can now log in to your account using the OTP sent to your email.\n\n" +
                "Best regards,\n" +
                "Albany Vehicle Service Team", name));
            
            mailSender.send(message);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Error sending welcome email to {}: {}", to, e.getMessage());
        }
    }
}