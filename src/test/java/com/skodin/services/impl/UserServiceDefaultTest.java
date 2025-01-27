package com.skodin.services.impl;

import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.exceptions.NotFoundException;
import com.skodin.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceDefaultTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceDefault userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserEntity result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(result, user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        String email = "test@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserEntity result = userService.findByEmail(email);

        assertNotNull(result);
        assertEquals(result, user);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_shouldThrowNotFoundException_whenUserDoesNotExist() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByActivationCode_shouldReturnUser_whenUserExists() {
        String code = "activation-code";
        UserEntity user = new UserEntity();
        user.setActivationCode(code);
        when(userRepository.findByActivationCode(code)).thenReturn(Optional.of(user));

        UserEntity result = userService.findByActivationCode(code);

        assertNotNull(result);
        assertEquals(result, user);
        verify(userRepository, times(1)).findByActivationCode(code);
    }

    @Test
    void findByActivationCode_shouldThrowNotFoundException_whenUserDoesNotExist() {
        String code = "activation-code";
        when(userRepository.findByActivationCode(code)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByActivationCode(code));
        verify(userRepository, times(1)).findByActivationCode(code);
    }

    @Test
    void saveAndFlush_shouldSaveUserSuccessfully() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(userRepository.saveAndFlush(user)).thenReturn(user);

        UserEntity result = userService.saveAndFlush(user);

        assertNotNull(result);
        assertEquals(result, user);
        verify(userRepository, times(1)).saveAndFlush(user);
    }

    @Test
    void updateEnable_shouldSetActivationCodeToEnableAndSaveUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setActivationCode("old-code");
        when(userRepository.save(user)).thenReturn(user);

        userService.updateEnable(user);

        assertNotNull(user.getActivationCode());
        assertEquals(user.getActivationCode(), "enable");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findFirstByRole_shouldReturnUser_whenUserExists() {
        Role role = Role.USER;
        UserEntity user = new UserEntity();
        user.setRole(role);
        when(userRepository.findFirstByRole(role)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.findFirstByRole(role);

        assertThat(result).isPresent();
        assertEquals(result.get(), user);
        verify(userRepository, times(1)).findFirstByRole(role);
    }

    @Test
    void findFirstByRole_shouldReturnEmpty_whenUserDoesNotExist() {
        Role role = Role.USER;
        when(userRepository.findFirstByRole(role)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userService.findFirstByRole(role);

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findFirstByRole(role);
    }
}
