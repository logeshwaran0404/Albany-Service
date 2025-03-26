package com.albany.vsm.controller;

import com.albany.vsm.dto.AuthDTO.UserDTO;
import com.albany.vsm.service.SessionService;
import com.albany.vsm.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for admin dashboard and operations
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SessionService sessionService;
    private final VehicleService vehicleService;

    /**
     * Display admin dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            redirectAttributes.addAttribute("error", "Please login as an administrator to access this page");
            return "redirect:/auth/admin/login";
        }

        // Get current user
        UserDTO currentUser = sessionService.getCurrentUser(session);
        model.addAttribute("user", currentUser);

        // Get vehicles data for dashboard
        model.addAttribute("vehiclesForService", vehicleService.getVehiclesDueForService());
        model.addAttribute("vehiclesInService", vehicleService.getVehiclesInService());
        model.addAttribute("completedServices", vehicleService.getCompletedServices());

        return "admin/dashboard";
    }

    /**
     * Display user management page
     */
    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            redirectAttributes.addAttribute("error", "Please login as an administrator to access this page");
            return "redirect:/auth/admin/login";
        }

        // Get current user
        UserDTO currentUser = sessionService.getCurrentUser(session);
        model.addAttribute("user", currentUser);

        // Load user management data

        return "admin/users";
    }

    /**
     * Display service items management page
     */
    @GetMapping("/service-items")
    public String serviceItemsManagement(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            redirectAttributes.addAttribute("error", "Please login as an administrator to access this page");
            return "redirect:/auth/admin/login";
        }

        // Get current user
        UserDTO currentUser = sessionService.getCurrentUser(session);
        model.addAttribute("user", currentUser);

        // Load service items management data

        return "admin/service-items";
    }

    /**
     * Display vehicles management page
     */
    @GetMapping("/vehicles")
    public String vehiclesManagement(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            redirectAttributes.addAttribute("error", "Please login as an administrator to access this page");
            return "redirect:/auth/admin/login";
        }

        // Get current user
        UserDTO currentUser = sessionService.getCurrentUser(session);
        model.addAttribute("user", currentUser);

        // Load vehicles management data

        return "admin/vehicles";
    }

    /**
     * Display invoices management page
     */
    @GetMapping("/invoices")
    public String invoicesManagement(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            redirectAttributes.addAttribute("error", "Please login as an administrator to access this page");
            return "redirect:/auth/admin/login";
        }

        // Get current user
        UserDTO currentUser = sessionService.getCurrentUser(session);
        model.addAttribute("user", currentUser);

        // Load invoices management data

        return "admin/invoices";
    }

    /**
     * Display reports page
     */
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            redirectAttributes.addAttribute("error", "Please login as an administrator to access this page");
            return "redirect:/auth/admin/login";
        }

        // Get current user
        UserDTO currentUser = sessionService.getCurrentUser(session);
        model.addAttribute("user", currentUser);

        // Load reports data

        return "admin/reports";
    }

    /**
     * Display settings page
     */
    @GetMapping("/settings")
    public String settings(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and has admin role
        if (!sessionService.isLoggedIn(session) || !sessionService.isAdmin(session)) {
            redirectAttributes.addAttribute("error", "Please login as an administrator to access this page");
            return "redirect:/auth/admin/login";
        }

        // Get current user
        UserDTO currentUser = sessionService.getCurrentUser(session);
        model.addAttribute("user", currentUser);

        return "admin/settings";
    }
}