package org.STPP.AgriAutomation.api.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Object> healthCheck() {
        try {
            return ResponseEntity.ok()
                .body(Map.of(
                    "status", "Connected successfully to AgriAutomation backend!",
                    "timestamp", java.time.LocalDateTime.now().toString()
                ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "error", "Health check failed",
                    "message", e.getMessage()
                ));
        }
    }
} 