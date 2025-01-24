package com.skodin.services;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public interface UserService {
    UserEntity findById(Long aLong);

    UserEntity findByEmail(String email);

    UserEntity findByActivationCode(String code);

    @Transactional
    <S extends UserEntity> S saveAndFlush(S entity);

    @Transactional
    void updateEnable(UserEntity user);

    Optional<UserEntity> findFirstByRole(Role role);
}
