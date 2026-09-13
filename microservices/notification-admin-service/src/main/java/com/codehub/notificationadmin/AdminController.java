package com.codehub.notificationadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminController {

    @GetMapping("/admin/summary")
    public Map<String, String> summary() {
        return Map.of("service", "codehub-notification-admin-service");
    }
}
