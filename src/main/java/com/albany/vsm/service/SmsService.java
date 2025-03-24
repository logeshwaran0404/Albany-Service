package com.albany.vsm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SmsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    
    @Value("${sms.api.key:dummy-key}")
    private String apiKey;
    
    @Value("${sms.sender.id:ALBANY}")
    private String senderId;
    
    /**
     * Send SMS with OTP to customer's mobile number
     * In production, this would integrate with an SMS gateway provider
     * For development, we're logging the OTP
     * 
     * @param mobileNumber Recipient's mobile number
     * @param otp OTP to send
     * @return true if sent successfully
     */
    public boolean sendOtp(String mobileNumber, String otp) {
        try {
            // Log for development purposes
            logger.info("Sending OTP {} to mobile number {}", otp, mobileNumber);
            
            // In production, replace with actual SMS gateway integration
            // Example with Twilio or any other SMS provider:
            /*
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                    new PhoneNumber(mobileNumber),
                    new PhoneNumber(FROM_NUMBER),
                    "Your OTP for Vehicle Service Management is: " + otp)
                .create();
            */
            
            // Simulating successful send
            return true;
        } catch (Exception e) {
            logger.error("Failed to send OTP: {}", e.getMessage(), e);
            return false;
        }
    }
}