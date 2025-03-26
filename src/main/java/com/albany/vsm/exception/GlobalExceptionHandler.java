package com.albany.vsm.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application
 * Handles exceptions in a consistent way
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation exceptions for REST endpoints
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errorResponse = new HashMap<>();

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        errorResponse.put("success", false);
        errorResponse.put("message", "Validation error");
        errorResponse.put("errors", errorMessage);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handle binding exceptions for form submissions
     */
    @ExceptionHandler(BindException.class)
    public ModelAndView handleBindExceptions(BindException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ModelAndView mav = new ModelAndView();
        mav.addObject("error", errorMessage);

        // Extract the view name from the request URI
        String viewName = determineViewName(request.getRequestURI());
        mav.setViewName(viewName);

        return mav;
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        // If request is an API call, return JSON response
        if (isApiCall(request)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // Otherwise, return an error view
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    /**
     * Handle unauthorized access exceptions
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {

        // If request is an API call, return JSON response
        if (isApiCall(request)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        // Otherwise, return an error view
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception occurred", ex);

        // If request is an API call, return JSON response
        if (isApiCall(request)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        // Otherwise, return an error view
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("message", "An unexpected error occurred. Please try again later.");
        return mav;
    }

    /**
     * Check if the request is an API call
     */
    private boolean isApiCall(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/") ||
                request.getHeader("Accept") != null &&
                        request.getHeader("Accept").contains("application/json");
    }

    /**
     * Determine the view name from the request URI
     */
    private String determineViewName(String uri) {
        // Extract the main part of the URI
        if (uri.contains("/admin/")) {
            return "admin/login";
        } else if (uri.contains("/advisor/")) {
            return "advisor/login";
        } else if (uri.contains("/customer/")) {
            return "customer/login";
        }

        // Default to home page
        return "redirect:/";
    }
}