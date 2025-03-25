package com.albany.vsm.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration for Email service
 * Used for sending OTP emails
 */
@Configuration
public class EmailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private boolean starttls;

    /**
     * Initialize email service on startup
     */
    @PostConstruct
    public void init() {
        if (isConfigured()) {
            System.out.println("Email service initialized with account: " + username);
        } else {
            System.out.println("Email service not configured. Email sending will be simulated.");
        }
    }

    /**
     * Check if email service is properly configured
     */
    public boolean isConfigured() {
        return username != null && !username.isEmpty()
                && password != null && !password.isEmpty();
    }

    /**
     * Configure and create JavaMailSender bean
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        if (isConfigured()) {
            mailSender.setUsername(username);
            mailSender.setPassword(password);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", "true");

        return mailSender;
    }

    /**
     * Get the configured sender email address
     */
    public String getSenderEmail() {
        return username;
    }
}