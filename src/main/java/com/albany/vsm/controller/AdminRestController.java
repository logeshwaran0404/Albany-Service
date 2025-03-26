package com.albany.vsm.controller;

import com.albany.vsm.dto.AuthDTO.UserDTO;
import com.albany.vsm.entity.ServiceRequest;
import com.albany.vsm.service.SessionService;
import com.albany.vsm.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for admin operations
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminRestController {

    private final SessionService sessionService;
    private final VehicleService vehicleService;

    /**
     * Get dashboard summary data
     */
    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboardSummary(HttpSession session) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }
        
        // Get dashboard data
        List<ServiceRequest> pendingServices = vehicleService.getVehiclesDueForService();
        List<ServiceRequest> inProgressServices = vehicleService.getVehiclesInService();
        List<ServiceRequest> completedServices = vehicleService.getCompletedServices();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("pendingCount", pendingServices.size());
        summary.put("inProgressCount", inProgressServices.size());
        summary.put("completedCount", completedServices.size());
        summary.put("pendingServices", pendingServices);
        summary.put("inProgressServices", inProgressServices);
        summary.put("completedServices", completedServices);
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Assign service advisor to service request
     */
    @PostMapping("/assign-advisor")
    public ResponseEntity<?> assignServiceAdvisor(
            @RequestBody Map<String, Integer> requestData,
            HttpSession session) {
        
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }
        
        Integer serviceRequestId = requestData.get("serviceRequestId");
        Integer serviceAdvisorId = requestData.get("serviceAdvisorId");
        
        if (serviceRequestId == null || serviceAdvisorId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required parameters"));
        }
        
        try {
            ServiceRequest updatedRequest = vehicleService.assignServiceAdvisor(serviceRequestId, serviceAdvisorId);
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Service advisor assigned successfully",
                "serviceRequest", updatedRequest
            ));
        } catch (Exception e) {
            log.error("Error assigning service advisor", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mark service as completed
     */
    @PostMapping("/mark-completed")
    public ResponseEntity<?> markServiceAsCompleted(
            @RequestBody Map<String, Integer> requestData,
            HttpSession session) {
        
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }
        
        Integer serviceRequestId = requestData.get("serviceRequestId");
        
        if (serviceRequestId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required parameter"));
        }
        
        try {
            ServiceRequest updatedRequest = vehicleService.markServiceAsCompleted(serviceRequestId);
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "Service marked as completed",
                "serviceRequest", updatedRequest
            ));
        } catch (Exception e) {
            log.error("Error marking service as completed", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get current user information
     */
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        // Check if user is logged in
        if (!sessionService.isLoggedIn(session)) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }
        
        UserDTO currentUser = sessionService.getCurrentUser(session);
        
        // Check if user has admin role
        if (!sessionService.isAdmin(session)) {
            return ResponseEntity.status(403).body(Map.of("error", "Not an admin user"));
        }
        
        return ResponseEntity.ok(currentUser);
    }
}