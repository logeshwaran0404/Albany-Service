package com.albany.vsm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entity for service details (linking service requests to work items)
 */
@Entity
@Table(name = "servicedetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "service_request_id", nullable = false)
    private Integer serviceRequestId;
    
    @Column(name = "work_item_id", nullable = false)
    private Integer workItemId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;
}