package com.skodin.services.impl;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
class SuperAdminCreateService {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    private final String EMAIl = "admin@gmail.com";
    private final String PASSWORD = "rootroot";

    @PostConstruct
    void createAdmin() {
        log.info("Checking if an admin user already exists...");

        if (userService.findFirstByRole(Role.ADMIN).isEmpty()) {
            log.info("No admin user found. Creating a new admin user...");

            UserEntity user = new UserEntity(0L, EMAIl, passwordEncoder.encode(PASSWORD), Role.ADMIN, "enable");

            try {
                userService.saveAndFlush(user);
                log.info("Super Admin created successfully with email: {}", EMAIl);
            } catch (Exception e) {
                log.error("Failed to create Super Admin. Error: {}", e.getMessage(), e);
            }
        } else {
            log.info("An admin user already exists. Skipping creation.");
        }
    }
}
