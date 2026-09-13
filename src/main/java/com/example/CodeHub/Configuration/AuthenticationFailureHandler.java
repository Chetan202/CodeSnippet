package com.example.CodeHub.Configuration;

import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserRepository userRepository;
    private final int maxFailedAttempts;
    private final int lockoutMinutes;

    public AuthenticationFailureHandler(
            UserRepository userRepository,
            @Value("${app.security.max-failed-login-attempts:5}") int maxFailedAttempts,
            @Value("${app.security.lockout-minutes:15}") int lockoutMinutes) {
        this.userRepository = userRepository;
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockoutMinutes = lockoutMinutes;
        setDefaultFailureUrl("/login?error");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        User user = email == null ? null : userRepository.findByEmail(email);

        if (user != null) {
            int failedAttempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(failedAttempts);
            if (failedAttempts >= maxFailedAttempts) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(lockoutMinutes));
            }
            userRepository.save(user);
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
