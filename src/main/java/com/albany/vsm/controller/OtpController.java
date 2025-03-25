package com.albany.vsm.controller;

import com.albany.vsm.dto.OtpRequestDto;
import com.albany.vsm.dto.OtpResponseDto;
import com.albany.vsm.dto.OtpVerificationDto;
import com.albany.vsm.exception.EmailSendingException;
import com.albany.vsm.exception.OtpGenerationException;
import com.albany.vsm.exception.OtpVerificationException;
import com.albany.vsm.exception.UserNotFoundException;
import com.albany.vsm.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for OTP-related endpoints
 */
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    /**
     * Generate and send OTP via email
     */
    @PostMapping("/send")
    public ResponseEntity<OtpResponseDto> sendOtp(@RequestBody OtpRequestDto request) {
        try {
            otpService.sendOtp(request.getEmail());
            return ResponseEntity.ok(
                OtpResponseDto.success("OTP sent successfully to " + request.getEmail())
            );
        } catch (UserNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(OtpResponseDto.failure(e.getMessage()));
        } catch (EmailSendingException | OtpGenerationException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OtpResponseDto.failure(e.getMessage()));
        }
    }

    /**
     * Verify OTP provided by user
     */
    @PostMapping("/verify")
    public ResponseEntity<OtpResponseDto> verifyOtp(@RequestBody OtpVerificationDto request) {
        try {
            boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
            
            if (isValid) {
                return ResponseEntity.ok(
                    OtpResponseDto.success("OTP verified successfully")
                );
            } else {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(OtpResponseDto.failure("Invalid OTP"));
            }
        } catch (OtpVerificationException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(OtpResponseDto.failure(e.getMessage()));
        }
    }

    /**
     * Global exception handler for OTP-related exceptions
     */
    @ExceptionHandler({OtpGenerationException.class, OtpVerificationException.class, 
                      EmailSendingException.class, UserNotFoundException.class})
    public ResponseEntity<OtpResponseDto> handleOtpExceptions(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (e instanceof UserNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (e instanceof OtpVerificationException) {
            status = HttpStatus.BAD_REQUEST;
        }
        
        return ResponseEntity
            .status(status)
            .body(OtpResponseDto.failure(e.getMessage()));
    }
}