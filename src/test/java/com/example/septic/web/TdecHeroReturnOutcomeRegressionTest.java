package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class TdecHeroReturnOutcomeRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-003 — direct TDEC visitors could leave for the official viewer without a return outcome checkpoint.
    // Found by /qa on 2026-08-27.
    // Report: .gstack/qa-reports/qa-report-septicpath-intent-2026-08-27.md
    @Test
    void asksDirectOfficialSearchVisitorsWhatHappenedAndTracksCompletion() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String script = Files.readString(Path.of("src/main/resources/static/tdec-records.js"));

        assertThat(html)
                .contains("data-tdec-hero-return")
                .contains("Did the official search solve it?")
                .contains("data-tdec-hero-outcome=\"found\"")
                .contains("data-tdec-hero-outcome=\"empty\"")
                .contains("data-tdec-hero-outcome=\"blocked\"");
        assertThat(script)
                .contains("septicpath:tdec-hero-return:v1")
                .contains("official_source_returned")
                .contains("official_outcome_recorded")
                .contains("source_context: \"tdec_hero\"")
                .contains("document.addEventListener(\"visibilitychange\"")
                .contains("window.addEventListener(\"pageshow\"");
    }
}
