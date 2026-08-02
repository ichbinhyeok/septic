package com.example.septic;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class NarrativePermitExtractionRegressionTest {

    @Test
    void extractsNarrativeSepticPermitNumberAndIssuedFinalApprovalDate() throws Exception {
        SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
                document -> DocumentOcrService.OcrResult.unavailable("should not run")
        );
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "county-record.txt",
                "text/plain",
                """
                        Suffolk County septic permit OWTS-2024-1187.
                        Approved bedrooms: 3.
                        Final approval issued June 14, 2024.
                        Design flow: 450 gallons per day.
                        """.getBytes(StandardCharsets.UTF_8)
        );

        SepticDocumentAnalysisResult result = analyzer.analyze(file, "buying", "NY", "Suffolk County");

        assertThat(result.findings())
                .filteredOn(finding -> finding.key().equals("permit_number"))
                .extracting(finding -> finding.value())
                .containsExactly("OWTS-2024-1187");
        assertThat(result.findings())
                .extracting(finding -> finding.key())
                .contains("approval_date", "final_approval");
    }
}
