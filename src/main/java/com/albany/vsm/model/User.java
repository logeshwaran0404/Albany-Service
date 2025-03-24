package com.albany.vsm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 15)
    private String mobileNumber;

    @Column(nullable = false, length = 255)
    private String password; // Will be "N/A" for OTP-based customers

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean isActive = false; // Will be activated after OTP verification

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Enum for user roles
    public enum Role {
        admin,
        serviceadvisor,
        customer
    }
}