package com.translate;

import com.translate.configuration.OllamaApiClient;
import com.translate.hotkey.HotkeyManager;
import com.translate.service.TranslateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Array;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Method;

@SpringBootApplication
public class TranslateToolApplication implements CommandLineRunner {

    @Value("${ollama.host:localhost:11434}")
    private String ollamaHost;

    @Value("${ollama.model:llama3.2}")
    private String ollamaModel;

    @Value("${hotkey.enabled:true}")
    private boolean hotkeyEnabled;

    @Value("${hotkey.config:256+160}")
    private String hotkeyConfig;

    @Value("${hotkey.description:Ctrl+Alt+T}")
    private String hotkeyDescription;

    private HotkeyManager hotkeyManager;
    @Autowired
    private TranslateController translateController;

    public static void main(String[] args) {
        SpringApplication.run(TranslateToolApplication.class, args);
    }

    @Override
    public void run(String... args) {
        this.ollamaHost = ollamaHost;
        this.ollamaModel = ollamaModel;

        // Kích hoạt hotkey nếu enabled
        if (hotkeyEnabled) {
            try {
                hotkeyManager = new HotkeyManager(hotkeyConfig, hotkeyDescription,
                        () -> translateController);
                hotkeyManager.activate();
                System.out.println("=== Translate Tool Started ===");
                System.out.println("Hotkey: " + hotkeyConfig.replace("256+", "Ctrl+").replace("+160", "+Alt+T"));
                System.out.println("Ollama Host: " + ollamaHost);
                System.out.println("Model: " + ollamaModel);
                System.out.println("===============================");
            } catch (UnsatisfiedLinkError e) {
                System.out.println("Warning: Could not initialize hotkey (native library issue): " + e.getMessage());
                System.out.println("Translation will still work via the popup when triggered.");
            }
        }

        // Kiểm tra Ollama
        OllamaApiClient ollamaClient = new OllamaApiClient(ollamaHost, ollamaModel, 120);
        if (ollamaClient.isOllamaAvailable()) {
            System.out.println("Ollama is available and ready to translate!");
        } else {
            System.out.println("Warning: Ollama is not available at: " + ollamaHost);
            System.out.println("Please make sure Ollama is running: ollama serve");
        }
    }
}
