package com.albany.vsm.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Twilio SMS service
 */
@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid:${TWILIO_ACCOUNT_SID:}}")
    private String accountSid;

    @Value("${twilio.auth.token:${TWILIO_AUTH_TOKEN:}}")
    private String authToken;

    @Value("${twilio.phone.number:${TWILIO_PHONE_NUMBER:}}")
    private String phoneNumber;

    /**
     * Initialize Twilio with credentials on startup
     */
    @PostConstruct
    public void initTwilio() {
        if (isConfigured()) {
            Twilio.init(accountSid, authToken);
            System.out.println("Twilio initialized with account: " + accountSid);
        } else {
            System.out.println("Twilio not configured. SMS sending will be simulated.");
        }
    }

    /**
     * Check if Twilio is properly configured
     */
    public boolean isConfigured() {
        return accountSid != null && !accountSid.isEmpty() 
                && authToken != null && !authToken.isEmpty()
                && phoneNumber != null && !phoneNumber.isEmpty();
    }

    /**
     * Get the configured Twilio phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
}