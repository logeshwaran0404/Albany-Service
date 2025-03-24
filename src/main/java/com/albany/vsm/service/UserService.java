package com.albany.vsm.service;

import com.albany.vsm.dto.RegistrationRequest;

/**
 * Service interface for user operations
 */
public interface UserService {
    /**
     * Register a new customer
     * @param request The registration request
     */
    void registerCustomer(RegistrationRequest request);
    
    /**
     * Activate a customer account
     * @param mobileNumber The customer's mobile number
     */
    void activateCustomer(String mobileNumber);
    
    /**
     * Generate a session token for a customer
     * @param mobileNumber The customer's mobile number
     * @return The generated session token
     */
    String generateSessionToken(String mobileNumber);
}