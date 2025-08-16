package org.example.utility;

public enum FileType {
    CSV, XLS, XLSX;

    public static FileType from(String filename, String contentType) {
        String name = filename == null ? "" : filename.toLowerCase();
        String ct = contentType == null ? "" : contentType.toLowerCase();
        if (name.endsWith(".csv") || ct.contains("text/csv")) return CSV;
        if (name.endsWith(".xlsx") || ct.contains("sheet")) return XLSX;
        if (name.endsWith(".xls")) return XLS;
        throw new IllegalArgumentException("Unsupported file type: " + filename + " (" + contentType + ")");
    }
}