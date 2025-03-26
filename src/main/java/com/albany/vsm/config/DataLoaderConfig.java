package com.albany.vsm.config;

import com.albany.vsm.entity.User;
import com.albany.vsm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration to load initial data on application startup
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoaderConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initialize default admin account if no admin exists
     */
    @Bean
    @Profile("!test") // Skip this during testing
    public CommandLineRunner initializeData() {
        return args -> {
            if (!userRepository.existsByEmail("admin@albany.com")) {
                log.info("Creating default admin account...");

                User admin = new User();
                admin.setName("System Administrator");
                admin.setEmail("admin@albany.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Default password
                admin.setRole("admin");

                userRepository.save(admin);

                log.info("Default admin account created.");
            }

            // Count users by role
            long adminCount = userRepository.findAll().stream()
                    .filter(user -> "admin".equals(user.getRole()))
                    .count();

            long advisorCount = userRepository.findAll().stream()
                    .filter(user -> "serviceadvisor".equals(user.getRole()))
                    .count();

            long customerCount = userRepository.findAll().stream()
                    .filter(user -> "customer".equals(user.getRole()))
                    .count();

            log.info("User statistics - Admins: {}, Service Advisors: {}, Customers: {}",
                    adminCount, advisorCount, customerCount);
        };
    }
}