package com.skodin.services.auth;

import com.skodin.DTOs.auth.AuthenticationRequest;
import com.skodin.DTOs.auth.AuthenticationResponse;
import com.skodin.DTOs.auth.RegisterRequest;
import com.skodin.entities.UserEntity;
import com.skodin.exceptions.AccountDisableException;
import com.skodin.exceptions.BadRequestException;
import com.skodin.repositories.UserRepository;
import com.skodin.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public boolean register(RegisterRequest request) {
        log.info("Attempt to register user: {}", request.getEmail());
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BadRequestException("User already exists");
            }

            UserEntity user = new UserEntity
                    (0L, request.getEmail(), request.getPassword(), request.getRole(), UUID.randomUUID().toString());

            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userService.saveAndFlush(user);

            log.info("User: {} registered", request.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Exception while registration user {}", request.getEmail());
            throw e;
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Attempt to authenticate user: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword())
            );
        } catch (AuthenticationException e) {
            if (e instanceof DisabledException) {
                log.warn("authentication for user : {} failed. Account is disable", request.getEmail());
                throw new AccountDisableException("Account is disable");
            }
            throw e;
        }

        UserEntity user = userService
                .findByEmail(request.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User: {} authenticated", request.getEmail());
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refresh(String refreshToken) {
        log.info("Attempt to refresh token: {}", refreshToken);
        return jwtService.refreshUserToken(refreshToken);
    }

    public Boolean enable(String code) {
        log.info("Attempt to enable code: {}", code);

        if (code == null) {
            throw new BadRequestException("code cannot be null");
        }

        UserEntity user = userService.findByActivationCode(code);

        userService.updateEnable(user);
        log.info("User: {} successfully enabled", user.getEmail());
        return true;
    }
}
