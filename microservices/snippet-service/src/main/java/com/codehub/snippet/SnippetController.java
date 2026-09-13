package com.codehub.snippet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/snippets")
public class SnippetController {

    @GetMapping
    public Map<String, String> list() {
        return Map.of("service", "codehub-snippet-service");
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of("message", "Snippet CRUD extraction target. Current production flow lives in the monolith."));
    }
}
