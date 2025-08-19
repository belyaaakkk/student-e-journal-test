package com.belyak.test.presentation.controller;

import com.belyak.test.domain.application.auth.AuthenticationService;
import com.belyak.test.presentation.dto.AuthenticationRequest;
import com.belyak.test.presentation.dto.AuthenticationResponse;
import com.belyak.test.presentation.dto.RefreshTokenRequest;
import com.belyak.test.presentation.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and JWT management")
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Register a new user", description = "Creates a new account with email, username and password")
    @ApiResponse(responseCode = "200", description = "Successfully registered")
    @ApiResponse(responseCode = "400", description = "Validation error or user already exists")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Login", description = "Authenticate user with email and password")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Refresh JWT", description = "Get a new access/refresh token pair using a valid refresh token")
    @ApiResponse(responseCode = "200", description = "New tokens generated")
    @ApiResponse(responseCode = "400", description = "Invalid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(service.refreshToken(request.getRefreshToken()));
    }
}
