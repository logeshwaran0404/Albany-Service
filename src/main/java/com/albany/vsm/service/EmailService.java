package com.albany.vsm.service;

import com.albany.vsm.config.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private final EmailConfig emailConfig;

    /**
     * Send an email asynchronously
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            if (!emailConfig.isConfigured()) {
                log.info("Email service not configured. Simulating email to: {}, subject: {}", to, subject);
                log.info("Email body: {}", body);
                return;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailConfig.getSenderEmail());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true for HTML content

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
}