package com.translate.service;

import com.translate.configuration.OllamaApiClient;
import com.translate.dto.OllamaResponse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;

/**
 * Dịch vụ xử lý popup dịch thuật sử dụng JavaFX
 * Giao diện đồ họa hiện đại với UI responsive
 */
public class TranslatePopupService {

    private OllamaApiClient ollamaClient;
    private String hotkeyConfig;
    private String sourceLang = "English";
    private String targetLang = "Vietnamese";
    private String translatedText = "";

    private TextArea textArea;
    private Button translateBtn;
    private TextArea resultArea;
    private Stage stage;

    public TranslatePopupService() {
        System.out.println("=== Translate Popup Service Initialized ===");
        System.out.println("Using JavaFX UI (requires JavaFX runtime)");
    }

    public void setOllamaClient(OllamaApiClient client) {
        this.ollamaClient = client;
        System.out.println("Ollama client: " + (client != null ? "OK" : "NOT FOUND"));
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

    /**
     * Launch JavaFX popup
     * @param primaryStage the primary stage to show the popup
     */
    public void launch(Stage primaryStage) {
        createPopup(primaryStage);
    }

    /**
     * Launch JavaFX popup (no primary stage, creates new one)
     */
    public void launch() {
        java.lang.reflect.Method startup = null;
        try {
            Class<?> javaFX = Class.forName("javafx.application.Application");
            startup = javaFX.getMethod("startup", Class.forName("javafx.stage.Stage"));
        } catch (Exception e) {
            System.err.println("Cannot launch JavaFX: " + e.getMessage());
            return;
        }

        Stage stage = new Stage();
        createPopup(stage);

        try {
            Object stageArgs = new Object[0];
            startup.invoke(null, stage);
        } catch (Exception e) {
            System.err.println("Error launching JavaFX: " + e.getMessage());
        }
    }

    private void createPopup(Stage primaryStage) {
        // Tạo giao diện
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label titleLabel = new Label("Translate Tool");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");
        root.getChildren().add(titleLabel);

        // Language selector
        HBox languageBox = new HBox(10);
        languageBox.setPadding(new Insets(0, 0, 10, 0));

        Label sourceLabel = new Label("From:");
        sourceLabel.setStyle("-fx-font-size: 14px;");
        languageBox.getChildren().add(sourceLabel);

        ComboBox<String> sourceCombo = createLanguageComboBox("Source", sourceLang);
        languageBox.getChildren().add(sourceCombo);

        Label targetLabel = new Label("To:");
        targetLabel.setStyle("-fx-font-size: 14px;");
        languageBox.getChildren().add(targetLabel);

        ComboBox<String> targetCombo = createLanguageComboBox("Target", targetLang);
        languageBox.getChildren().add(targetCombo);

        root.getChildren().add(languageBox);

        // Text area input
        textArea = new TextArea();
        textArea.setPrefWidth(350);
        textArea.setPrefHeight(100);
        textArea.setEditable(true);
        textArea.setStyle("-fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-text-fill: #333;");
        textArea.setPromptText("Enter text to translate...");
        root.getChildren().add(new Label("Input Text:"));
        root.getChildren().add(textArea);

        // Translate button
        translateBtn = new Button("Dịch (Translate)");
        translateBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 5;");
        translateBtn.setOnAction(e -> doTranslate());
        root.getChildren().add(new Label(""));
        root.getChildren().add(translateBtn);

        // Result area
        resultArea = new TextArea();
        resultArea.setPrefWidth(350);
        resultArea.setPrefHeight(100);
        resultArea.setEditable(false);
        resultArea.setStyle("-fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-text-fill: #333;");
        resultArea.appendText("Translation result will appear here...\n");
        root.getChildren().add(new Label("Translation:"));
        root.getChildren().add(resultArea);

        // Close button
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #ff5722; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 5;");
        closeButton.setOnAction(e -> Platform.exit());
        root.getChildren().add(new Label(""));
        root.getChildren().add(closeButton);

        // Tạo Scene
        Scene scene = new Scene(root, 400, 350);
        scene.setFill(null);
        scene.getStylesheets().add("javafx://skin/styles/classic.css");

        // Tạo Stage
        stage = primaryStage;
        stage.setTitle("Translate Tool - " + targetLang);
        stage.setScene(scene);
        stage.setMinWidth(400);
        stage.setMinHeight(350);

        // Show stage
        stage.show();
    }

    private ComboBox<String> createLanguageComboBox(String label, String selectedValue) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setPrefWidth(120);
        ObservableList<String> languages = FXCollections.observableArrayList(
                "Vietnamese", "English", "Chinese", "French", "German", "Japanese", "Korean");
        combo.setItems(languages);
        combo.setValue(selectedValue);
        combo.setPromptText(label + " Language:");
        combo.setStyle("-fx-background-color: #fff; -fx-border-color: #ddd; -fx-border-radius: 5;");

        return combo;
    }

    private void doTranslate() {
        String text = getInputText();
        if (text == null || text.trim().isEmpty()) {
            showAlert("No Text", "Please enter text to translate!");
            return;
        }

        translateBtn.setDisable(true);
        textArea.setText(""); // Clear input after translation

        // Show loading
        Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION);
        loadingAlert.setTitle("Translating...");
        loadingAlert.setHeaderText("Please wait...");
        loadingAlert.showAndWait();

        // Translate
        OllamaResponse response = ollamaClient.translate(text, sourceLang, targetLang);

        if (response.isSuccess()) {
            translatedText = response.getMessage();
            resultArea.setText(translatedText + "\n");

            showAlert("Translation Complete", "Your text has been successfully translated!");
        } else {
            showAlert("Translation Failed", response.getMessage());
        }

        translateBtn.setDisable(false);
    }

    private String getInputText() {
        if (textArea != null && textArea.getText() != null) {
            return textArea.getText();
        }
        return "";
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Stage getStage() {
        return stage;
    }
}
