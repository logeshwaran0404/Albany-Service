package com.albany.vsm.dto;

import com.albany.vsm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRegistrationRequest {
        private String name;
        private String email;
        private String mobileNumber;
        private String password;
        private User.UserRole role;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtpLoginRequest {
        private String mobileNumber;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtpVerificationRequest {
        private String mobileNumber;
        private String otp;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailLoginRequest {
        private String email;
        private String password;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String mobileNumber;
        private User.UserRole role;
        private String token; // JWT token or session ID
        
        public static UserResponse fromUser(User user) {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setMobileNumber(user.getMobileNumber());
            response.setRole(user.getRole());
            return response;
        }
    }
}