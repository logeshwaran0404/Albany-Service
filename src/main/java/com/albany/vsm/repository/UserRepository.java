package com.albany.vsm.repository;

import com.albany.vsm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by mobile number
     */
    Optional<User> findByMobileNumber(String mobileNumber);

    /**
     * Check if user exists with given email
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists with given mobile number
     */
    boolean existsByMobileNumber(String mobileNumber);
}