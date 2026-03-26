# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Translate Tool** is a Spring Boot application that provides instant translation using Ollama API with hotkey activation.

## Technology Stack

- **Spring Boot 3.2.0** - Application framework
- **Java 17** - Runtime
- **Maven** - Build tool (mvn)
- **OkHttp** - HTTP client for Ollama API
- **JNativeHook 2.2.3** - Hotkey listener
- **JavaFX 17.0.2** - Popup GUI interface
- **Lombok 1.18.30** - Code generator

## Architecture

```
com.translate/
├── TranslateToolApplication.java    # Spring Boot main class, initializes services
├── configuration/
│   └── OllamaApiClient.java          # OkHttp client to call Ollama translate API
├── hotkey/
│   └── HotkeyManager.java            # JNativeHook-based hotkey handler (Ctrl+Alt+T)
├── service/
│   ├── TranslateController.java     # Spring @Service: calls Ollama API
│   └── TranslatePopupService.java   # JavaFX popup window for translation UI
├── dto/
│   └── OllamaResponse.java          # Lombok data class for API response
└── resources/
    └── application.yml              # Spring configuration
```

## Key Dependencies

- `org.springframework.boot:spring-boot-starter-web`
- `com.github.dasniko:jnativehook:2.2.3`
- `org.openjfx:javafx-controls:17.0.2`
- `com.squareup.okhttp3:okhttp:4.12.0`
- `org.projectlombok:lombok:1.18.30`

## Build Commands

```bash
# Clean build
mvn clean package

# Run application via Maven
mvn spring-boot:run

# Run JAR directly
java -jar target/translate-tool-1.0.0-SNAPSHOT.jar

# Build and run
mvn clean package && mvn spring-boot:run
```

## Common Development Tasks

### Run a single test
```bash
mvn test -Dtest=<TestClassName>
```

### Run with specific profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=<profile>
```

### Check Ollama API
```bash
ollama list
ollama pull llama3.2
curl http://localhost:11434/api/tags
```

## Configuration

### Environment Variables (or .env file)

| Variable | Default | Description |
|----------|---------|-------------|
| `OLLAMA_HOST` | `http://localhost:11434` | Ollama API endpoint |
| `OLLAMA_MODEL` | `llama3.2` | Translation model name |
| `HOTKEY_ENABLED` | `true` | Enable/disable hotkey |
| `HOTKEY_CONFIG` | `256+160` | Hotkey key codes (Ctrl+Alt+T) |
| `HOTKEY_DESCRIPTION` | `Ctrl+Alt+T` | Human-readable hotkey name |

### Hotkey Configuration

Hotkey uses numeric key codes. Format: `<modifier>+<key>`
- `256` = Ctrl key
- `160` = T key
- `256+160` = Ctrl+T
- `256+160` in context = Ctrl+Alt+T (when Alt is also pressed)

To change hotkey, edit `HOTKEY_CONFIG` in `.env` or application.yml.

## Development Workflow

1. **Start Ollama**: `ollama serve` in background
2. **Pull model**: `ollama pull llama3.2` (if not installed)
3. **Configure**: Edit `.env` with your OLLAMA_HOST and model
4. **Run app**: `mvn spring-boot:run`
5. **Use hotkey**: Press configured hotkey to show popup
6. **Translate**: Input text, select source/target languages, click "Dịch"

## Service Interaction

- `TranslateToolApplication` (Spring Boot app)
  - Creates `OllamaApiClient` instance
  - Initializes `HotkeyManager` (if enabled)
  - Checks Ollama availability on startup

- `HotkeyManager` (singleton)
  - Registers global key listener via JNativeHook
  - When hotkey pressed, calls `TranslateController.showPopup()`
  - `deactivate()` unregisters the listener

- `TranslateController` (@Service)
  - Called by hotkey manager to show popup
  - Invokes `OllamaApiClient.translate()`
  - Exposes `/api/translate` endpoint (demo)

- `TranslatePopupService` (JavaFX Application)
  - Launches popup window when hotkey triggered
  - Contains UI: text input, language selectors, translate button
  - Calls `OllamaApiClient.translate()` and displays result

- `OllamaApiClient` (configuration class)
  - Uses OkHttp to call `http://<host>/api/generate`
  - Sends JSON: `{"model": "<model>", "prompt": "Translate...", "options": {...}}`
  - Returns `OllamaResponse` with success status and translated text

## Data Flow

1. User presses hotkey → `HotkeyManager.onHotkeyPressed()`
2. Hotkey manager calls `TranslateController.showPopup()`
3. Controller creates/launches `TranslatePopupService` (JavaFX)
4. User inputs text and clicks "Dịch" in popup
5. Popup calls `ollamaClient.translate(text, source, target)`
6. `OllamaApiClient` sends HTTP POST to Ollama API
7. Ollama returns translated text in JSON response
8. Popup displays result in TextArea

## File Structure

```
src/main/java/com/translate/
├── TranslateToolApplication.java    # Spring Boot main
├── configuration/
│   └── OllamaApiClient.java         # Ollama HTTP client
├── hotkey/
│   └── HotkeyManager.java           # Hotkey handler
├── service/
│   ├── TranslateController.java     # Translation service
│   └── TranslatePopupService.java   # JavaFX popup
└── dto/
    └── OllamaResponse.java          # API response DTO

src/main/resources/
├── application.yml                  # Spring config
└── application.properties           # Additional props
```

## Environment Files

- `.env.example` - Template for environment variables
- `application.yml` - Spring Boot configuration
- `start.bat` - Windows startup script

## Testing

No test classes exist yet. To add tests:

```java
@SpringBootTest
class TranslateControllerTest {
    @Autowired
    private TranslateController controller;

    @Test
    void testTranslate() {
        // Test translation logic
    }
}
```

## Troubleshooting

### JavaFX popup not showing
- Ensure JavaFX runtime is installed (see INSTALLATION.md)
- Check JavaFX plugin in pom.xml is configured correctly

### Hotkey not working
- Verify no other app is using the same hotkey
- Check `HOTKEY_CONFIG` matches your desired combination
- Review logs for "Hotkey registered successfully"

### Ollama connection timeout
- Run `ollama serve` in background
- Verify model is loaded: `ollama list`
- Increase timeout in `OllamaApiClient` constructor

### Connection refused
- Ollama may not be running (`ollama serve`)
- Check firewall isn't blocking port 11434
- If using ngrok, ensure tunnel is active

## External Dependencies

- **Ollama** (https://ollama.ai) - Local LLM server
- **JavaFX** (https://gluonhq.com/products/javafx/) - GUI toolkit
- **JNativeHook** (https://github.com/dasniko/jnativehook) - Native hotkey API
