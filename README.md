# Anh-Viet Translator

Java Desktop Translation Application with Global Hotkey

## Features

- Global hotkey `Ctrl + Alt + M` to translate selected text
- Copy-paste translation results automatically
- Support multiple translation modes
- Clean translation output with text cleaning

## Prerequisites

- Java 17+
- Maven 3.6+

## Build & Run

### Initial Setup (One-time only)

Download and set up the JNativeHook DLL:

```bash
# 1. Download the official JAR with native libraries
curl -L -o C:\Users\%USERPROFILE%\.m2\repository\com\github\kwhat\jnativehook\2.2.2\jnativehook-2.2.2.jar https://github.com/kwhat/jnativehook/releases/download/2.2.2/jnativehook-2.2.2.jar

# 2. Extract DLL from the JAR
jar xf C:\Users\%USERPROFILE%\.m2\repository\com\github\kwhat\jnativehook\2.2.2\jnativehook-2.2.2.jar com/github/kwhat/jnativehook/lib/windows/x86_64/JNativeHook.dll

# 3. Copy DLL to project resources
mkdir src\main\resources\com\github\kwhat\jnativehook\lib\windows\x86_64
copy com\github\kwhat\jnativehook\lib\windows\x86_64\JNativeHook.dll src\main\resources\com\github\kwhat\jnativehook\lib\windows\x86_64\

# 4. Clean up extracted files
rmdir /s /q com\github\kwhat\jnativehook
```

### Build Project

```bash
mvn clean package
```

The build process automatically copies the DLL to the target directory.

### Run Application

```bash
java -Djava.library.path=target -jar target/anh-viet-translator-1.0.0-with-native.jar
```

## Usage

1. Select any text in any application
2. Press `Ctrl + Alt + M`
3. The text will be translated to Vietnamese
4. The result will be automatically copied to clipboard

## Project Structure

```
Translate-tool/
├── src/main/java/com/translate/
│   ├── Main.java                  # Application entry point
│   ├── OllamaService.java         # Ollama API service
│   ├── TranslationPopup.java      # Translation popup UI
│   ├── HotkeyListener.java        # Global hotkey listener
│   └── TextCleaner.java           # Text cleaning utilities
├── src/main/resources/
│   └── com/github/kwhat/jnativehook/lib/windows/x86_64/
│       └── JNativeHook.dll        # Native library (included in build)
├── target/
│   ├── JNativeHook.dll            # Native DLL for runtime
│   ├── anh-viet-translator-1.0.0.jar
│   └── anh-viet-translator-1.0.0-with-native.jar
└── pom.xml
```

## License

MIT License
