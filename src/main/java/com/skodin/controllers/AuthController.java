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
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody String refreshToken
    ) {
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

    @GetMapping("/enable/{code}")
    public ResponseEntity<String> enable(
            @PathVariable String code) {
        boolean activationResult = authenticationService.enable(code);
        if (activationResult) {
            return ResponseEntity.ok("Account successfully activated");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation code");
        }
    }

}

