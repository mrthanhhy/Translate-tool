package com.translate;

import com.translate.configuration.OllamaApiClient;
import com.translate.dto.OllamaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Translate Tool - Application Main
 * Dịch thuật sử dụng Ollama API
 */
@SpringBootApplication
public class TranslateToolApplication {

    private static final Logger logger = LoggerFactory.getLogger(TranslateToolApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TranslateToolApplication.class, args);
    }
}
