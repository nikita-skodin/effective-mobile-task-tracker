package com.skodin.services.impl;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.exceptions.NotFoundException;
import com.skodin.repositories.UserRepository;
import com.skodin.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class UserServiceDefault implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity findById(Long aLong) {
        log.info("Finding user with id: {}", aLong);
        return userRepository.findById(aLong).orElseThrow(
                () -> {
                    log.error("User with id: {} not found", aLong);
                    return new NotFoundException(String.format("User with id %d is not found", aLong));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        log.info("Finding user with email: {}", email);
        return userRepository.findByEmail(email).orElseThrow(
                () -> {
                    log.error("User with email: {} not found", email);
                    return new NotFoundException(String.format("User with email %s not found", email));
                });
    }

    @Override
    public UserEntity findByActivationCode(String code) {
        log.info("Finding user with activation code: {}", code);
        return userRepository.findByActivationCode(code).orElseThrow(
                () -> {
                    log.error("User with activation code: {} not found", code);
                    return new NotFoundException(String.format("User with activation code %s not found", code));
                });
    }

    @Override
    @Transactional
    public <S extends UserEntity> S saveAndFlush(S entity) {
        log.info("Saving user: {}", entity);
        return userRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void updateEnable(UserEntity user) {
        log.info("Enabling user with id: {}", user.getId());
        user.setActivationCode("enable");
        userRepository.save(user);
        log.info("User with id: {} enabled successfully", user.getId());
    }

    @Override
    public Optional<UserEntity> findFirstByRole(Role role) {
        return userRepository.findFirstByRole(role);
    }
}
