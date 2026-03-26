# Translate Tool - Dịch thuật bằng Ollama

Ứng dụng Spring Boot dịch thuật sử dụng API Ollama với hotkey.

## Công nghệ sử dụng

- **Spring Boot 3.2.0** - Framework ứng dụng
- **Java 17+** - Runtime
- **Maven** - Build tool
- **OkHttp** - HTTP client
- **JNativeHook** - Hotkey listener
- **JavaFX** - Popup interface
- **Lombok** - Code generator

## Cấu hình

### File `.env`

```bash
# Ollama Configuration
OLLAMA_HOST=http://localhost:11434
# OLLAMA_HOST=https://chery-unnatural-collinearly.ngrok-free.dev

OLLAMA_MODEL=llama3.2
# Hoặc: OLLAMA_MODEL=spTranl

# Hotkey
HOTKEY_ENABLED=true
HOTKEY_CONFIG=256+160
HOTKEY_DESCRIPTION=Ctrl+Alt+T
```

### Chạy dự án

**Cách 1: Chạy qua Maven (khuyến nghị)**
```bash
mvn spring-boot:run
```

**Cách 2: Chạy batch file (Windows)**
```bash
start.bat
```

**Cách 3: Chạy JAR trực tiếp**
```bash
java -jar target/translate-tool-1.0.0-SNAPSHOT.jar
```

### Sử dụng hotkey

Nhấn **Ctrl+Alt+T** để mở popup dịch thuật.

## Cài đặt dependencies

Windows:
```bash
# Cài tất cả dependencies
mvn clean package

# Chạy
mvn spring-boot:run
```

## Kiểm tra

```bash
# Kiểm tra Ollama
ollama list
ollama pull llama3.2

# Kiểm tra API
curl http://localhost:11434/api/tags
```

## File cấu hình

| File | Mô tả |
|------|-------|
| `pom.xml` | Maven dependencies |
| `application.yml` | Spring config |
| `.env.example` | Template cấu hình |
| `start.bat` | Script khởi động Windows |

## License

MIT
