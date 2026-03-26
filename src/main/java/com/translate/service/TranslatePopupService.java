package com.translate.service;

import com.translate.dto.OllamaResponse;
import com.translate.configuration.OllamaApiClient;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Dịch vụ xử lý popup dịch thuật
 */
public class TranslatePopupService extends Application {

    private static final String OLLAMA_HOST = "ollama.host";
    private static final String OLLAMA_MODEL = "ollama.model";
    private static final String HOTKEY_CONFIG = "hotkey.config";

    private OllamaApiClient ollamaClient;
    private String hotkeyConfig;
    private TextArea resultArea;

    @Override
    public void start(Stage primaryStage) {
        // Create UI
        createUI(primaryStage);
    }

    private void createUI(Stage primaryStage) {
        primaryStage.setTitle("Translate Popup");
        primaryStage.setResizable(false);
        primaryStage.setWidth(500);
        primaryStage.setHeight(400);

        // VBox cho layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // Text area để nhập văn bản
        TextArea textArea = new TextArea();
        textArea.setPromptText("Nhập văn bản cần dịch...");
        textArea.setWrapText(true);
        textArea.setPrefColumnCount(50);

        // SplitPane: Text area + kết quả dịch
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setSpacing(10);

        // Source language selector
        ComboBox<String> sourceLang = new ComboBox<>();
        sourceLang.getItems().addAll("Vietnamese", "English", "Chinese", "Japanese", "Korean");
        sourceLang.setValue("Vietnamese");

        // Target language selector
        ComboBox<String> targetLang = new ComboBox<>();
        targetLang.getItems().addAll("English", "Vietnamese", "Chinese", "Japanese", "Korean");
        targetLang.setValue("English");

        // Dịch thuật button
        Button translateButton = new Button("Dịch");
        translateButton.setOnAction(e -> handleTranslate(textArea, sourceLang, targetLang));

        inputBox.getChildren().addAll(sourceLang, targetLang, translateButton);

        // Kết quả dịch
        TextArea resultArea = new TextArea();
        resultArea.setPromptText("Kết quả dịch...");
        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        // ScrollPane cho nội dung dài
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);

        // Stack cho text area + kết quả
        HBox contentBox = new HBox(10);
        contentBox.getChildren().addAll(textArea, resultArea);

        // Lưu resultArea để sử dụng
        this.resultArea = resultArea;

        // Cập nhật layout
        root.getChildren().clear();
        root.getChildren().addAll(contentBox);

        // Scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleTranslate(TextArea textArea, ComboBox<String> sourceLang, ComboBox<String> targetLang) {
        String text = textArea.getText();
        String source = sourceLang.getValue();
        String target = targetLang.getValue();

        if (text.isEmpty()) {
            showAlert("Warning", "Vui lòng nhập văn bản!");
            return;
        }

        if (ollamaClient == null) {
            showAlert("Error", "Ollama client không được khởi tạo!");
            return;
        }

        // Hiển thị loading
        showAlert("Translating...", "Đang dịch thuật...");

        // Gọi API Ollama
        OllamaResponse response = ollamaClient.translate(text, source, target);

        if (response.isSuccess()) {
            resultArea.setText(response.getMessage());
        } else {
            showAlert("Error", response.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setOllamaClient(OllamaApiClient client) {
        this.ollamaClient = client;
    }

    public void setHotkeyConfig(String config) {
        this.hotkeyConfig = config;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
