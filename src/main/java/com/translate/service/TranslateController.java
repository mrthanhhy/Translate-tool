package com.translate.service;

import com.translate.configuration.OllamaApiClient;
import com.translate.dto.OllamaResponse;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Controller xử lý dịch thuật
 */
@Service
public class TranslateController {

    private static final Logger logger = LoggerFactory.getLogger(TranslateController.class);

    private final OllamaApiClient ollamaClient;
    private final String hotkeyConfig;

    public TranslateController(
            @Value("${ollama.host:localhost:11434}") String ollamaHost,
            @Value("${ollama.model:llama3.2}") String ollamaModel,
            @Value("${hotkey.enabled:true}") String hotkeyEnabled,
            @Value("${hotkey.config:256+160}") String hotkeyConfig) {

        this.ollamaClient = new OllamaApiClient(ollamaHost, ollamaModel, 120);
        this.hotkeyConfig = hotkeyConfig;
        logger.info("TranslateController initialized with Ollama: {} ({})", ollamaHost, ollamaModel);

        // Kiểm tra Ollama có sẵn không
        if (!ollamaClient.isOllamaAvailable()) {
            logger.warn("Ollama is not available at: {}", ollamaHost);
        }
    }

    /**
     * Xử lý dịch thuật
     */
    public OllamaResponse translate(String text, String sourceLang, String targetLang) {
        if (ollamaClient == null || !ollamaClient.isOllamaAvailable()) {
            return new OllamaResponse(false, "Ollama API is not available", "Ollama API is not available");
        }

        return ollamaClient.translate(text, sourceLang, targetLang);
    }

    /**
     * Show popup dịch thuật (console-based, không cần JavaFX)
     */
    public void showPopup() {
        TranslatePopupService popupService = new TranslatePopupService();
        popupService.setOllamaClient(ollamaClient);
        popupService.setHotkeyConfig(hotkeyConfig);

        // Nhập văn bản và dịch
        System.out.println("\n=== Translation Popup ===");
        System.out.println("Nhập văn bản cần dịch:");
        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");

        // Đọc văn bản từ console (dễ bị cut text nếu dài)
        popupService.setSourceLanguage("Vietnamese");
        popupService.setTargetLanguage("English");

        logger.info("Popup service started (console mode)");
    }

    /**
     * API endpoint để test dịch thuật
     */
    @org.springframework.web.bind.annotation.PostMapping("/api/translate")
    public ResponseEntity<String> translateApi(@org.springframework.web.bind.annotation.RequestHeader("Content-Type") String contentType) {
        String text = contentType; // Demo: lấy từ body
        return ResponseEntity.ok(text);
    }
}
