package com.albany.vsm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for API endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allow all auth-related endpoints
                        .requestMatchers("/auth/**", "/api/auth/**", "/").permitAll()
                        // Allow static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**").permitAll()
                        // Protect admin endpoints
                        .requestMatchers("/admin/**").hasRole("admin")
                        // Protect customer endpoints
                        .requestMatchers("/customer/**").hasRole("customer")
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                // Not using form login, we have our own login pages
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}