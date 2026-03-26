# Hướng dẫn cài đặt và chạy Translate Tool

## Phần 1: Cài đặt phần mềm cần thiết trên Windows 11

### 1. Java JDK 17+

**Cài đặt:**
1. Tải JDK 17 hoặc cao hơn từ: https://adoptium.net/
2. Cài đặt theo hướng dẫn (chọn "I Agree" và cài đặt)
3. Kiểm tra: `java -version`

### 2. Maven

**Cài đặt:**
1. Tải Maven từ: https://maven.apache.org/download.cgi
2. Giải nén vào `C:\Program Files\Maven` hoặc bất kỳ thư mục nào
3. Thêm vào biến môi trường PATH:
   ```
   C:\Program Files\Apache\maven\bin
   ```
4. Kiểm tra: `mvn -version`

### 3. Ollama

**Cài đặt:**
1. Tải Ollama từ: https://ollama.ai/download
2. Chạy file setup và làm theo hướng dẫn
3. Mở Terminal/CMD và chạy:
   ```bash
   ollama serve
   ```
4. Pull model dịch thuật:
   ```bash
   ollama pull llama3.2
   ```

### 4. Ngrok (cho tunneling)

**Cài đặt:**
1. Tải ngrok từ: https://ngrok.com/download
2. Đăng ký tài khoản: https://ngrok.com
3. Lấy API token: https://dashboard.ngrok.com/get-started/your账号
4. Chạy:
   ```bash
   ngrok config add-authtoken <your-token>
   ngrok http http://localhost:11434
   ```

### 5. JavaFX Runtime (cho giao diện popup)

**Cài đặt:**
1. Tải JavaFX từ: https://gluonhq.com/products/javafx/
2. Giải nén và chạy `jfx_win_x64_bin.exe`
3. Đặt thư mục JavaFX vào: `C:\Program Files\JavaFX`
4. Thêm vào PATH: `C:\Program Files\JavaFX\bin`

**Hoặc** sử dụng JAR bundle:
```bash
# Tải JavaFX bundle
# Đặt vào: C:\Program Files\JavaFX\lib
# Copy javafx-sdk/bin/jfx_windows_x64_bin.jar vào
```

---

## Phần 2: Cấu hình dự án

### Tạo file `.env`

```bash
# Sao chép file .env.example
copy .env.example .env

# Chỉnh sửa file .env
# OLLAMA_HOST=https://chery-unnatural-collinearly.ngrok-free.dev
# OLLAMA_MODEL=spTranl
```

### Cấu hình hotkey

Hotkey mặc định: `Ctrl+Alt+T`

Để thay đổi:
- Chỉ `Ctrl+A`: `HOTKEY_CONFIG=1+65`
- `Ctrl+Shift+A`: `HOTKEY_CONFIG=288+65`

---

## Phần 3: Chạy dự án

### Cách 1: Chạy qua Maven (khuyến nghị)

```bash
cd "D:\projects\projects\my-workplace\Translate-tool"

# Clean build
mvn clean package

# Chạy
mvn spring-boot:run
```

### Cách 2: Chạy JAR file

```bash
# Build trước
mvn clean package

# Chạy
java -jar "target\translate-tool-1.0.0-SNAPSHOT.jar"
```

### Cách 3: Chạy với biến môi trường

```bash
set OLLAMA_HOST=http://localhost:11434
set OLLAMA_MODEL=llama3.2
set HOTKEY_ENABLED=true
set HOTKEY_CONFIG=256+160

java -jar "target\translate-tool-1.0.0-SNAPSHOT.jar"
```

---

## Phần 4: Sử dụng

1. **Khởi động Ollama**: `ollama serve`
2. **Pull model**: `ollama pull llama3.2`
3. **Khởi động ứng dụng**: `mvn spring-boot:run`
4. **Nhấn hotkey**: `Ctrl+Alt+T` để mở popup dịch

---

## Kiểm tra

```bash
# Kiểm tra Java
java -version

# Kiểm tra Maven
mvn -version

# Kiểm tra Ollama
ollama list
ollama pull llama3.2
curl http://localhost:11434/api/tags

# Kiểm tra ngrok
ngrok http http://localhost:11434
```

---

## Troubleshooting

### Lỗi JavaFX: "module not found"
- Cài đặt JavaFX runtime
- Đảm bảo JavaFX bin trong PATH

### Lỗi Hotkey không hoạt động
- Đảm bảo không có ứng dụng khác dùng hotkey giống nhau
- Thử hotkey khác

### Lỗi Ollama connection timeout
- Kiểm tra Ollama đang chạy: `ollama serve`
- Kiểm tra model đã load: `ollama list`
- Tăng timeout trong `application.yml`

### Lỗi ngrok
- Đăng ký tài khoản: https://ngrok.com
- Lấy API token và add vào ngrok config
- Chạy: `ngrok config add-authtoken <token>`

---

## Tài liệu tham khảo

- Ollama: https://ollama.ai
- JavaFX: https://openjfx.io
- JNativeHook: https://github.com/dasniko/jnativehook
- Maven: https://maven.apache.org
