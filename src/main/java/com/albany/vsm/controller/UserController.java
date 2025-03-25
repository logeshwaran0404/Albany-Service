package com.albany.vsm.controller;

import com.albany.vsm.dto.InitialLoginRequest;
import com.albany.vsm.dto.InitialRegistrationRequest;
import com.albany.vsm.dto.LoginResponse;
import com.albany.vsm.dto.RegistrationResponse;
import com.albany.vsm.dto.VerifyOtpRequest;
import com.albany.vsm.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Step 1 of registration: Collect user info and send OTP
     */
    @PostMapping("/register/init")
    public ResponseEntity<?> initiateRegistration(@Valid @RequestBody InitialRegistrationRequest request) {
        boolean success = userService.initiateRegistration(request);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("status", 200);
        response.put("message", "OTP sent to your email");
        response.put("email", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2 of registration: Verify OTP and create user
     */
    @PostMapping("/register/verify")
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody VerifyOtpRequest request) {
        RegistrationResponse response = userService.completeRegistration(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 1 of login: Request OTP by providing email
     */
    @PostMapping("/login/init")
    public ResponseEntity<?> initiateLogin(@Valid @RequestBody InitialLoginRequest request) {
        boolean success = userService.initiateLogin(request);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("status", 200);
        response.put("message", "OTP sent to your email");
        response.put("email", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2 of login: Verify OTP
     */
    @PostMapping("/login/verify")
    public ResponseEntity<?> completeLogin(@Valid @RequestBody VerifyOtpRequest request) {
        LoginResponse response = userService.completeLogin(request);
        return ResponseEntity.ok(response);
    }
}