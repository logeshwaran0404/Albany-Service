package com.albany.vsm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "mobile_number", length = 15, unique = true)
    private String mobileNumber;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // For customers registering via mobile, we'll set a default secure password
    public void setDefaultPassword() {
        // BCrypt will be applied at the service layer
        this.password = "DefaultSecurePassword" + System.currentTimeMillis();
    }

    public enum UserRole {
        admin, serviceadvisor, customer
    }
}