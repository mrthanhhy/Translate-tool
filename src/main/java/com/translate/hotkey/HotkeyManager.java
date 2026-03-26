package com.translate.hotkey;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quản lý hotkey để kích hoạt dịch thuật
 */
public class HotkeyManager {

    private static final Logger logger = LoggerFactory.getLogger(HotkeyManager.class);

    private static NativeKeyListener listener;
    private final String hotkeyConfig;
    private final String hotkeyDescription;
    private boolean enabled = true;

    public HotkeyManager(String hotkeyConfig, String hotkeyDescription) {
        this.hotkeyConfig = hotkeyConfig;
        this.hotkeyDescription = hotkeyDescription;

        listener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                logger.info("Hotkey pressed: {}", e);
                onHotkeyPressed(e);
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent e) {
                // Không làm gì khi release
            }

            @Override
            public void nativeKeyTyped(NativeKeyEvent e) {
                // Không làm gì khi type
            }

            private void onHotkeyPressed(NativeKeyEvent event) {
                // Kiểm tra hotkey đã cấu hình
                if (!isHotkeyMatch(event)) {
                    return;
                }

                // Kích hoạt popup dịch thuật
                try {
                    logger.info("Hotkey triggered - showing popup");
                } catch (Exception ex) {
                    logger.error("Error handling hotkey: {}", ex.getMessage());
                }
            }

            private boolean isHotkeyMatch(NativeKeyEvent event) {
                if (!enabled) {
                    return false;
                }

                // Parse hotkey config (ví dụ: "256+160" cho Ctrl+Alt+T)
                String[] parts = hotkeyConfig.split("\\+");
                if (parts.length != 2) {
                    return false;
                }

                try {
                    int expectedModifier = Integer.parseInt(parts[0]);
                    int expectedChar = Integer.parseInt(parts[1]);

                    // Kiểm tra modifier codes
                    if (event.getModifiers() != expectedModifier) {
                        return false;
                    }

                    // Kiểm tra keyCode
                    int keyCode = event.getKeyCode();
                    return keyCode == expectedChar;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
        };

        // Đăng ký listener
        GlobalScreen.addNativeKeyListener(listener);
    }

    /**
     * Kích hoạt hotkey
     */
    public void activate() {
        logger.info("Activating hotkey: {}", hotkeyDescription);

        if (GlobalScreen.isNativeHookRegistered()) {
            logger.info("Hotkey already registered");
            return;
        }

        try {
            GlobalScreen.registerNativeHook();
            logger.info("Hotkey registered successfully: {}", hotkeyDescription);
        } catch (NativeHookException e) {
            logger.error("Failed to register hotkey: {}", e.getMessage());
        }
    }

    /**
     * Vô hiệu hóa hotkey
     */
    public void deactivate() {
        logger.info("Deactivating hotkey");
        enabled = false;
        try {
            GlobalScreen.removeNativeKeyListener(listener);
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            logger.error("Failed to unregister hotkey: {}", e.getMessage());
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            deactivate();
        }
    }

    /**
     * Kiểm tra xem hotkey có được kích hoạt không
     */
    public boolean isEnabled() {
        return enabled;
    }
}
