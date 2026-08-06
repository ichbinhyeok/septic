package com.example.septic;

import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class OfficialNoRecordWordingRegressionTest {

    private final SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(null);

    // Regression: ISSUE-007 — a common "no septic permit record was found" office reply
    // was treated as an unrelated document during final browser QA on 2026-08-06.
    @Test
    void commonOfficialWrittenResponseIsNegativeSearchEvidence() throws Exception {
        String text = """
                Official written response dated August 6, 2026.
                The county searched the property address and parcel identifier.
                No septic permit record was found in the county archive.
                """;

        SepticDocumentAnalysisResult result = analyzer.analyze(
                new MockMultipartFile(
                        "file",
                        "official-response.txt",
                        "text/plain",
                        text.getBytes(StandardCharsets.UTF_8)
                ),
                "buying",
                "NC",
                "Wake County"
        );

        assertThat(result.status()).isEqualTo("official_no_record");
        assertThat(result.recordOutcome()).isNotNull();
        assertThat(result.recordOutcome().type()).isEqualTo("no_record_response");
        assertThat(result.decision().level()).isEqualTo("negative_evidence");
    }
}
