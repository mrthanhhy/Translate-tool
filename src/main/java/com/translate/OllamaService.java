package com.translate;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Service gọi API Ollama để dịch thuật
 */
public class OllamaService {
    private static final String OLLAMA_BASE_URL = "https://chery-unnatural-collinearly.ngrok-free.dev";
    private static final String MODEL_NAME = "spTranl";

    /**
     * Dịch văn bản sang tiếng Việt
     * @param text Văn bản cần dịch
     * @return Kết quả dịch thuật
     * @throws Exception Nếu có lỗi khi gọi API
     */
    public String translate(String text) throws Exception {
        String apiUrl = OLLAMA_BASE_URL + "/api/generate";
        URL url = new URL(apiUrl);

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setConnectTimeout(30000); // 30 seconds timeout
            connection.setReadTimeout(30000); // 30 seconds timeout

            // Tạo payload JSON
            String jsonPayload = createJsonPayload(text);

            // Gửi yêu cầu
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Đọc phản hồi
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                StringBuilder errorMsg = new StringBuilder();
                errorMsg.append("HTTP Error: ").append(responseCode);
                if (connection.getErrorStream() != null) {
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorMsg.append(" - ").append(line);
                        }
                    }
                }
                throw new Exception(errorMsg.toString());
            }

            // Đọc kết quả
            StringBuilder response = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                StringBuilder fullResponse = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    fullResponse.append(line);
                    // Xử dụng streaming response
                    if (line.contains("\"response\"")) {
                        int startIndex = line.indexOf("\"response\"");
                        if (startIndex >= 0) {
                            int colonIndex = line.indexOf(":", startIndex);
                            if (colonIndex >= 0) {
                                int quoteStart = line.indexOf("\"", colonIndex + 1);
                                if (quoteStart >= 0) {
                                    int quoteEnd = line.indexOf("\"", quoteStart + 1);
                                    if (quoteEnd > quoteStart) {
                                        String extractedText = line.substring(quoteStart + 1, quoteEnd).trim();
                                        if (!extractedText.isEmpty()) {
                                            response.append(extractedText);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String result = response.toString().trim();
            if (result.isEmpty()) {
                throw new Exception("Không nhận được kết quả từ Ollama");
            }
            return result;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Tạo payload JSON cho API Ollama
     */
    private String createJsonPayload(String text) {
        // Escapes special characters in JSON
        String escapedPrompt = escapeJsonString(text);

        return String.format(
            "{" +
            "  \"model\": \"%s\"," +
            "  \"prompt\": \"%s\"," +
            "  \"stream\": false" +
            "}",
            MODEL_NAME, escapedPrompt
        );
    }

    /**
     * Escape string for JSON
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Kiểm tra kết nối đến Ollama
     */
    public boolean isConnected() {
        try {
            URL url = new URL(OLLAMA_BASE_URL + "/api/tags");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }
}
