package com.translate;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * Popup dialog hiển thị kết quả dịch thuật
 * UI đơn giản, ổn định - giữ nguyên 100% nội dung Plain Text từ Ollama
 *
 * FIX: Khởi tạo JTextArea NGAY TỪ ĐẦU Constructor trước khi gọi initComponents()
 *      để tránh NullPointerException khi Hotkey được kích hoạt.
 */
public class TranslationPopup extends JDialog {
    // Các thành phần UI - khai báo private final để đảm bảo an toàn
    private final JTextArea sourceArea;
    private final JTextArea resultArea;
    private final JComboBox<String> modeSelector;
    private final JLabel statusLabel;
    private final JPanel statusPanel;
    private boolean isClosing = false;

    // Các chế độ dịch - chính xác theo yêu cầu
    private static final String[] MODES = {"-vn", "-en", "-en -sug", "-q"};

    public TranslationPopup() {
        setTitle("Anh - Việt Translator");
        setSize(700, 500);
        setResizable(true);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);

        // FIX: Khởi tạo JTextArea NGAY TỪ ĐẦU - trước khi gọi initComponents()
        // Đây là nguyên nhân của NullPointerException: textArea is null
        sourceArea = new JTextArea();
        resultArea = new JTextArea();
        modeSelector = new JComboBox<>(MODES);
        statusLabel = new JLabel("Sẵn sàng");
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        initComponents();

        // Bắt sự kiện ESC để đóng cửa sổ
        getRootPane().registerKeyboardAction(
            e -> closePopup(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel tiêu đề
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Anh - Việt Translator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Panel chính - dùng GridBagLayout để kiểm soát layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 10px insets cho tất cả thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Row 0: Source text panel ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        mainPanel.add(createTextPanel("Tiếng Anh:", sourceArea), gbc);

        // --- Row 1: Mode selector dropdown (ngăn bên phải, không kéo giãn) ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0; // Không kéo giãn
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel modeLabelPanel = new JPanel(new BorderLayout());
        modeLabelPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Chế độ dịch:"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        mainPanel.add(modeLabelPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.0; // Dropdown không kéo giãn
        gbc.anchor = GridBagConstraints.EAST;
        modeSelector.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        modeSelector.setSelectedIndex(0); // Mặc định -vn
        mainPanel.add(modeSelector, gbc);

        // --- Row 2: Result text panel ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(createTextPanel("Tiếng Việt:", resultArea), gbc);

        // --- Row 3: Copy button ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JButton copyButton = new JButton("Copy");
        copyButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyButton.addActionListener(e -> copyResultToClipboard());
        mainPanel.add(copyButton, gbc);

        add(mainPanel, BorderLayout.CENTER);

        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createTextPanel(String label, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(label),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // JTextArea nằm trong JScrollPane
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void showLoading() {
        sourceArea.setText("");
        resultArea.setText("");
        statusLabel.setText("Đang dịch...");
        statusLabel.setForeground(Color.BLUE);
        resultArea.setEditable(false);
    }

    public void showResult(String source, String translated) {
        sourceArea.setText(source);
        // Giữ nguyên 100% nội dung Plain Text từ Ollama - KHÔNG xử lý gì thêm
        // KHÔNG dùng JEditorPane, KHÔNG parse HTML - chỉ setText()
        resultArea.setText(translated);
        statusLabel.setText("Hoàn thành");
        statusLabel.setForeground(Color.GREEN);
        resultArea.setEditable(false);
    }

    private void copyResultToClipboard() {
        String text = resultArea.getText();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = new StringSelection(text);
        clipboard.setContents(transferable, null);
    }

    public void showError(String message) {
        sourceArea.setText("");
        resultArea.setText("");
        statusLabel.setText("Lỗi: " + message);
        statusLabel.setForeground(Color.RED);
        resultArea.setEditable(false);

        JOptionPane.showMessageDialog(this,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE);
    }

    public String getSelectedMode() {
        return modeSelector.getSelectedItem().toString();
    }

    private void closePopup() {
        if (!isClosing) {
            isClosing = true;
            setVisible(false);
            isClosing = false;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        isClosing = false;
    }
}
