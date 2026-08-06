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
class WakeCountyOperationalFallbackRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-002 — Wake's legacy permit portal timed out twice in the operating browser.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/records-exhaustive-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void wakeCountyUsesAssistedRecordsRouteInsteadOfTheTimedOutPortal() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/wake-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Call Wake County records assistance")))
                .andExpect(content().string(containsString("tel:+19198567400")))
                .andExpect(content().string(containsString("Phone or office intake")))
                .andExpect(content().string(containsString("Open the official iMAPS search guide")))
                .andExpect(content().string(not(containsString(
                        "data-county-primary-url=\"https://permitsearch.wake.gov/\""
                ))));
    }
}
