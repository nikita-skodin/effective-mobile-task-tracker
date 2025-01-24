package com.skodin.repositories;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @QueryHints(@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"))
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByActivationCode(String code);

    Optional<UserEntity> findFirstByRole(Role role);
}
