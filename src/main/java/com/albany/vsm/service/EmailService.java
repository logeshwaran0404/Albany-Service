package com.albany.vsm.service;

import com.albany.vsm.config.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;

    /**
     * Sends an OTP email for login authentication
     * @param to recipient email address
     * @param otp the one-time password
     */
    public void sendOtpEmail(String to, String otp) {
        try {
            if (!emailConfig.isConfigured()) {
                log.info("Email service not configured. Simulating email with OTP: {} to: {}", otp, to);
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(emailConfig.getSenderEmail());
            helper.setTo(to);
            helper.setSubject("Your Login OTP - Albany Vehicle Service Management");
            
            String content = buildOtpEmailContent(otp);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: " + to, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    /**
     * Builds the HTML content for the OTP email
     * @param otp the one-time password
     * @return formatted HTML content
     */
    private String buildOtpEmailContent(String otp) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; }"
                + ".container { max-width: 600px; margin: 0 auto; padding: 20px; }"
                + ".header { background-color: #4A90E2; color: white; padding: 15px; text-align: center; }"
                + ".content { padding: 20px; border: 1px solid #ddd; }"
                + ".otp-box { font-size: 24px; font-weight: bold; text-align: center; "
                + "padding: 10px; background-color: #f8f8f8; border: 1px dashed #ccc; margin: 20px 0; }"
                + ".footer { font-size: 12px; color: #777; margin-top: 20px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h2>Albany Vehicle Service Management</h2>"
                + "</div>"
                + "<div class='content'>"
                + "<p>Hello,</p>"
                + "<p>Please use the following One-Time Password (OTP) to login to your account. This OTP is valid for 5 minutes.</p>"
                + "<div class='otp-box'>" + otp + "</div>"
                + "<p>If you did not request this OTP, please ignore this email.</p>"
                + "<p>Thank you,<br>Albany Vehicle Service Team</p>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>This is an automated message. Please do not reply.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}