package com.albany.vsm.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth2.redirectUri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        // Get authenticated user
        CustomUserPrincipal userPrincipal = null;
        if (authentication.getPrincipal() instanceof CustomUserPrincipal) {
            userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof CustomOidcUserPrincipal) {
            userPrincipal = ((CustomOidcUserPrincipal) authentication.getPrincipal()).getCustomUserPrincipal();
        }
        
        if (userPrincipal == null) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // Create target URL with user role to redirect based on role
        String targetUrl = determineTargetUrl(userPrincipal);
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(CustomUserPrincipal userPrincipal) {
        // Add user info as query parameters to redirect URI
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("userId", userPrincipal.getUser().getId())
                .queryParam("role", userPrincipal.getUser().getRole())
                .build().toUriString();
    }
}