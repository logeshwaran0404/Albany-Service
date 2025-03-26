package com.albany.vsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for work item data (services provided)
 */
@Entity
@Table(name = "workitems")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}