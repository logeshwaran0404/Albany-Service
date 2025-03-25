package com.albany.vsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing a user in the system
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "int") // Explicitly define as INT to match existing schema
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    // Make mobile number nullable
    @Column(name = "mobile_number", length = 15, unique = true, nullable = true)
    private String mobileNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    // Add the is_active field
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Set default value to true

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        // Ensure is_active is set before persisting
        if (isActive == null) {
            isActive = true;
        }
    }
}