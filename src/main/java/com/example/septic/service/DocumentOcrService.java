package com.example.septic.service;

import org.apache.pdfbox.pdmodel.PDDocument;

@FunctionalInterface
public interface DocumentOcrService {
    OcrResult read(PDDocument document);

    record OcrResult(String text, String message) {
        public static OcrResult unavailable(String message) {
            return new OcrResult("", message);
        }
    }
}
