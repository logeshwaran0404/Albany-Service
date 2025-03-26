package com.albany.vsm.repository;

import com.albany.vsm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for user data access
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user by email
     * @param email User's email
     * @return Optional User object
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by mobile number
     * @param mobileNumber User's mobile number
     * @return Optional User object
     */
    Optional<User> findByMobileNumber(String mobileNumber);

    /**
     * Check if an email is already in use
     * @param email User's email
     * @return Boolean indicating if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if a mobile number is already in use
     * @param mobileNumber User's mobile number
     * @return Boolean indicating if mobile number exists
     */
    boolean existsByMobileNumber(String mobileNumber);
}