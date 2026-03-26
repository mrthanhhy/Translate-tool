package com.translate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response từ API Ollama
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OllamaResponse {

    private boolean success;
    private String message;
    private String response;

    @Override
    public String toString() {
        return String.format("OllamaResponse{success=%s, message='%s', response='%s'}",
                success, message, response);
    }
}
