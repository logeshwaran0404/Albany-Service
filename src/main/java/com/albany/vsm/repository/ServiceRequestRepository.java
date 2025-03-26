package com.albany.vsm.repository;

import com.albany.vsm.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for service request data access
 */
@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Integer> {
    
    /**
     * Find service requests by status
     * @param status Service request status
     * @return List of service requests
     */
    List<ServiceRequest> findByStatus(ServiceRequest.Status status);
    
    /**
     * Find service requests by vehicle ID
     * @param vehicleId Vehicle ID
     * @return List of service requests
     */
    List<ServiceRequest> findByVehicleId(Integer vehicleId);
    
    /**
     * Find service requests by service advisor ID
     * @param serviceAdvisorId Service advisor ID
     * @return List of service requests
     */
    List<ServiceRequest> findByServiceAdvisorId(Integer serviceAdvisorId);
    
    /**
     * Find service requests by vehicle ID and status
     * @param vehicleId Vehicle ID
     * @param status Service request status
     * @return List of service requests
     */
    List<ServiceRequest> findByVehicleIdAndStatus(Integer vehicleId, ServiceRequest.Status status);
}