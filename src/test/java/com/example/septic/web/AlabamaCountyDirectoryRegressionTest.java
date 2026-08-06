package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AlabamaCountyDirectoryRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-005 — the Alabama cost page repeated county routes as a long card/link wall.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/records-exhaustive-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void alabamaCostPageUsesOneSearchableSemanticCountyDirectory() throws Exception {
        mockMvc.perform(get("/septic-system-cost-calculator/alabama/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-alabama-county-directory")))
                .andExpect(content().string(containsString("data-alabama-county-directory-search")))
                .andExpect(content().string(containsString("data-track-source-context=\"alabama_county_directory\"")))
                .andExpect(content().string(not(containsString("data-track-source-context=\"state_guide_county_page_cta\""))))
                .andExpect(content().string(not(containsString("data-track-source-context=\"state_guide_county_page_cta_overflow\""))));
    }
}
