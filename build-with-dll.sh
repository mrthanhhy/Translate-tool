#!/bin/bash
# Script to build application and copy JNativeHook DLL

set -e

PROJECT_DIR="D:/projects/projects/Translate-tool"
TARGET_DIR="$PROJECT_DIR/target"
M2_REPO="$HOME/.m2/repository/com/github/kwhat/jnativehook/2.2.2/jnativehook-2.2.2.jar"
DLL_NAME="JNativeHook-1.0.0.x86_64.dll"

echo "Building application with Maven..."
cd "$PROJECT_DIR"
mvn clean package

echo ""
echo "Copying JNativeHook DLL from dependency JAR..."

# Create target directory if it doesn't exist
mkdir -p "$TARGET_DIR"

# Check if JAR exists
if [ ! -f "$M2_REPO" ]; then
    echo "Error: JNativeHook JAR not found at $M2_REPO"
    echo "Run 'mvn dependency:resolve' first to download dependencies."
    exit 1
fi

# Extract DLL from JAR using jar command
TEMP_DIR=$(mktemp -d)
echo "Extracting from $M2_REPO to $TEMP_DIR"
jar xf "$M2_REPO" -C "$TEMP_DIR" com/github/kwhat/jnativehook/lib/windows/x86_64/JNativeHook.dll

if [ -f "$TEMP_DIR/com/github/kwhat/jnativehook/lib/windows/x86_64/JNativeHook.dll" ]; then
    cp "$TEMP_DIR/com/github/kwhat/jnativehook/lib/windows/x86_64/JNativeHook.dll" "$TARGET_DIR/$DLL_NAME"
    echo "DLL copied successfully to $TARGET_DIR/$DLL_NAME"
    ls -la "$TARGET_DIR/$DLL_NAME"
else
    echo "Error: DLL not found in JAR"
    echo "Contents of extracted directory:"
    find "$TEMP_DIR" -type f
    rm -rf "$TEMP_DIR"
    exit 1
fi

# Cleanup
rm -rf "$TEMP_DIR"

echo ""
echo "Build complete!"
echo "Files in target directory:"
ls -la "$TARGET_DIR"
echo ""
echo "To run the application:"
echo "  java -Djava.library.path=$TARGET_DIR -jar $TARGET_DIR/anh-viet-translator-1.0.0.jar"
