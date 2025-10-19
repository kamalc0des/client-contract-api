package com.apifactory.clientcontractapi.controller;

import com.apifactory.clientcontractapi.security.JwtService;
import com.apifactory.clientcontractapi.web.LoginRequest;
import com.apifactory.clientcontractapi.web.LoginResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates user credentials and returns a signed JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        logger.info("Attempting login for username {}", request.getUsername());

        // Authenticate credentials (will throw on bad credentials)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Generate token if authentication succeeds
        String token = jwtService.generateToken(request.getUsername());
        logger.info("Login success for username {}", request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}
