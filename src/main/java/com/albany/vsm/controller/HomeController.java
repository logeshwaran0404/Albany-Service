package com.albany.vsm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Controller to serve the HTML pages
 */
@Controller
public class HomeController {

    /**
     * Serve the login page on root path
     */
    @GetMapping("/")
    public String home() {
        return "login";  // This will serve login.html from templates folder
    }

    /**
     * Serve the login page on /login path
     */
    @GetMapping("/login")
    public String login() {
        return "login";  // This will also serve login.html from templates folder
    }

    /**
     * Redirect to dashboard page after successful login
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";  // You'll need to create this template
    }

    /**
     * Suppress favicon errors
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFoundException() {
        // Just suppressing the exception
    }
}