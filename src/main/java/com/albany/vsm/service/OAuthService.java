package com.albany.vsm.service;

import com.albany.vsm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling OAuth2 token generation and validation
 * In a production environment, this would integrate with a proper OAuth2 server
 * This implementation is simplified for demonstration purposes
 */
@Service
public class OAuthService {

    @Value("${oauth.token.validity.seconds:3600}")
    private long tokenValiditySeconds;
    
    // In-memory token store (in production, use Redis or another distributed cache)
    private final Map<String, TokenInfo> tokenStore = Collections.synchronizedMap(new HashMap<>());
    
    /**
     * Generate an OAuth token for a user
     * @param user User to generate token for
     * @return OAuth access token
     */
    public String generateToken(User user) {
        String tokenValue = UUID.randomUUID().toString();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(tokenValiditySeconds);
        
        // Store token information
        TokenInfo tokenInfo = new TokenInfo(
                tokenValue,
                user.getId(),
                user.getRole().toString(),
                user.getMobileNumber(),
                issuedAt,
                expiresAt
        );
        
        tokenStore.put(tokenValue, tokenInfo);
        
        return tokenValue;
    }
    
    /**
     * Validate a token and return user information if valid
     * @param token Token to validate
     * @return TokenInfo if valid, null otherwise
     */
    public TokenInfo validateToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        
        if (tokenInfo != null && Instant.now().isBefore(tokenInfo.getExpiresAt())) {
            return tokenInfo;
        }
        
        // Remove expired token
        if (tokenInfo != null) {
            tokenStore.remove(token);
        }
        
        return null;
    }
    
    /**
     * Revoke a token
     * @param token Token to revoke
     */
    public void revokeToken(String token) {
        tokenStore.remove(token);
    }
    
    /**
     * Token information class
     */
    public static class TokenInfo {
        private final String token;
        private final Long userId;
        private final String role;
        private final String mobileNumber;
        private final Instant issuedAt;
        private final Instant expiresAt;
        
        public TokenInfo(String token, Long userId, String role, String mobileNumber, 
                         Instant issuedAt, Instant expiresAt) {
            this.token = token;
            this.userId = userId;
            this.role = role;
            this.mobileNumber = mobileNumber;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }
        
        public String getToken() {
            return token;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public String getRole() {
            return role;
        }
        
        public String getMobileNumber() {
            return mobileNumber;
        }
        
        public Instant getIssuedAt() {
            return issuedAt;
        }
        
        public Instant getExpiresAt() {
            return expiresAt;
        }
    }
}