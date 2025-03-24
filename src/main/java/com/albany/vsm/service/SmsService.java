package com.albany.vsm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${sms.api.key}")
    private String apiKey;

    @Value("${sms.api.url}")
    private String apiUrl;

    @Value("${sms.api.account.sid}")
    private String accountSid;

    @Value("${sms.api.sender.number}")
    private String senderNumber;

    // Send SMS using Twilio API
    public boolean sendSms(String phoneNumber, String message) {
        try {
            // Formatting the phone number (ensuring it has country code)
            String formattedPhoneNumber = formatPhoneNumber(phoneNumber);

            // Create HTTP client with timeout
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // Basic auth for Twilio (AccountSID:AuthToken)
            String auth = accountSid + ":" + apiKey;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            // Form data for Twilio
            String formData = String.format(
                    "To=%s&From=%s&Body=%s",
                    formattedPhoneNumber, senderNumber, message);

            // Create POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Log and check response
            logger.info("SMS API Response: Status={}, Body={}", response.statusCode(), response.body());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("SMS sent successfully to {}", formattedPhoneNumber);
                return true;
            } else {
                logger.error("Failed to send SMS. Status: {}, Response: {}",
                        response.statusCode(), response.body());
                return false;
            }

        } catch (Exception e) {
            logger.error("Error sending SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    // Format the OTP message and send it
    public boolean sendOtp(String phoneNumber, String otp) {
        String message = "Your Albany Vehicle Service verification code is: " + otp +
                ". Valid for 5 minutes. Do not share this code with anyone.";
        return sendSms(phoneNumber, message);
    }

    // Helper method to format phone number
    private String formatPhoneNumber(String phoneNumber) {
        // Clean the phone number (remove any non-digit characters)
        String cleanNumber = phoneNumber.replaceAll("[^\\d+]", "");

        // Add country code if not present
        if (!cleanNumber.startsWith("+")) {
            // Assuming India country code (+91) as default, adjust as needed
            cleanNumber = "+91" + cleanNumber;
        }

        return cleanNumber;
    }
}