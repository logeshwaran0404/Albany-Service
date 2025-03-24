package com.albany.vsm.config;

import com.albany.vsm.security.OAuth2AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.CacheManager;

@Configuration
@EnableWebSecurity
@EnableCaching
public class SecurityConfig {

    @Autowired
    private OAuth2AuthenticationFilter oAuth2AuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers("/api/auth/register/**").permitAll()
                        .requestMatchers("/api/auth/login/**").permitAll()
                        // Restricted endpoints
                        .requestMatchers("/api/customer/**").hasRole("customer")
                        .requestMatchers("/api/admin/**").hasRole("admin")
                        .requestMatchers("/api/serviceadvisor/**").hasRole("serviceadvisor")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(oAuth2AuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("otpCache");
    }
}