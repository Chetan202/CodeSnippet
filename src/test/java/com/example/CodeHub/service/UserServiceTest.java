package com.example.CodeHub.service;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "adminEmail", "admin@example.com");
    }

    @Test
    void save_assignsUserRole_forNonAdminEmail() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto dto = new UserDto();
        dto.setEmail("alice@example.com");
        dto.setPassword("pass");
        User saved = userService.save(dto);

        assertThat(saved.getRole()).isEqualTo("USER");
        assertThat(saved.getEmail()).isEqualTo("alice@example.com");
        verify(passwordEncoder).encode("pass");
    }

    @Test
    void save_assignsAdminRole_forAdminEmail() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto dto = new UserDto();
        dto.setEmail("admin@example.com");
        dto.setPassword("pass");
        User saved = userService.save(dto);

        assertThat(saved.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void save_adminEmailCheck_isCaseInsensitive() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto dto = new UserDto();
        dto.setEmail("ADMIN@EXAMPLE.COM");
        dto.setPassword("pass");
        User saved = userService.save(dto);

        assertThat(saved.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void findByUsername_delegatesToRepository() {
        User user = new User("bob", "encoded", "bob@example.com");
        when(userRepository.findByUsername("bob")).thenReturn(user);

        User result = userService.findByUsername("bob");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findByUsername_returnsNull_whenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThat(userService.findByUsername("unknown")).isNull();
    }

    @Test
    void findByEmail_delegatesToRepository() {
        User user = new User("bob", "encoded", "bob@example.com");
        when(userRepository.findByEmail("bob@example.com")).thenReturn(user);

        User result = userService.findByEmail("bob@example.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void saveExisting_persistsUser() {
        User user = new User("bob", "encoded", "bob@example.com");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveExisting(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserRoles_grantsAdminToAdminEmail() {
        User adminUser = new User("admin", "encoded", "admin@example.com");
        adminUser.setRole("USER");
        User regularUser = new User("alice", "encoded", "alice@example.com");
        regularUser.setRole("USER");

        when(userRepository.findAll()).thenReturn(List.of(adminUser, regularUser));

        userService.updateUserRoles();

        verify(userRepository).save(adminUser);
        assertThat(adminUser.getRole()).isEqualTo("ADMIN");
        verify(userRepository, never()).save(regularUser);
    }

    @Test
    void updateUserRoles_doesNotSave_whenRoleAlreadyCorrect() {
        User user = new User("alice", "encoded", "alice@example.com");
        user.setRole("USER");

        when(userRepository.findAll()).thenReturn(List.of(user));

        userService.updateUserRoles();

        verify(userRepository, never()).save(any());
    }
}
