package com.albany.vsm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration
 * Disables default security login and allows all requests
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF protection for API
            .formLogin(form -> form.disable())  // Disable the default login form completely
            .httpBasic(basic -> basic.disable())  // Disable HTTP Basic
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll()  // Allow all requests without authentication
            );
        
        return http.build();
    }
}