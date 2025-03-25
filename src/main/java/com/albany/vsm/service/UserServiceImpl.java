package com.albany.vsm.service;

import com.albany.vsm.dto.RegistrationRequest;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of user service
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void registerCustomer(RegistrationRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        // Check if mobile number already exists
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserAlreadyExistsException("Mobile number already registered: " + request.getMobileNumber());
        }

        // Create a new customer
        User customer = new User();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setPassword("N/A"); // Not needed for OTP-based login

        // Set the role using the enum value that matches database
        try {
            customer.setRole(User.Role.customer);
            System.out.println("Role set to: " + customer.getRole()); // Debug log
        } catch (Exception e) {
            System.err.println("Error setting role: " + e.getMessage());
            throw new RuntimeException("Failed to set user role: " + e.getMessage(), e);
        }

        customer.setActive(false); // Will be activated after OTP verification

        // Debug logs before saving
        System.out.println("Saving user with role: " + customer.getRole());
        System.out.println("Available roles: " + java.util.Arrays.toString(User.Role.values()));

        try {
            userRepository.save(customer);
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void activateCustomer(String mobileNumber) {
        User customer = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UserNotFoundException("No user found with mobile number: " + mobileNumber));

        customer.setActive(true);
        userRepository.save(customer);
    }

    @Override
    public String generateSessionToken(String mobileNumber) {
        User customer = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new UserNotFoundException("No user found with mobile number: " + mobileNumber));

        if (!customer.isActive()) {
            throw new RuntimeException("Account is not active. Please verify your account first.");
        }

        // Simple token generation - in a real app, we'd use JWT with proper security
        return UUID.randomUUID().toString();
    }
}