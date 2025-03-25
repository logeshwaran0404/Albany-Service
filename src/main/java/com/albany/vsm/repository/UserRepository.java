package com.albany.vsm.repository;

import com.albany.vsm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for user data access
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user by their email address
     * @param email the user's email
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their mobile number
     * @param mobileNumber the user's mobile number
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByMobileNumber(String mobileNumber);

    /**
     * Check if a user exists with the given email
     * @param email the email to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given mobile number
     * @param mobileNumber the mobile number to check
     * @return true if a user exists with this mobile number, false otherwise
     */
    boolean existsByMobileNumber(String mobileNumber);
}