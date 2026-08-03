package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class CommonPermitLabelRegressionTests {
    @Test
    void approvedCapacityBedroomLabelIsExtracted() throws Exception {
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable("should not run")
        );
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "county-permit.txt",
                "text/plain",
                "Approved capacity: 3 bedrooms. Final approval was issued after the county inspection."
                        .getBytes(StandardCharsets.UTF_8)
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(file, "buying", "TN", "Davidson County");

        assertThat(result.findings())
                .filteredOn(finding -> finding.key().equals("approved_bedrooms"))
                .extracting(finding -> finding.value())
                .containsExactly("3");
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
                        """.getBytes(StandardCharsets.UTF_8)
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(file, "buying", "TN", "Davidson County");

        assertThat(result.findings()).extracting(finding -> finding.key())
                .contains("permit_number", "approved_bedrooms", "final_approval", "layout", "system_type");
        assertThat(result.findings()).filteredOn(finding -> finding.key().equals("permit_number"))
                .extracting(finding -> finding.value())
                .containsExactly("OWTS-26-4408");
    }
}
