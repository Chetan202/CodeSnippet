package com.example.CodeHub.controller;

import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_returnsToken_withValidCredentials() throws Exception {
        User user = new User("testuser", passwordEncoder.encode("password123"), "testuser@example.com");
        user.setRole("USER");
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_returns401_withInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "nobody", "password", "wrong"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void login_returns401_withWrongPassword() throws Exception {
        User user = new User("wrongpass", passwordEncoder.encode("correct"), "wrongpass@example.com");
        user.setRole("USER");
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "wrongpass", "password", "incorrect"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_incrementsFailedAttempts_onWrongPassword() throws Exception {
        User user = new User("lockeduser", passwordEncoder.encode("correct"), "locked@example.com");
        user.setRole("USER");
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "lockeduser", "password", "wrong"))))
                .andExpect(status().isUnauthorized());

        User updated = userRepository.findByUsername("lockeduser");
        org.assertj.core.api.Assertions.assertThat(updated.getFailedLoginAttempts()).isEqualTo(1);
    }

    @Test
    void login_resetsFailedAttempts_onSuccess() throws Exception {
        User user = new User("resetuser", passwordEncoder.encode("password"), "reset@example.com");
        user.setRole("USER");
        user.setFailedLoginAttempts(3);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", "resetuser", "password", "password"))))
                .andExpect(status().isOk());

        User updated = userRepository.findByUsername("resetuser");
        org.assertj.core.api.Assertions.assertThat(updated.getFailedLoginAttempts()).isEqualTo(0);
    }
}
