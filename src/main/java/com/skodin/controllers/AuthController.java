package com.skodin.controllers;

import com.skodin.DTOs.auth.AuthenticationRequest;
import com.skodin.DTOs.auth.AuthenticationResponse;
import com.skodin.DTOs.auth.RegisterRequest;
import com.skodin.services.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController extends MainController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @PreAuthorize("@CheckPermissionsService.permitAdmin(#userDetails)")
    public ResponseEntity<Boolean> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ) {
        log.info("Attempting to register a new user by admin: {}", userDetails.getUsername());
        try {
            boolean result = authenticationService.register(request);
            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        log.info("Attempting to authenticate user: {}", request.getEmail());
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            log.info("User authenticated successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Authentication failed for user: {}. Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody String refreshToken
    ) {
        log.info("Attempting to refresh token");
        try {
            AuthenticationResponse response = authenticationService.refresh(refreshToken);
            log.info("Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage(), e);
            throw e;
        }
    }

    // for implement the activation mechanism
    @GetMapping("/enable/{code} ")
    public ResponseEntity<String> enable(
            @PathVariable String code) {
        log.info("Attempting to enable account with activation code: {}", code);
        try {
            boolean activationResult = authenticationService.enable(code);
            if (activationResult) {
                log.info("Account successfully activated with code: {}", code);
                return ResponseEntity.ok("Account successfully activated");
            } else {
                log.warn("Invalid activation code: {}", code);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation code");
            }
        } catch (Exception e) {
            log.error("Error during account activation: {}", e.getMessage(), e);
            throw e;
        }
    }

}
