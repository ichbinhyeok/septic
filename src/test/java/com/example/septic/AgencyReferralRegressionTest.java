package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class AgencyReferralRegressionTest {
    private final SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
            document -> DocumentOcrService.OcrResult.unavailable("OCR is disabled for text regression tests.")
    );

    // Regression: ISSUE-DOC-006 - an official file-owner referral looked like an ordinary incomplete document.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void officialReferralBecomesARerouteOutcomeInsteadOfNoRecordOrPending() throws Exception {
        SepticDocumentAnalysisResult result = analyze("""
                Tennessee Department of Environment and Conservation
                Public records response
                Our office does not maintain the septic permit records for this contract county.
                We have forwarded your request to Sevier County Environmental Health.
                """);

        assertThat(result.status()).isEqualTo("agency_referral");
        assertThat(result.recordOutcome()).isNotNull();
        assertThat(result.recordOutcome().type()).isEqualTo("agency_referral");
        assertThat(result.decision().level()).isEqualTo("reroute_required");
        assertThat(result.nextSteps())
                .anyMatch(step -> step.contains("Do not resend"))
                .noneMatch(step -> step.contains("no-record result"));
    }

    @Test
    void instructionsAboutPossibleRoutingAreNotTreatedAsCompletedReferral() throws Exception {
        SepticDocumentAnalysisResult result = analyze("""
                Submit a public records request with the address and parcel ID.
                Staff may forward the request if another office maintains the file.
                """);

        assertThat(result.recordOutcome()).isNull();
    }

    private SepticDocumentAnalysisResult analyze(String text) throws Exception {
        return analyzer.analyze(
                new MockMultipartFile("file", "official-response.txt", "text/plain", text.getBytes(StandardCharsets.UTF_8)),
                "buying",
                "TN",
                "Sevier County"
        );
    }
}
