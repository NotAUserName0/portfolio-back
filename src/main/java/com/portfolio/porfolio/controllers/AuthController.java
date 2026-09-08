package com.portfolio.porfolio.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import com.portfolio.porfolio.dto.UserDto;
import com.portfolio.porfolio.service.user.AuthService;
import com.portfolio.porfolio.utils.*;

/**
 * @description Controller handling user authentication with input validation.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * @description Authenticates the user credentials.
     *              The @Valid annotation enforces validation constraints on the UserDto payload before processing.
     * @param user The validated login credentials payload.
     * @return Standardized API response containing authentication token or error status.
     */
    @PostMapping(consumes= "application/json")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody UserDto user) {
        return authService.loginService(user).toResponseEntity();
    }
}
