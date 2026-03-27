@echo off
setlocal EnableDelayedExpansion

echo ========================================
echo   Translate Tool - Start Script
echo ========================================
echo.

REM Kiểm tra Java
java -version >nul 2>&1
if errorlevel 1 (
    echo Java chưa được cài đặt hoặc chưa trong PATH!
    pause
    exit /b 1
)

REM Kiểm tra Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Maven chưa được cài đặt hoặc chưa trong PATH!
    pause
    exit /b 1
)

REM Kiểm tra JavaFX
set JAVAFX_PATH=C:\Program Files\JavaFX\bin
if not exist "%JAVAFX_PATH%" (
    echo JavaFX chưa được cài đặt!
    echo Đang tải JavaFX SDK...

    rem Tạo thư mục Downloads nếu chưa có
    if not exist "C:\Users\htngu\Downloads" mkdir "C:\Users\htngu\Downloads"

    rem Download JavaFX SDK
    curl -L "https://download.java.net/java/GA/javadoc/api/17/docs/" -o "C:/Users/htngu/Downloads/temp.html" 2>nul

    echo Xin vui lòng cài đặt JavaFX thủ công:
    echo 1. Truy cập https://gluonhq.com/products/javafx/
    echo 2. Download và chạy installer
    echo 3. Đặt vào: C:\Program Files\JavaFX
    pause
    exit /b 1
)

REM Load .env file nếu tồn tại
if exist ".env" (
    for /f "tokens=*" %%a in ('type .env') do set %%a
)

REM Default values nếu chưa set
set OLLAMA_HOST=%OLLAMA_HOST%:http://localhost:11434
set OLLAMA_MODEL=%OLLAMA_MODEL%:llama3.2
set HOTKEY_ENABLED=%HOTKEY_ENABLED%:true
set HOTKEY_CONFIG=%HOTKEY_CONFIG%:256+160

echo.
echo Configuration:
echo   OLLAMA_HOST=%OLLAMA_HOST%
echo   OLLAMA_MODEL=%OLLAMA_MODEL%
echo   HOTKEY_ENABLED=%HOTKEY_ENABLED%
echo   HOTKEY_CONFIG=%HOTKEY_CONFIG%
echo.

REM Kiểm tra Ollama
echo Checking Ollama...
ollama list %OLLAMA_MODEL% >nul 2>&1
if errorlevel 1 (
    echo Pulling model: %OLLAMA_MODEL%
    ollama pull %OLLAMA_MODEL%
)

REM Khởi động Ollama server (nếu chưa chạy)
echo Starting Ollama server...
ollama serve > ollama-server.log 2>&1 &
timeout /t 5 /nobreak >nul

REM Build ứng dụng nếu chưa có JAR
if not exist "target\translate-tool-1.0.0-SNAPSHOT.jar" (
    echo Building application...
    mvn clean package -DskipTests
    if errorlevel 1 (
        echo Build failed!
        pause
        exit /b 1
    )
)

echo Starting Translate Tool...
echo.

REM Chạy ứng dụng
java -jar "target\translate-tool-1.0.0-SNAPSHOT.jar"

echo.
echo Application stopped.
