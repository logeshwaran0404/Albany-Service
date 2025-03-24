package com.albany.vsm.security;

import com.albany.vsm.model.User;
import com.albany.vsm.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class OtpAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Only apply to OTP verification endpoint
        String path = request.getServletPath();
        if (!path.equals("/api/auth/login/otp/verify")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // Parse request body
            Map<String, String> body = objectMapper.readValue(request.getInputStream(), HashMap.class);
            String mobileNumber = body.get("mobileNumber");
            String otp = body.get("otp");
            
            // Verify OTP
            User user = userService.verifyOtp(mobileNumber, otp);
            
            // Create authentication object
            Authentication authentication = createAuthentication(user);
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Create success response
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("userId", user.getId());
            responseMap.put("role", user.getRole());
            
            // Write response
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            
        } catch (Exception e) {
            // Create error response
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("success", false);
            errorMap.put("message", e.getMessage());
            
            // Write error response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorMap));
        }
    }
    
    private Authentication createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                user.getEmail(), 
                null, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}