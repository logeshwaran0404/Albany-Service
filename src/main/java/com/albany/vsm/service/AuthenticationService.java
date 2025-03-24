package com.albany.vsm.service;

import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import com.albany.vsm.dto.RegistrationRequest;
import com.albany.vsm.dto.OtpRequest;
import com.albany.vsm.dto.OtpVerificationRequest;
import com.albany.vsm.exception.UserAlreadyExistsException;
import com.albany.vsm.exception.InvalidOtpException;
import com.albany.vsm.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final OAuthService oAuthService;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            OtpService otpService,
            SmsService smsService,
            PasswordEncoder passwordEncoder,
            OAuthService oAuthService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.smsService = smsService;
        this.passwordEncoder = passwordEncoder;
        this.oAuthService = oAuthService;
    }

    /**
     * Initiate customer registration by sending OTP
     */
    public boolean initiateRegistration(RegistrationRequest request) {
        // Check if user already exists with this mobile number
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserAlreadyExistsException("User with this mobile number already exists");
        }
        
        // Generate OTP
        String otp = otpService.generateOTP(request.getMobileNumber());
        
        // Send OTP to mobile number
        return smsService.sendOtp(request.getMobileNumber(), otp);
    }

    /**
     * Complete customer registration after OTP verification
     */
    @Transactional
    public User completeRegistration(OtpVerificationRequest request) {
        // Verify OTP
        if (!otpService.validateOTP(request.getMobileNumber(), request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
        
        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setDefaultPassword(); // Generate secure default password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.UserRole.customer);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Generate OAuth token (return will be handled by controller)
        oAuthService.generateToken(savedUser);
        
        return savedUser;
    }

    /**
     * Initiate login by sending OTP
     */
    public boolean initiateLogin(OtpRequest request) {
        // Check if user exists with this mobile number
        if (!userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new UserNotFoundException("No user found with this mobile number");
        }
        
        // Generate OTP
        String otp = otpService.generateOTP(request.getMobileNumber());
        
        // Send OTP to mobile number
        return smsService.sendOtp(request.getMobileNumber(), otp);
    }

    /**
     * Complete login after OTP verification
     */
    public String completeLogin(OtpVerificationRequest request) {
        // Verify OTP
        if (!otpService.validateOTP(request.getMobileNumber(), request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
        
        // Find user
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseThrow(() -> new UserNotFoundException("No user found with this mobile number"));
        
        // Generate and return OAuth token
        return oAuthService.generateToken(user);
    }
}