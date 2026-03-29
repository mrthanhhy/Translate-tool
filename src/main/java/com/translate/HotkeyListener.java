package com.translate;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import javax.swing.SwingUtilities;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * Listener cho Global Hotkey Ctrl + Alt + M
 */
public class HotkeyListener implements NativeKeyListener {
    private OllamaService ollamaService;
    private TranslationPopup popup;
    private Clipboard clipboard;

    public HotkeyListener() {
        try {
            // Set event dispatcher cho thread safety với Swing
            GlobalScreen.setEventDispatcher(new SwingDispatchService());
            // Register native hook
            GlobalScreen.registerNativeHook();
            // Add this listener
            GlobalScreen.addNativeKeyListener(this);
            this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (NativeHookException e) {
            System.err.println("Failed to register native key listener: " + e.getMessage());
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // Không xử lý khi nhấn phím
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Xử lý khi thả phím Ctrl+Alt+M
        int keycode = e.getKeyCode();
        int modifiers = e.getModifiers();

        // Kiểm tra phím M + Ctrl + Alt
        if (keycode == NativeKeyEvent.VC_M &&
                (modifiers & NativeKeyEvent.CTRL_MASK) != 0 &&
                (modifiers & NativeKeyEvent.ALT_MASK) != 0) {
            SwingUtilities.invokeLater(this::processTranslation);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Không xử lý sự kiện typed
    }

    private void processTranslation() {
        try {
            Transferable transferable = clipboard.getContents(null);
            String text = "";

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }

            if (text == null || text.trim().isEmpty()) {
                System.out.println("Clipboard is empty");
                return;
            }

            if (ollamaService == null) {
                ollamaService = new OllamaService();
            }
            if (popup == null) {
                popup = new TranslationPopup();
            }

            final String finalText = text.trim();
            String[] result = new String[1];
            Exception[] exception = new Exception[1];

            SwingUtilities.invokeLater(() -> {
                popup.showLoading();
            });

            // Lấy mode từ dropdown và nối vào text để gửi API
            String mode = popup.getSelectedMode();
            String textToSend = finalText + " " + mode;
            System.out.println("Translating: " + finalText + " [Mode: " + mode + "]");

            // Gọi API Ollama trong background thread
            new Thread(() -> {
                try {
                    String rawResponse = ollamaService.translate(textToSend);

                    // Parse và làm sạch văn bản
                    result[0] = TextCleaner.cleanRawText(rawResponse, mode);

                } catch (Exception ex) {
                    exception[0] = ex;
                }

                SwingUtilities.invokeLater(() -> {
                    if (exception[0] != null) {
                        popup.showError("Lỗi kết nối đến Ollama: " + exception[0].getMessage());
                    } else {
                        popup.showResult(finalText, result[0]);
                    }
                });
            }).start();

        } catch (Exception e) {
            System.err.println("Error processing clipboard: " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                if (popup != null) {
                    popup.showError("Lỗi: " + e.getMessage());
                }
            });
        }
    }

    public void shutdown() {
        try {
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            System.err.println("Error shutting down hotkey: " + e.getMessage());
        }
    }
}
