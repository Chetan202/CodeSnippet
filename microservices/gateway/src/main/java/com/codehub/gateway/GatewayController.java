package com.codehub.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GatewayController {

    @Value("${codehub.auth-service-url}")
    private String authServiceUrl;

    @Value("${codehub.snippet-service-url}")
    private String snippetServiceUrl;

    @Value("${codehub.notification-admin-service-url}")
    private String notificationAdminServiceUrl;

    @GetMapping("/")
    public Map<String, Object> routes() {
        return Map.of(
                "service", "codehub-gateway",
                "authServiceUrl", authServiceUrl,
                "snippetServiceUrl", snippetServiceUrl,
                "notificationAdminServiceUrl", notificationAdminServiceUrl
        );
    }
}
