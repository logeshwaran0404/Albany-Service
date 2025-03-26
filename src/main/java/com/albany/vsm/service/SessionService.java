package com.albany.vsm.service;

import com.albany.vsm.dto.AuthDTO.UserDTO;
import com.albany.vsm.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 * Service to manage user session data
 */
@Service
public class SessionService {

    private static final String USER_KEY = "currentUser";
    private static final int SESSION_TIMEOUT_SHORT = 1800; // 30 minutes
    private static final int SESSION_TIMEOUT_LONG = 86400; // 24 hours
    
    /**
     * Store user in session after successful login
     * @param session Http session
     * @param user User entity
     * @param rememberMe Whether to keep session for extended period
     */
    public void storeUserInSession(HttpSession session, User user, boolean rememberMe) {
        UserDTO userDTO = convertToDTO(user);
        session.setAttribute(USER_KEY, userDTO);
        
        // Set session timeout based on rememberMe flag
        int timeout = rememberMe ? SESSION_TIMEOUT_LONG : SESSION_TIMEOUT_SHORT;
        session.setMaxInactiveInterval(timeout);
    }
    
    /**
     * Get current user from session
     * @param session Http session
     * @return UserDTO or null if not logged in
     */
    public UserDTO getCurrentUser(HttpSession session) {
        return (UserDTO) session.getAttribute(USER_KEY);
    }
    
    /**
     * Check if current session has a logged in user
     * @param session Http session
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(USER_KEY) != null;
    }
    
    /**
     * Check if current user in session has admin role
     * @param session Http session
     * @return true if admin, false otherwise
     */
    public boolean isAdmin(HttpSession session) {
        UserDTO user = getCurrentUser(session);
        return user != null && "admin".equals(user.getRole());
    }
    
    /**
     * Check if current user in session has serviceadvisor role
     * @param session Http session
     * @return true if service advisor, false otherwise
     */
    public boolean isServiceAdvisor(HttpSession session) {
        UserDTO user = getCurrentUser(session);
        return user != null && "serviceadvisor".equals(user.getRole());
    }
    
    /**
     * Check if current user in session has customer role
     * @param session Http session
     * @return true if customer, false otherwise
     */
    public boolean isCustomer(HttpSession session) {
        UserDTO user = getCurrentUser(session);
        return user != null && "customer".equals(user.getRole());
    }
    
    /**
     * Invalidate session on logout
     * @param session Http session
     */
    public void logout(HttpSession session) {
        session.invalidate();
    }
    
    /**
     * Convert User entity to UserDTO
     * @param user User entity
     * @return UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().toString()
        );
    }
}