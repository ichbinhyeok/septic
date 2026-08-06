package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SouthCarolinaRequestEvidenceEntryRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-003 — the SC request draft did not expose the existing submission-evidence step.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/records-exhaustive-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void requestDraftLinksDirectlyToDatedSubmissionEvidence() throws Exception {
        mockMvc.perform(get("/dhec-septic-permit-lookup/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-sc-confirm-request")))
                .andExpect(content().string(containsString("Save submission evidence")))
                .andExpect(content().string(containsString("data-state-return-outcome=\"request_submitted\"")));

        String script = new ClassPathResource("static/south-carolina-records.js")
                .getContentAsString(StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(
                script.contains("submitted.click()") && script.contains("submittedOn"),
                "The inline SC confirmation must open and focus the shared evidence form"
        );
    }
}
