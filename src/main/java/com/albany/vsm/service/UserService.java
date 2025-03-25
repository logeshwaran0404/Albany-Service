package com.albany.vsm.service;

import com.albany.vsm.dto.InitialLoginRequest;
import com.albany.vsm.dto.InitialRegistrationRequest;
import com.albany.vsm.dto.LoginResponse;
import com.albany.vsm.dto.RegistrationResponse;
import com.albany.vsm.dto.VerifyOtpRequest;
import com.albany.vsm.exception.UserException;
import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final CacheManager cacheManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       OtpService otpService, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.cacheManager = cacheManager;
    }

    /**
     * First step of registration: store user data in cache and send OTP
     */
    public boolean initiateRegistration(InitialRegistrationRequest request) {
        // Check if email or mobile already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email already registered");
        }
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserException("Mobile number already registered");
        }

        // Store registration data in cache
        Cache registrationCache = cacheManager.getCache("registrationCache");
        registrationCache.put(request.getEmail(), request);

        // Generate and send OTP
        otpService.generateAndSendOtp(request.getEmail());
        return true;
    }

    /**
     * Second step of registration: verify OTP and create user
     */
    @Transactional
    public RegistrationResponse completeRegistration(VerifyOtpRequest request) {
        // Verify OTP
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            throw new UserException("Invalid or expired OTP");
        }

        // Get registration data from cache
        Cache registrationCache = cacheManager.getCache("registrationCache");
        Cache.ValueWrapper valueWrapper = registrationCache.get(request.getEmail());

        if (valueWrapper == null) {
            throw new UserException("Registration session expired");
        }

        InitialRegistrationRequest registrationData =
                (InitialRegistrationRequest) valueWrapper.get();

        // Create user
        User user = new User();
        user.setName(registrationData.getName());
        user.setEmail(registrationData.getEmail());
        user.setMobileNumber(registrationData.getMobileNumber());
        user.setPassword(passwordEncoder.encode(registrationData.getPassword()));
        user.setRole(User.UserRole.customer);  // Default role for registration

        User savedUser = userRepository.save(user);

        // Clear cache
        otpService.clearOtp(request.getEmail());
        registrationCache.evict(request.getEmail());

        // Return response
        RegistrationResponse response = new RegistrationResponse();
        response.setUserId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setName(savedUser.getName());
        response.setMessage("Registration successful");

        return response;
    }

    /**
     * First step of login: send OTP to user email
     */
    public boolean initiateLogin(InitialLoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new UserException("User not found");
        }

        // Generate and send OTP
        otpService.generateAndSendOtp(request.getEmail());
        return true;
    }

    /**
     * Second step of login: verify OTP and create session
     */
    public LoginResponse completeLogin(VerifyOtpRequest request) {
        // Verify OTP
        if (!otpService.verifyOtp(request.getEmail(), request.getOtp())) {
            throw new UserException("Invalid or expired OTP");
        }

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("User not found"));

        // Clear OTP
        otpService.clearOtp(request.getEmail());

        // Generate session token (simple UUID for now)
        String token = UUID.randomUUID().toString();

        // Build response
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().toString());
        response.setToken(token);
        response.setMessage("Login successful");

        return response;
    }
}