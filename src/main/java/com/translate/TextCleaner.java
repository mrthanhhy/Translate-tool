package com.translate;

/**
 * Class xử lý và làm sạch văn bản từ Ollama
 */
public class TextCleaner {

    /**
     * Unescape ký tự xuống dòng từ chuẩn JSON
     * Chỉ thực hiện 2 thao tác:
     * 1. Chuyển \n (escaped) thành newline thực
     * 2. Chuyển \" (escaped) thành dấu ngoặc kép thực
     *
     * @param rawText Văn bản raw từ Ollama (escaped JSON)
     * @return Văn bản đã unescape, giữ nguyên 100% nội dung
     */
    public static String cleanRawText(String rawText, String mode) {
        if (rawText == null) return "";
        return rawText.replace("\\n", "\n").replace("\\\"", "\"");
    }
}
