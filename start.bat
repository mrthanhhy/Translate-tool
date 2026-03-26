@echo off
echo ========================================
echo   Translate Tool - Start Script
echo ========================================
echo.

REM Kiểm tra Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java không được cài đặt!
    echo Vui lòng cài đặt JDK 17+ từ https://adoptium.net/
    pause
    exit /b 1
)

REM Kiểm tra Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven không được cài đặt!
    echo Vui lòng cài đặt Maven từ https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Kiểm tra Ollama
ollama list >nul 2>&1
if errorlevel 1 (
    echo ERROR: Ollama không được cài đặt!
    echo Vui lòng cài đặt Ollama từ https://ollama.ai/download
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

echo Starting Translate Tool...
echo.

REM Chạy ứng dụng
java -jar "target\translate-tool-1.0.0-SNAPSHOT.jar"

echo.
echo Application stopped.
