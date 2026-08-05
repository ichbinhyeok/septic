package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class RequestAcknowledgmentRegressionTest {

    private final SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
            document -> DocumentOcrService.OcrResult.unavailable("should not run")
    );

    // Regression: ISSUE-DOC-004 — an official request acknowledgment was treated as an unsubmitted request.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void officialAcknowledgmentPreservesThePendingRequestAndReference() throws Exception {
        SepticDocumentAnalysisResult result = analyze("""
                Tennessee Public Records Request Confirmation
                Your request has been received by the Environmental Field Office.
                Reference number: PRR-2026-4831
                Submitted: August 5, 2026
                This acknowledgment does not include responsive septic records.
                Staff will search the address, parcel ID, current and prior owners, and archived files.
                """);

        assertThat(result.status()).isEqualTo("request_pending");
        assertThat(result.recordOutcome()).isNotNull();
        assertThat(result.recordOutcome().type()).isEqualTo("request_submitted");
        assertThat(result.recordOutcome().evidence()).containsIgnoringCase("request has been received");
        assertThat(result.findings())
                .filteredOn(finding -> finding.key().equals("request_reference"))
                .extracting(finding -> finding.value())
                .containsExactly("PRR-2026-4831");
        assertThat(result.nextSteps())
                .noneMatch(step -> step.toLowerCase().contains("request the missing"))
                .anyMatch(step -> step.contains("Do not treat the acknowledgment as a permit"));
    }

    @Test
    void requestInstructionsWithoutReceiptDoNotBecomeAPendingRequest() throws Exception {
        SepticDocumentAnalysisResult result = analyze("""
                County Environmental Health public records instructions
                Submit a records request with the address, parcel ID, current owner, and prior owner.
                Staff may search archived files after the form is complete.
                """);

        assertThat(result.recordOutcome()).isNull();
    }

    private SepticDocumentAnalysisResult analyze(String text) throws Exception {
        return analyzer.analyze(
                new MockMultipartFile(
                        "file",
                        "request-response.txt",
                        "text/plain",
                        text.getBytes(StandardCharsets.UTF_8)
                ),
                "buying",
                "TN",
                "Montgomery County"
        );
    }
}
