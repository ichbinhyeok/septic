package com.example.septic.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class RecordHelpDocumentPolicy {
    public static final int MAX_DOCUMENTS = 3;
    public static final long MAX_DOCUMENT_BYTES = 10L * 1024L * 1024L;
    public static final long MAX_REQUEST_DOCUMENT_BYTES = 15L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "txt", "png", "jpg", "jpeg");

    public String validate(List<MultipartFile> uploads, boolean documentsRequired) {
        List<MultipartFile> documents = present(uploads);
        if (documentsRequired && documents.isEmpty()) {
            return "Add at least one permit, drawing, letter, screenshot, or other septic record for review.";
        }
        if (documents.size() > MAX_DOCUMENTS) {
            return "Add no more than three files.";
        }

        long totalBytes = 0;
        for (MultipartFile document : documents) {
            if (document.getSize() > MAX_DOCUMENT_BYTES) {
                return "Each file must be 10 MB or smaller.";
            }
            totalBytes += document.getSize();
            if (totalBytes > MAX_REQUEST_DOCUMENT_BYTES) {
                return "The combined file size must be 15 MB or smaller.";
            }
            String extension = extension(document.getOriginalFilename());
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                return "Use PDF, TXT, PNG, JPG, or JPEG files only.";
            }
            try {
                if (!hasExpectedSignature(document, extension)) {
                    return "One of the selected files does not match its file type.";
                }
            } catch (IOException exception) {
                return "One of the selected files could not be read. Try attaching it again.";
            }
        }
        return "";
    }

    public List<MultipartFile> present(List<MultipartFile> uploads) {
        if (uploads == null) {
            return List.of();
        }
        return uploads.stream().filter(upload -> upload != null && !upload.isEmpty()).toList();
    }

    public String safeFileName(MultipartFile upload, int index) {
        String original = upload == null ? "" : upload.getOriginalFilename();
        String fileName = original == null ? "" : original.replace('\\', '/');
        int slash = fileName.lastIndexOf('/');
        if (slash >= 0) {
            fileName = fileName.substring(slash + 1);
        }
        String extension = extension(fileName);
        String stem = extension.isBlank() ? fileName : fileName.substring(0, Math.max(0, fileName.length() - extension.length() - 1));
        stem = stem.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (stem.isBlank()) {
            stem = "record";
        }
        if (stem.length() > 60) {
            stem = stem.substring(0, 60).replaceAll("-+$", "");
        }
        return "%02d-%s.%s".formatted(index, stem, extension);
    }

    public String displayFileName(MultipartFile upload, int index) {
        String original = upload == null ? "" : upload.getOriginalFilename();
        String fileName = original == null ? "" : original.replace('\\', '/');
        int slash = fileName.lastIndexOf('/');
        if (slash >= 0) {
            fileName = fileName.substring(slash + 1);
        }
        fileName = fileName.replace('\r', ' ').replace('\n', ' ').trim();
        if (fileName.isBlank()) {
            return "record-%d.%s".formatted(index, extension(original));
        }
        return fileName.length() > 120 ? fileName.substring(0, 120) : fileName;
    }

    public String trustedContentType(MultipartFile upload) {
        return switch (extension(upload == null ? "" : upload.getOriginalFilename())) {
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            default -> "application/octet-stream";
        };
    }

    private String extension(String fileName) {
        if (fileName == null) {
            return "";
        }
        String normalized = fileName.toLowerCase(Locale.ROOT).trim();
        int dot = normalized.lastIndexOf('.');
        return dot < 0 || dot == normalized.length() - 1 ? "" : normalized.substring(dot + 1);
    }

    private boolean hasExpectedSignature(MultipartFile upload, String extension) throws IOException {
        byte[] header = new byte[16];
        int read;
        try (InputStream input = upload.getInputStream()) {
            read = input.read(header);
        }
        if (read <= 0) {
            return false;
        }
        return switch (extension) {
            case "pdf" -> read >= 4 && header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F';
            case "png" -> read >= 8
                    && (header[0] & 0xff) == 0x89 && header[1] == 'P' && header[2] == 'N' && header[3] == 'G'
                    && header[4] == '\r' && header[5] == '\n' && (header[6] & 0xff) == 0x1a && header[7] == '\n';
            case "jpg", "jpeg" -> read >= 3
                    && (header[0] & 0xff) == 0xff && (header[1] & 0xff) == 0xd8 && (header[2] & 0xff) == 0xff;
            case "txt" -> isPlainText(header, read);
            default -> false;
        };
    }

    private boolean isPlainText(byte[] header, int length) {
        String sample = new String(header, 0, length, StandardCharsets.UTF_8);
        return sample.indexOf('\0') < 0;
    }
}
