package com.translate;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class - Entry point của ứng dụng
 */
public class Main {

    private static HotkeyListener hotkeyListener;

    public static void main(String[] args) {
        // Chạy trên EDT
        SwingUtilities.invokeLater(Main::launchApp);
    }

    private static void launchApp() {
        // Đặt look and feel cho ứng dụng
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Cannot set look and feel: " + e.getMessage());
        }

        System.out.println("========================================");
        System.out.println("  Anh-Viet Translator v1.0.0");
        System.out.println("========================================");
        System.out.println("Hotkey: Ctrl + Alt + M");
        System.out.println("Press Ctrl + C to exit");
        System.out.println("========================================");

        // Khởi tạo hotkey listener
        try {
            hotkeyListener = new HotkeyListener();
            System.out.println("Global hotkey registered successfully!");
        } catch (Exception e) {
            System.err.println("Failed to register global hotkey: " + e.getMessage());
            System.err.println("Make sure no other application is using Ctrl+Alt+M");
        }

        // Khởi động popup khi cần
        // (popup sẽ được tạo bởi HotkeyListener khi kích hoạt)

        // Runtime shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down...");
            if (hotkeyListener != null) {
                hotkeyListener.shutdown();
                System.out.println("Hotkey unregistered.");
            }
            System.out.println("Application exited.");
        }));
    }
}
