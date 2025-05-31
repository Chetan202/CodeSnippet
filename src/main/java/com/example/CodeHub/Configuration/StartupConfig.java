package com.example.CodeHub.Configuration;

import com.example.CodeHub.Services.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Component that runs configuration tasks when the application starts
 */
@Component
public class StartupConfig {

    private final UserService userService;

    public StartupConfig(UserService userService) {
        this.userService = userService;
    }

    /**
     * Update user roles when the application starts
     * This ensures that only the user with email chetanjha888@gmail.com has admin privileges
     */
    @EventListener(ApplicationReadyEvent.class)
    public void updateUserRolesOnStartup() {
        userService.updateUserRoles();
        System.out.println("User roles updated: Admin privilege set for chetanjha888@gmail.com");
    }
}
