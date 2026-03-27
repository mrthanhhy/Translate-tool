package com.translate.configuration;

import com.translate.dto.OllamaResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Client gọi API Ollama cho dịch thuật
 */
public class OllamaApiClient {

    private static final Logger logger = LoggerFactory.getLogger(OllamaApiClient.class);
    private final OkHttpClient client;
    private final String host;
    private final String model;
    private final int timeoutSeconds;

    public OllamaApiClient(String host, String model, int timeoutSeconds) {
        this.host = host;
        this.model = model;
        this.timeoutSeconds = timeoutSeconds;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * Gọi API translate cho text
     *
     * @param sourceText Văn bản cần dịch
     * @param sourceLang Ngôn ngữ nguồn
     * @param targetLang Ngôn ngữ đích
     * @return Kết quả dịch
     */
    public OllamaResponse translate(String sourceText, String sourceLang, String targetLang) {
        try {
            String apiUrl = String.format("http://%s/api/generate", host);

            String jsonBody = String.format(
                    "{\"model\": \"%s\", \"prompt\": \"Translate the following %s text to %s:\\n\\n%s\", \"options\": {\"temperature\": 0}}",
                    model, sourceLang, targetLang, sourceText
            );

            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .build();

            logger.info("Sending request to Ollama: {}", apiUrl);

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    logger.error("Ollama API error: {} - {}", response.code(), errorBody);
                    return new OllamaResponse(false, "Translation failed: " + response.code());
                }

                String responseBody = response.body().string();
                logger.info("Ollama response received: {}", responseBody);

                // Parse response (Ollama trả về JSON stream)
                OllamaResponse result = new OllamaResponse(true, responseBody, responseBody);
                logger.info("Translation result: {}", result.getMessage());
                return result;
            }

        } catch (IOException e) {
            logger.error("Error calling Ollama API: {}", e.getMessage());
            return new OllamaResponse(false, "Connection error");
        }
    }

    /**
     * Kiểm tra Ollama có đang chạy không
     */
    public boolean isOllamaAvailable() {
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(host + "/api/tags")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    logger.info("Ollama available. Models: {}", body);
                    return true;
                } else {
                    logger.warn("Ollama not available - HTTP {}", response.code());
                    return false;
                }
            }
        } catch (IOException e) {
            logger.warn("Ollama not available (connection): {}", e.getMessage());
            return false;
        }
    }

    public String getModelList() {
        try {
            Request request = new Request.Builder()
                    .get()
                    .url(host + "/api/tags")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            }
        } catch (IOException e) {
            logger.error("Error fetching model list: {}", e.getMessage());
        }
        return "";
    }
}
