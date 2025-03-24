package com.albany.vsm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import com.albany.vsm.service.UserService;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        
        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        // Extract email from OIDC user info
        String email = oidcUser.getEmail();
        
        if (email == null) {
            throw new RuntimeException("Email not found from OIDC provider");
        }
        
        // Check if user already exists in our system
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        
        if (userOptional.isPresent()) {
            // Update existing user with info from OIDC
            user = userOptional.get();
            user = updateExistingUser(user, oidcUser);
        } else {
            // Register as a new user
            user = registerNewUser(userRequest, oidcUser);
        }
        
        return new CustomOidcUserPrincipal(user, oidcUser);
    }
    
    private User registerNewUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        User user = new User();
        
        // Set user properties from OIDC info
        user.setName(oidcUser.getFullName());
        user.setEmail(oidcUser.getEmail());
        user.setPassword(""); // OIDC users don't need a password
        user.setRole(User.UserRole.CUSTOMER); // Default role
        
        return userService.registerUser(user);
    }
    
    private User updateExistingUser(User user, OidcUser oidcUser) {
        user.setName(oidcUser.getFullName());
        return userRepository.save(user);
    }
}