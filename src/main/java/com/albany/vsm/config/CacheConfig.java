package com.albany.vsm.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration for cache management
 * Used for OTP storage with a 5-minute expiration time
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager for OTP storage
     * OTPs are stored in-memory with a 5-minute expiration
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("otpCache");
    }
}