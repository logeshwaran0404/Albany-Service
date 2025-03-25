package com.albany.vsm.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Configuration for cache management
 * Used for OTP storage with a 5-minute expiration time
 */
@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    /**
     * Cache manager for OTP and registration data storage
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                "otpCache",
                "registrationCache"
        );
        return cacheManager;
    }

    /**
     * Schedule a task to clear the OTP cache every 5 minutes
     * This simulates expiration since ConcurrentMapCache doesn't support TTL
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void evictExpiredOtps() {
        // This will clear the entire cache every 5 minutes
        ((ConcurrentMapCacheManager) cacheManager()).getCache("otpCache").clear();
        ((ConcurrentMapCacheManager) cacheManager()).getCache("registrationCache").clear();
    }
}