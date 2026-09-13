package com.codehub.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Registration extraction target. Current production flow lives in the monolith."));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "JWT login extraction target. Current production flow lives in the monolith."));
    }

    @GetMapping("/me")
    public Map<String, String> me() {
        return Map.of("service", "codehub-auth-service");
    }
}
