package com.albany.vsm.service;

import com.albany.vsm.config.EmailConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;

    public EmailService(JavaMailSender mailSender, EmailConfig emailConfig) {
        this.mailSender = mailSender;
        this.emailConfig = emailConfig;
    }

    /**
     * Send email with given subject and body
     * If email is not configured, just log the message
     * @param to Email recipient
     * @param subject Email subject
     * @param body Email content
     */
    public void sendEmail(String to, String subject, String body) {
        if (emailConfig.isConfigured()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(emailConfig.getSenderEmail());
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);

                mailSender.send(message);
                System.out.println("Email sent to: " + to);
            } catch (Exception e) {
                System.out.println("Failed to send email: " + e.getMessage());
                simulateEmailSending(to, subject, body);
            }
        } else {
            simulateEmailSending(to, subject, body);
        }
    }

    /**
     * Simulate email sending by logging the content (for development)
     */
    private void simulateEmailSending(String to, String subject, String body) {
        System.out.println("\n-------- SIMULATED EMAIL --------");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("-------------------------------\n");
    }
}