package com.albany.vsm.service;

import com.albany.vsm.model.User;
import com.albany.vsm.repository.UserRepository;
import com.albany.vsm.util.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SmsService smsService;

    // Cache to store OTPs (In production, use Redis or another distributed cache)
    private final java.util.Map<String, User> otpUserMap = new java.util.concurrent.ConcurrentHashMap<>();

    @Transactional
    public User registerUser(User user) {
        // Check if user already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (user.getMobileNumber() != null && userRepository.existsByMobileNumber(user.getMobileNumber())) {
            throw new RuntimeException("Mobile number already registered");
        }

        // Encode password if not empty (for OAuth users, password might be empty)
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Set default role as CUSTOMER if not specified
        if (user.getRole() == null) {
            user.setRole(User.UserRole.CUSTOMER);
        }

        return userRepository.save(user);
    }

    public boolean initiateOtpLogin(String mobileNumber) {
        Optional<User> userOpt = userRepository.findByMobileNumber(mobileNumber);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        String otp = OtpUtil.generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());

        // Store OTP in cache
        otpUserMap.put(mobileNumber, user);

        // Send OTP via SMS - using real SMS gateway
        return smsService.sendOtp(mobileNumber, otp);
    }

    public User verifyOtp(String mobileNumber, String otp) {
        User user = otpUserMap.get(mobileNumber);

        if (user == null) {
            throw new RuntimeException("No OTP request found for this mobile number");
        }

        if (OtpUtil.isOtpExpired(user.getOtpGeneratedTime())) {
            otpUserMap.remove(mobileNumber);
            throw new RuntimeException("OTP has expired");
        }

        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // OTP verified successfully, remove from cache
        otpUserMap.remove(mobileNumber);

        return userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // For admin and service advisor login with email/password
    public User authenticateUser(String email, String password) {
        User user = findByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}