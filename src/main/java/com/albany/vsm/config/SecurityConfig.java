package com.albany.vsm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration
 * For simplicity, we're disabling CSRF and allowing all requests
 * In a production environment, set up proper security rules
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF protection for API
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()  // Allow all requests without authentication
            );
        
        return http.build();
    }
}