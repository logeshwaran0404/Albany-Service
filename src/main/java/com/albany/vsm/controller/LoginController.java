package com.albany.vsm.controller;

import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        Model model,
                        HttpSession session) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check password
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Store user in session
                session.setAttribute("user", user);

                // Route based on role
                switch (user.getRole()) {
                    case admin:
                        return "redirect:/admin/dashboard";
                    case serviceadvisor:
                        return "redirect:/serviceadvisor/dashboard";
                    case customer:
                        return "redirect:/customer/dashboard";
                    default:
                        model.addAttribute("error", "Invalid user role");
                        return "auth/login";
                }
            }
        }

        // Login failed
        model.addAttribute("error", "Invalid email or password");
        return "auth/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        // Check if user is in session and is an admin
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals(User.Role.admin)) {
            return "redirect:/login";
        }

        // Add any dashboard-specific data
        model.addAttribute("adminUser", user);
        return "auth/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}