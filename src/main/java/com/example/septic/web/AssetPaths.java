package com.example.septic.web;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AssetPaths {
    private static final Map<String, String> VERSIONED_PATHS = new ConcurrentHashMap<>();

    private AssetPaths() {
    }

    public static String versioned(String path) {
        if (path == null || path.isBlank()) {
            return "";
        }
        return VERSIONED_PATHS.computeIfAbsent(path, AssetPaths::computeVersionedPath);
    }

    private static String computeVersionedPath(String path) {
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        try (InputStream stream = AssetPaths.class.getClassLoader().getResourceAsStream("static" + normalizedPath)) {
            if (stream == null) {
                return normalizedPath;
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = stream.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
            return normalizedPath + "?v=" + toHex(digest.digest()).substring(0, 12);
        } catch (IOException | NoSuchAlgorithmException exception) {
            return normalizedPath;
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(Character.forDigit((value >> 4) & 0xF, 16));
            builder.append(Character.forDigit(value & 0xF, 16));
        }
        return builder.toString();
    }
}
