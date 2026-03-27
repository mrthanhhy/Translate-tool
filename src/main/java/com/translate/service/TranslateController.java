package com.translate.service;

import com.translate.configuration.OllamaApiClient;
import com.translate.dto.OllamaResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý dịch thuật
 */
@Service
@org.springframework.web.bind.annotation.ControllerAdvice
public class TranslateController {

    private final OllamaApiClient ollamaClient;

    public TranslateController(OllamaApiClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    /**
     * Xử lý dịch thuật qua API
     */
    @PostMapping("/api/translate")
    public ResponseEntity<String> translate(@RequestBody String text) {
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Please provide text to translate");
        }

        OllamaResponse response = ollamaClient.translate(text, "English", "Vietnamese");

        if (response.isSuccess()) {
            return ResponseEntity.ok(response.getMessage());
        }
        return ResponseEntity.badRequest().body(response.getMessage());
    }

    /**
     * Check health status
     */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new java.util.HashMap<>();
        status.put("status", "ok");
        status.put("ollama", ollamaClient.isOllamaAvailable() ? "available" : "unavailable");
        return ResponseEntity.ok(status);
    }
}
