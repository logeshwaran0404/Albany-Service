package com.albany.vsm.controller.mvc;

import com.albany.vsm.dto.OtpRequest;
import com.albany.vsm.dto.OtpVerificationRequest;
import com.albany.vsm.dto.RegistrationRequest;
import com.albany.vsm.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthMvcController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthMvcController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String initiateRegistration(
            @Valid @ModelAttribute("registrationRequest") RegistrationRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        try {
            boolean otpSent = authenticationService.initiateRegistration(request);
            
            if (otpSent) {
                // Store registration details in session for OTP verification
                session.setAttribute("pendingRegistration", request);
                
                // Prepare OTP verification form
                redirectAttributes.addFlashAttribute("mobileNumber", request.getMobileNumber());
                return "redirect:/verify-otp?action=register";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to send OTP. Please try again.");
                return "redirect:/register";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("otpRequest", new OtpRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String initiateLogin(
            @Valid @ModelAttribute("otpRequest") OtpRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        if (result.hasErrors()) {
            return "auth/login";
        }
        
        try {
            boolean otpSent = authenticationService.initiateLogin(request);
            
            if (otpSent) {
                // Store mobile number in session for OTP verification
                session.setAttribute("pendingLoginMobile", request.getMobileNumber());
                
                // Prepare OTP verification form
                redirectAttributes.addFlashAttribute("mobileNumber", request.getMobileNumber());
                return "redirect:/verify-otp?action=login";
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to send OTP. Please try again.");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/verify-otp")
    public String showOtpVerificationForm(
            @ModelAttribute("action") String action,
            @ModelAttribute("mobileNumber") String mobileNumber,
            Model model,
            HttpSession session) {
        
        // Verify session has pending action
        if ("register".equals(action) && session.getAttribute("pendingRegistration") == null) {
            return "redirect:/register";
        } else if ("login".equals(action) && session.getAttribute("pendingLoginMobile") == null) {
            return "redirect:/login";
        }
        
        OtpVerificationRequest verificationRequest = new OtpVerificationRequest();
        verificationRequest.setMobileNumber(mobileNumber);
        
        model.addAttribute("verificationRequest", verificationRequest);
        model.addAttribute("action", action);
        
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @Valid @ModelAttribute("verificationRequest") OtpVerificationRequest request,
            BindingResult result,
            @ModelAttribute("action") String action,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        
        if (result.hasErrors()) {
            return "auth/verify-otp";
        }
        
        try {
            if ("register".equals(action)) {
                // Get registration details from session
                RegistrationRequest registrationRequest = 
                        (RegistrationRequest) session.getAttribute("pendingRegistration");
                
                // Complete registration data
                request.setName(registrationRequest.getName());
                request.setEmail(registrationRequest.getEmail());
                
                // Complete registration
                authenticationService.completeRegistration(request);
                
                // Generate token and set in session
                String token = authenticationService.completeLogin(request);
                session.setAttribute("authToken", token);
                
                // Clean up session
                session.removeAttribute("pendingRegistration");
                
                redirectAttributes.addFlashAttribute("success", "Registration successful!");
                return "redirect:/dashboard";
                
            } else if ("login".equals(action)) {
                // Complete login
                String token = authenticationService.completeLogin(request);
                
                // Store token in session
                session.setAttribute("authToken", token);
                
                // Clean up session
                session.removeAttribute("pendingLoginMobile");
                
                redirectAttributes.addFlashAttribute("success", "Login successful!");
                return "redirect:/dashboard";
            }
            
            // Invalid action
            redirectAttributes.addFlashAttribute("error", "Invalid action");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("mobileNumber", request.getMobileNumber());
            return "redirect:/verify-otp?action=" + action;
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Clean up session
        session.invalidate();
        
        return "redirect:/login";
    }
}