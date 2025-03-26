package com.albany.vsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity for vehicle data
 */
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "customer_id", nullable = false)
    private Integer customerId;
    
    @Column(nullable = false, unique = true)
    private String vin;
    
    @Column(nullable = false)
    private String model;
    
    private Integer year;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}