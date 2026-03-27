package com.translate.service;

import com.translate.dto.OllamaResponse;
import com.translate.configuration.OllamaApiClient;
import com.translate.hotkey.HotkeyManager;

/**
 * Dịch vụ xử lý popup dịch thuật
 * Không cần JavaFX - chạy trong console
 */
public class TranslatePopupService {

    private static final String OLLAMA_HOST = "ollama.host";
    private static final String OLLAMA_MODEL = "ollama.model";
    private static final String HOTKEY_CONFIG = "hotkey.config";

    private OllamaApiClient ollamaClient;
    private String hotkeyConfig;
    private String inputText;
    private String translatedText;
    private String sourceLang = "Vietnamese";
    private String targetLang = "English";

    public TranslatePopupService() {
        System.out.println("=== Translate Popup Info ===");
        System.out.println("JavaFX không được hỗ trợ - Chạy trong console");
        System.out.println("Nhập văn bản và nhấn Ctrl+Shift+L để dịch.");
        System.out.println("=============================");
    }

    public void setOllamaClient(OllamaApiClient client) {
        this.ollamaClient = client;
        System.out.println("Ollama: " + (client != null ? "OK" : "NOT FOUND"));
    }

    public void setHotkeyConfig(String config) {
        this.hotkeyConfig = config;
    }

    public void setSourceLanguage(String lang) {
        this.sourceLang = lang;
    }

    public void setTargetLanguage(String lang) {
        this.targetLang = lang;
    }

    public void setInputText(String text) {
        this.inputText = text;
    }

    public void translate() {
        if (inputText == null || inputText.trim().isEmpty()) {
            System.out.println(">>> Error: Vui lòng nhập văn bản!");
            return;
        }

        if (ollamaClient == null) {
            System.out.println(">>> Error: Ollama client không được khởi tạo!");
            return;
        }

        System.out.println(">>> Translating: " + inputText);

        // Gọi API Ollama
        OllamaResponse response = ollamaClient.translate(inputText, sourceLang, targetLang);

        if (response.isSuccess()) {
            translatedText = response.getMessage();
            System.out.println(">>> Translation successful!");
            System.out.println(">>> Result: " + translatedText);
        } else {
            System.out.println(">>> Error: " + response.getMessage());
        }
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void printResult() {
        System.out.println("=== Translation Result ===");
        System.out.println("Source: " + sourceLang);
        System.out.println("Target: " + targetLang);
        System.out.println("Input:  " + inputText);
        System.out.println("Output: " + translatedText);
        System.out.println("==========================");
    }
}