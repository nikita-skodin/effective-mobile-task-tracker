package com.skodin.services;

import com.skodin.exceptions.ForbiddenException;
import com.skodin.exceptions.NotFoundException;
import com.skodin.entities.UserEntity;
import com.skodin.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public static UserEntity getCurrentUser() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return (UserEntity) principal;
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findById(Long aLong) {
        return userRepository.findById(aLong).orElseThrow(
                () -> new NotFoundException(String.format("User with id %d is not found", aLong)));
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(String.format("User with email %s not found", email)));
    }

    public UserEntity findByActivationCode(String code) {
        return userRepository.findByActivationCode(code).orElseThrow(
                () -> new NotFoundException(String.format("User with activation code %s not found", code)));
    }

    @Transactional
    public <S extends UserEntity> S saveAndFlush(S entity) {
        return userRepository.saveAndFlush(entity);
    }

    @Transactional
    public UserEntity updateEnable(UserEntity user) {
        user.setActivationCode("enable");
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void deleteById(Long aLong) {
        userRepository.deleteById(aLong);
    }

}
