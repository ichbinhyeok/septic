package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.service.TesseractDocumentOcrService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class DocumentOcrTests {
    @Test
    void imageOnlyPdfUsesOcrAndDowngradesEveryFindingToLowConfidence() throws Exception {
        DocumentOcrService ocr = document -> new DocumentOcrService.OcrResult("""
                Permit Number: OCR-2026-1842
                Approved for 4 bedrooms.
                Design flow: 600 GPD.
                Existing conventional system.
                Final approval date: 07/18/2026.
                """, "Typed text was read with OCR.");
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(ocr);

        SepticDocumentAnalysisResult result = analyzer.analyze(
                pdfFile("typed-scan.pdf", imageOnlyPdf()),
                "bedrooms",
                "TN",
                "Validation County"
        );

        assertThat(result.status()).isEqualTo("analyzed");
        assertThat(result.summary()).startsWith("OCR read typed text from this scan.");
        assertThat(result.decision().level()).isEqualTo("incomplete");
        assertThat(result.decision().label()).isEqualTo("Confirm OCR values");
        assertThat(result.decision().answer()).contains("OCR can confuse similar letters and digits");
        assertThat(result.findings()).extracting(finding -> finding.key())
                .contains("permit_number", "approved_bedrooms", "design_flow", "system_type", "final_approval");
        assertThat(result.findings()).allMatch(finding -> finding.confidence().equals("Low"));
    }

    @Test
    void unreadableOcrReturnsHonestBlockedResult() throws Exception {
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable(
                        "OCR could not find readable typed text. Handwriting may require manual review."
                )
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(
                pdfFile("faint-scan.pdf", imageOnlyPdf()),
                "buying",
                "WA",
                ""
        );

        assertThat(result.status()).isEqualTo("no_text");
        assertThat(result.summary()).contains("Handwriting may require manual review");
        assertThat(result.findings()).isEmpty();
        assertThat(result.decision().level()).isEqualTo("blocked");
    }

    @Test
    void searchablePdfDoesNotInvokeOcr() throws Exception {
        AtomicBoolean invoked = new AtomicBoolean();
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(document -> {
            invoked.set(true);
            return DocumentOcrService.OcrResult.unavailable("should not run");
        });

        SepticDocumentAnalysisResult result = analyzer.analyze(
                pdfFile("searchable.pdf", searchablePdf()),
                "bedrooms",
                "TN",
                ""
        );

        assertThat(invoked).isFalse();
        assertThat(result.findings()).extracting(finding -> finding.key())
                .contains("permit_number", "approved_bedrooms");
        assertThat(result.findings()).allMatch(finding -> !finding.confidence().equals("Low"));
    }

    @Test
    void searchablePdfFindingsNameTheSourcePage() throws Exception {
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable("should not run")
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(
                pdfFile("two-page-permit.pdf", searchableTwoPagePdf()),
                "bedrooms",
                "TN",
                ""
        );

        assertThat(result.findings())
                .filteredOn(finding -> finding.key().equals("permit_number")
                        || finding.key().equals("approved_bedrooms"))
                .isNotEmpty()
                .allMatch(finding -> Integer.valueOf(2).equals(finding.pageNumber()));
    }

    @Test
    void repairPermitIssueDateIsRepairHistoryNotFinalApproval() throws Exception {
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable("should not run")
        );
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "repair-record.txt",
                "text/plain",
                """
                        ONSITE WASTEWATER REPAIR RECORD
                        Permit Number: OWTS-R-10442
                        Repair permit issued 05/03/2018 for outlet baffle replacement.
                        """.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(file, "buying", "MD", "Prince George's County");

        assertThat(result.findings()).extracting(finding -> finding.key())
                .contains("repair_history")
                .doesNotContain("approval_date", "final_approval");
    }

    @Test
    void commonPlainTextPermitLabelsAreExtracted() throws Exception {
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable("should not run")
        );
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "county-permit.txt",
                "text/plain",
                """
                        Davidson County Environmental Health
                        Permit: OWTS-26-4408
                        Property: 401 Church St, Nashville, TN 37219
                        Approved bedrooms: 4
                        Final approval: May 10, 2024
                        As-built site plan: attached
                        System type: conventional septic system
                        """.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(file, "buying", "TN", "Davidson County");

        assertThat(result.findings()).extracting(finding -> finding.key())
                .contains("permit_number", "approved_bedrooms", "final_approval", "layout", "system_type");
        assertThat(result.findings()).filteredOn(finding -> finding.key().equals("permit_number"))
                .extracting(finding -> finding.value())
                .containsExactly("OWTS-26-4408");
    }

    @Test
    void missingOcrExecutableFailsClosedWithoutSavingTheDocument() throws Exception {
        TesseractDocumentOcrService ocr = new TesseractDocumentOcrService(
                true,
                "definitely-not-a-real-ocr-command",
                2,
                200,
                3
        );
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(imageOnlyPdf())) {
            DocumentOcrService.OcrResult result = ocr.read(document);
            assertThat(result.text()).isBlank();
            assertThat(result.message()).contains("OCR is not available");
        }
    }

    @Test
    void oversizedScanPageIsRejectedBeforeRenderingOrStartingOcr() throws Exception {
        TesseractDocumentOcrService ocr = new TesseractDocumentOcrService(
                true,
                "definitely-not-a-real-ocr-command",
                2,
                200,
                3
        );
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage(new PDRectangle(20_000, 20_000)));
            DocumentOcrService.OcrResult result = ocr.read(document);
            assertThat(result.text()).isBlank();
            assertThat(result.message()).contains("too large to process safely");
        }
    }

    @Test
    void ocrProcessReceivesPngThroughStandardInputAndReturnsTextWithoutTempFiles() throws Exception {
        String javaExecutable = Path.of(
                System.getProperty("java.home"),
                "bin",
                System.getProperty("os.name").toLowerCase().contains("win") ? "java.exe" : "java"
        ).toString();
        String testClasses = Path.of(
                FakeTesseractProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()
        ).toString();
        TesseractDocumentOcrService ocr = new TesseractDocumentOcrService(
                true,
                List.of(
                        javaExecutable,
                        "-cp",
                        testClasses,
                        FakeTesseractProcess.class.getName()
                ),
                2,
                200,
                6
        );
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(imageOnlyPdf())) {
            DocumentOcrService.OcrResult result = ocr.read(document);
            assertThat(result.text()).contains("PIPE-2026-4408", "Approved for 4 bedrooms", "600 GPD");
            assertThat(result.message()).contains("may contain recognition errors");
        }
    }

    private MockMultipartFile pdfFile(String name, byte[] bytes) {
        return new MockMultipartFile("file", name, "application/pdf", bytes);
    }

    private byte[] imageOnlyPdf() throws Exception {
        BufferedImage image = new BufferedImage(1200, 1600, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.setColor(Color.BLACK);
        graphics.drawString("This text exists only as pixels.", 120, 180);
        graphics.dispose();

        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write(image, "png", png);
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            PDImageXObject pageImage = PDImageXObject.createFromByteArray(document, png.toByteArray(), "scan");
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.drawImage(pageImage, 0, 0, PDRectangle.LETTER.getWidth(), PDRectangle.LETTER.getHeight());
            }
            ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            document.save(pdf);
            return pdf.toByteArray();
        }
    }

    private byte[] searchablePdf() throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(72, 700);
                content.showText("Permit Number: PDF-2026-1842. Approved for 4 bedrooms.");
                content.endText();
            }
            ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            document.save(pdf);
            return pdf.toByteArray();
        }
    }

    private byte[] searchableTwoPagePdf() throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage firstPage = new PDPage(PDRectangle.LETTER);
            document.addPage(firstPage);
            try (PDPageContentStream content = new PDPageContentStream(document, firstPage)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(72, 700);
                content.showText("Property file cover sheet for a 4 acre parcel. Continue to the permit details.");
                content.endText();
            }
            PDPage secondPage = new PDPage(PDRectangle.LETTER);
            document.addPage(secondPage);
            try (PDPageContentStream content = new PDPageContentStream(document, secondPage)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(72, 700);
                content.showText("Permit Number: PDF-2026-1842. Approved for 4 bedrooms.");
                content.endText();
            }
            ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            document.save(pdf);
            return pdf.toByteArray();
        }
    }
}
