package com.albany.vsm.repository;

import com.albany.vsm.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for vehicle data access
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    
    /**
     * Find vehicles by customer ID
     * @param customerId Customer ID
     * @return List of vehicles
     */
    List<Vehicle> findByCustomerId(Integer customerId);
    
    /**
     * Find vehicle by VIN
     * @param vin Vehicle Identification Number
     * @return Optional vehicle
     */
    Optional<Vehicle> findByVin(String vin);
    
    /**
     * Check if a VIN is already in use
     * @param vin Vehicle Identification Number
     * @return Boolean indicating if VIN exists
     */
    boolean existsByVin(String vin);
}