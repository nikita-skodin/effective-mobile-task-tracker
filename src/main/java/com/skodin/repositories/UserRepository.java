package com.skodin.repositories;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByActivationCode(String code);

    Optional<UserEntity> findFirstByRole(Role role);
}
