package com.albany.vsm.service;

import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for user operations
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new customer
     * This is the public registration method for customers only
     *
     * @param name User's full name
     * @param email User's email
     * @param mobileNumber User's mobile number
     * @param password User's password (will be hashed)
     * @return The registered user
     * @throws UserAlreadyExistsException if a user with the email or mobile number already exists
     */
    public User registerCustomer(String name, String email, String mobileNumber, String password) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new UserAlreadyExistsException("User with this mobile number already exists");
        }

        // Create a new user with customer role
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password with BCrypt
        user.setRole(User.Role.customer); // Always set role to 'customer' for public registration
        user.setIsActive(true);

        // Save and return the user
        return userRepository.save(user);
    }

    /**
     * Create a new admin or service advisor
     * This method should only be accessible to admin users
     *
     * @param name User's full name
     * @param email User's email
     * @param mobileNumber User's mobile number
     * @param password User's password (will be hashed)
     * @param role The role (admin or serviceadvisor)
     * @return The created user
     * @throws UserAlreadyExistsException if a user with the email or mobile number already exists
     * @throws IllegalArgumentException if the role is not valid
     */
    public User createStaffUser(String name, String email, String mobileNumber, String password, User.Role role) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new UserAlreadyExistsException("User with this mobile number already exists");
        }

        // Validate role - only admin and serviceadvisor allowed
        if (role == User.Role.customer) {
            throw new IllegalArgumentException("Use registerCustomer method for customer registration");
        }

        // Create a new user with the specified role
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt password with BCrypt
        user.setRole(role);
        user.setIsActive(true);

        // Save and return the user
        return userRepository.save(user);
    }

    /**
     * Check if a user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    /**
     * Validate password
     */
    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}