package com.skodin.services;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class SuperAdminCreateService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String EMAIl = "admin@gmail.com";
    private final String PASSWORD = "rootroot";
    private final UserService userService;

    @PostConstruct
    void createAdmin() {
        if (userRepository.findFirstByRole(Role.ADMIN).isEmpty()) {
            UserEntity user = new UserEntity
                    (0L, EMAIl, passwordEncoder.encode(PASSWORD), Role.ADMIN, "enable");

            userService.saveAndFlush(user);
        }
    }
}