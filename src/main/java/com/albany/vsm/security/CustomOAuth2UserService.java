package com.albany.vsm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import com.albany.vsm.service.UserService;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // Extract email from OAuth2 user info
        String email = getEmail(oAuth2User);
        
        if (email == null) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }
        
        // Check if user already exists in our system
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        
        if (userOptional.isPresent()) {
            // Update existing user with info from OAuth2
            user = userOptional.get();
            user = updateExistingUser(user, oAuth2User);
        } else {
            // Register as a new user
            user = registerNewUser(userRequest, oAuth2User);
        }
        
        return new CustomUserPrincipal(user, oAuth2User.getAttributes());
    }
    
    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        User user = new User();
        
        // Set user properties from OAuth2 info
        user.setName(getName(oAuth2User));
        user.setEmail(getEmail(oAuth2User));
        user.setPassword(""); // OAuth2 users don't need a password
        user.setRole(User.UserRole.CUSTOMER); // Default role
        
        return userService.registerUser(user);
    }
    
    private User updateExistingUser(User user, OAuth2User oAuth2User) {
        user.setName(getName(oAuth2User));
        return userRepository.save(user);
    }
    
    // Helper methods to extract info from OAuth2 user attributes
    private String getEmail(OAuth2User oAuth2User) {
        return (String) oAuth2User.getAttributes().get("email");
    }
    
    private String getName(OAuth2User oAuth2User) {
        return (String) oAuth2User.getAttributes().get("name");
    }
}