package com.albany.vsm.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for cache management
 * Used for OTP storage with a 5-minute expiration time
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Create and configure the cache manager for OTP and registration caches
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Set cache names
        cacheManager.setCacheNames(Arrays.asList("otpCache", "registrationCache"));
        
        // Configure Caffeine
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(100)
                .maximumSize(1000));
        
        return cacheManager;
    }
}