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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public boolean register(RegisterRequest request) {
        LOGGER.info("Attempt to register user: {}", request.getEmail());
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BadRequestException("User already exists");
            }

            UserEntity user = new UserEntity
                    (0L, request.getEmail(), request.getPassword(), request.getRole(), UUID.randomUUID().toString());

            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userService.saveAndFlush(user);

            LOGGER.info("User: {} registered", request.getEmail());
            return true;
        } catch (Exception e) {
            LOGGER.error("Exception while registration user {}", request.getEmail());
            throw e;
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        LOGGER.info("Attempt to authenticate user: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword())
            );
        } catch (AuthenticationException e) {
            if (e instanceof DisabledException) {
                LOGGER.warn("authentication for user : {} failed. Account is disable", request.getEmail());
                throw new AccountDisableException("Account is disable");
            }
            throw e;
        }

        UserEntity user = userService
                .findByEmail(request.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        LOGGER.info("User: {} authenticated", request.getEmail());
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refresh(String refreshToken) {
        LOGGER.info("Attempt to refresh token: {}", refreshToken);
        return jwtService.refreshUserToken(refreshToken);
    }

    public Boolean enable(String code) {
        LOGGER.info("Attempt to enable code: {}", code);

        if (code == null) {
            throw new BadRequestException("code cannot be null");
        }

        UserEntity user = userService.findByActivationCode(code);

        userService.updateEnable(user);
        LOGGER.info("User: {} successfully enabled", user.getEmail());
        return true;
    }
}
