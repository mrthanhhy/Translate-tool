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
public class OllamaResponse {

    private boolean success;
    private String message;
    private String response;

    public OllamaResponse(boolean success, String message, String response) {
        this.success = success;
        this.message = message;
        this.response = response;
    }

    public OllamaResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.response = null;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return String.format("OllamaResponse{success=%s, message='%s', response='%s'}",
                success, message, response);
    }
}
