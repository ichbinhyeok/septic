package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class SiteLayoutExtractionRegressionTest {

    // Regression: ISSUE-DOC-001 — explicit attached site-layout evidence was reported as missing.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void attachedSiteLayoutIsRecognizedAsFileEvidence() throws Exception {
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable("should not run")
        );
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "official-record.txt",
                "text/plain",
                """
                        SSDS Construction Permit No. 2024-01872
                        Approved capacity: 3 bedrooms
                        Final inspection approved June 14, 2024
                        Site layout attached. No repair permits listed.
                        """.getBytes(StandardCharsets.UTF_8)
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(file, "buying", "TN", "Montgomery County");

        assertThat(result.findings())
                .filteredOn(finding -> finding.key().equals("layout"))
                .extracting(finding -> finding.value())
                .containsExactly("Mentioned");
        assertThat(result.missingItems()).doesNotContain("As-built or site plan");
    }
}
