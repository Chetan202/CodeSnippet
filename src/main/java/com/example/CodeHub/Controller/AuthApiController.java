package com.example.CodeHub.Controller;

import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final int maxFailedAttempts;
    private final int lockoutMinutes;

    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtService jwtService,
                             UserRepository userRepository,
                             @Value("${app.security.max-failed-login-attempts:5}") int maxFailedAttempts,
                             @Value("${app.security.lockout-minutes:15}") int lockoutMinutes) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockoutMinutes = lockoutMinutes;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            User user = userRepository.findByUsername(request.username());
            if (user != null) {
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                userRepository.save(user);
            }
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(Map.of("token", token, "tokenType", "Bearer"));
        } catch (AuthenticationException exception) {
            User user = userRepository.findByUsername(request.username());
            if (user != null) {
                int failedAttempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(failedAttempts);
                if (failedAttempts >= maxFailedAttempts) {
                    user.setLockedUntil(LocalDateTime.now().plusMinutes(lockoutMinutes));
                }
                userRepository.save(user);
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials, unverified account, or locked account"));
        }
    }

    public record LoginRequest(String username, String password) {
    }
}
