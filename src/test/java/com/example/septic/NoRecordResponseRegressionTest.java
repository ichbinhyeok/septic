package com.example.septic;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.septic.service.DocumentOcrService;
import com.example.septic.service.SepticDocumentAnalysisService;
import com.example.septic.web.SepticDocumentAnalysisResult;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class NoRecordResponseRegressionTest {

    private final SepticDocumentAnalysisService analyzer = new SepticDocumentAnalysisService(
            document -> DocumentOcrService.OcrResult.unavailable("should not run")
    );

    // Regression: ISSUE-DOC-002 — a written no-record response looped back to another records request.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-document-workspace-2026-08-05.md
    @Test
    void officialNoRecordResponseBecomesACompletedNegativeSearchOutcome() throws Exception {
        SepticDocumentAnalysisResult result = analyze("""
                Tennessee Department of Environment and Conservation
                Environmental Field Office response dated July 22, 2026
                We searched the SSDS records using the street address, parcel number,
                current owner, prior owner, subdivision and lot.
                No septic system permit, layout, Certificate of Completion, repair permit,
                or archived paper record was located. This is a written no-record response
                for the property. Please retain this email with the property file.
                """);

        assertThat(result.status()).isEqualTo("official_no_record");
        assertThat(result.recordOutcome()).isNotNull();
        assertThat(result.recordOutcome().type()).isEqualTo("no_record_response");
        assertThat(result.recordOutcome().evidence()).containsIgnoringCase("No septic system permit");
        assertThat(result.decision().level()).isEqualTo("negative_evidence");
        assertThat(result.nextSteps())
                .noneMatch(step -> step.toLowerCase().contains("request the missing"))
                .anyMatch(step -> step.contains("Do not repeat the same request"));
    }

    @Test
    void documentedOfficialSearchWithoutTheExactNoRecordLabelIsStillRecognized() throws Exception {
        SepticDocumentAnalysisResult result = analyze("""
                County Environmental Health office
                Our search included the parcel ID, address, prior owner, and archived records.
                No matching septic file was found for this property.
                """);

        assertThat(result.recordOutcome()).isNotNull();
        assertThat(result.recordOutcome().type()).isEqualTo("no_record_response");
    }

    @Test
    void MissingSupportingItemsInAPermitDoNotBecomeANoRecordResponse() throws Exception {
        SepticDocumentAnalysisResult result = analyze("""
                County septic permit number SSDS-1102
                Approved for 3 bedrooms.
                No final approval or site layout was included in the copy.
                No repair history was listed.
                """);

        assertThat(result.recordOutcome()).isNull();
        assertThat(result.status()).isEqualTo("analyzed");
    }

    private SepticDocumentAnalysisResult analyze(String text) throws Exception {
        return analyzer.analyze(
                new MockMultipartFile(
                        "file",
                        "official-response.txt",
                        "text/plain",
                        text.getBytes(StandardCharsets.UTF_8)
                ),
                "buying",
                "TN",
                "Montgomery County"
        );
    }
}
