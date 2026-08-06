package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FloridaRecordsAcquisitionRouteRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-001 — Florida's primary records CTA opened a generic owner guide PDF.
    // Found by /qa on 2026-08-06.
    // Report: .gstack/qa-reports/records-exhaustive-2026-08-06/qa-report-septicpath-com-2026-08-06.md
    @Test
    void floridaRecordsPageStartsWithTheCountyAuthorityWorkspace() throws Exception {
        String html = mockMvc.perform(get("/septic-records-checklist/florida/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/florida-ostds-permit-lookup/#fl-record-search")))
                .andExpect(content().string(containsString("Open Florida county records routes")))
                .andExpect(content().string(containsString("https://www.floridahealth.gov/all-county-locations.html")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ownerGuide = "https://floridadep.gov/sites/default/files/septic-folder-8x11-link3.pdf";
        assertFalse(html.contains("class=\"button button--primary\" href=\"" + ownerGuide + "\""));
        assertFalse(html.contains("class=\"button button--secondary\" href=\"" + ownerGuide + "\""));
    }
}
