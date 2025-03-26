package com.albany.vsm.service;

import com.albany.vsm.entity.ServiceRequest;
import com.albany.vsm.repository.ServiceRequestRepository;
import com.albany.vsm.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for vehicle and service request operations
 */
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ServiceRequestRepository serviceRequestRepository;

    /**
     * Get all vehicles due for servicing in the current week
     * @return List of vehicles
     */
    public List<ServiceRequest> getVehiclesDueForService() {
        // Get service requests with status 'pending'
        return serviceRequestRepository.findByStatus(ServiceRequest.Status.pending);
    }

    /**
     * Get all vehicles currently under servicing
     * @return List of vehicles
     */
    public List<ServiceRequest> getVehiclesInService() {
        // Get service requests with status 'in progress'
        return serviceRequestRepository.findByStatus(ServiceRequest.Status.in_progress);
    }

    /**
     * Get all completed service requests
     * @return List of service requests
     */
    public List<ServiceRequest> getCompletedServices() {
        // Get service requests with status 'completed'
        return serviceRequestRepository.findByStatus(ServiceRequest.Status.completed);
    }

    /**
     * Assign a service advisor to a service request
     * @param serviceRequestId Service request ID
     * @param serviceAdvisorId Service advisor ID
     * @return Updated service request
     */
    public ServiceRequest assignServiceAdvisor(int serviceRequestId, int serviceAdvisorId) {
        // Find service request
        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
            .orElseThrow(() -> new RuntimeException("Service request not found"));
        
        // Update service advisor
        serviceRequest.setServiceAdvisorId(serviceAdvisorId);
        
        // Update status to 'in progress'
        serviceRequest.setStatus(ServiceRequest.Status.in_progress);
        
        // Save and return updated service request
        return serviceRequestRepository.save(serviceRequest);
    }

    /**
     * Mark a service request as completed
     * @param serviceRequestId Service request ID
     * @return Updated service request
     */
    public ServiceRequest markServiceAsCompleted(int serviceRequestId) {
        // Find service request
        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
            .orElseThrow(() -> new RuntimeException("Service request not found"));
        
        // Update status to 'completed'
        serviceRequest.setStatus(ServiceRequest.Status.completed);
        
        // Set completion timestamp
        serviceRequest.setCompletedAt(LocalDateTime.now());
        
        // Save and return updated service request
        return serviceRequestRepository.save(serviceRequest);
    }
}