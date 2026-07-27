package com.example.septic.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TesseractDocumentOcrService implements DocumentOcrService {
    private static final Semaphore OCR_SLOTS = new Semaphore(2);
    private static final long MAX_RENDERED_PIXELS = 8_000_000L;

    private final boolean enabled;
    private final List<String> command;
    private final int maxPages;
    private final int dpi;
    private final Duration pageTimeout;

    @Autowired
    public TesseractDocumentOcrService(
            @Value("${app.document-ocr.enabled:true}") boolean enabled,
            @Value("${app.document-ocr.command:tesseract}") String command,
            @Value("${app.document-ocr.max-pages:8}") int maxPages,
            @Value("${app.document-ocr.dpi:200}") int dpi,
            @Value("${app.document-ocr.page-timeout-seconds:12}") int pageTimeoutSeconds
    ) {
        this(enabled, List.of(command), maxPages, dpi, pageTimeoutSeconds);
    }

    public TesseractDocumentOcrService(
            boolean enabled,
            List<String> command,
            int maxPages,
            int dpi,
            int pageTimeoutSeconds
    ) {
        this.enabled = enabled;
        this.command = List.copyOf(command);
        this.maxPages = Math.max(1, Math.min(maxPages, 12));
        this.dpi = Math.max(150, Math.min(dpi, 250));
        this.pageTimeout = Duration.ofSeconds(Math.max(3, Math.min(pageTimeoutSeconds, 30)));
    }

    @Override
    public OcrResult read(PDDocument document) {
        if (!enabled) {
            return OcrResult.unavailable("OCR is disabled on this server.");
        }
        if (!OCR_SLOTS.tryAcquire()) {
            return OcrResult.unavailable("OCR is temporarily busy. Try this scan again in a moment.");
        }
        try {
            return readPages(document);
        } finally {
            OCR_SLOTS.release();
        }
    }

    private OcrResult readPages(PDDocument document) {
        PDFRenderer renderer = new PDFRenderer(document);
        List<String> pages = new ArrayList<>();
        int pageCount = Math.min(document.getNumberOfPages(), maxPages);
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            try {
                if (renderedPixelCount(document, pageIndex) > MAX_RENDERED_PIXELS) {
                    return OcrResult.unavailable(
                            "A scanned page is too large to process safely. Export it at 250 DPI or lower and try again."
                    );
                }
                BufferedImage image = renderer.renderImageWithDPI(pageIndex, dpi, ImageType.GRAY);
                String text = readImage(image);
                if (!text.isBlank()) {
                    pages.add(text);
                }
            } catch (IOException exception) {
                return OcrResult.unavailable(
                        "OCR is not available on this server. Try a searchable PDF or request a clearer copy."
                );
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return OcrResult.unavailable("OCR was interrupted before the scan could be read.");
            }
        }
        String text = String.join("\n", pages);
        if (text.isBlank()) {
            return OcrResult.unavailable(
                    "OCR could not find readable typed text. Handwriting, stamps, or a faint scan may require manual review."
            );
        }
        String pageNote = document.getNumberOfPages() > maxPages
                ? " OCR checked the first " + maxPages + " pages only."
                : "";
        return new OcrResult(
                text,
                "Typed text was read with OCR and may contain recognition errors." + pageNote
        );
    }

    private String readImage(BufferedImage image) throws IOException, InterruptedException {
        List<String> processCommand = new ArrayList<>(command);
        processCommand.addAll(List.of(
                "stdin",
                "stdout",
                "-l",
                "eng",
                "--dpi",
                Integer.toString(dpi),
                "--psm",
                "11"
        ));
        Process process = new ProcessBuilder(processCommand).start();
        try {
            ByteArrayOutputStream standardOutput = new ByteArrayOutputStream();
            ByteArrayOutputStream standardError = new ByteArrayOutputStream();
            Thread outputReader = Thread.ofVirtual().start(() -> transfer(process.getInputStream(), standardOutput));
            Thread errorReader = Thread.ofVirtual().start(() -> transfer(process.getErrorStream(), standardError));

            try (var processInput = process.getOutputStream()) {
                if (!ImageIO.write(image, "png", processInput)) {
                    throw new IOException("PNG encoder is unavailable.");
                }
            }

            if (!process.waitFor(pageTimeout.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)) {
                throw new IOException("OCR exceeded the per-page time limit.");
            }
            outputReader.join();
            errorReader.join();
            if (process.exitValue() != 0) {
                String error = standardError.toString(StandardCharsets.UTF_8).trim();
                throw new IOException(error.isBlank() ? "OCR process failed." : error);
            }
            return standardOutput.toString(StandardCharsets.UTF_8);
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private long renderedPixelCount(PDDocument document, int pageIndex) {
        var box = document.getPage(pageIndex).getCropBox();
        long width = Math.max(1, Math.round(box.getWidth() * dpi / 72.0));
        long height = Math.max(1, Math.round(box.getHeight() * dpi / 72.0));
        if (width > MAX_RENDERED_PIXELS || height > MAX_RENDERED_PIXELS
                || width > MAX_RENDERED_PIXELS / height) {
            return Long.MAX_VALUE;
        }
        return width * height;
    }

    private static void transfer(java.io.InputStream input, ByteArrayOutputStream output) {
        try (input; output) {
            input.transferTo(output);
        } catch (IOException ignored) {
            // The caller handles the process exit code or timeout.
        }
    }
}
