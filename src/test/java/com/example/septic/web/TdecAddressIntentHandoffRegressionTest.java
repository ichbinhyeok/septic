package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class TdecAddressIntentHandoffRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-002 — the TDEC county-finder CTA asked users to supply the county it promised to find.
    // Found by /qa on 2026-08-27.
    // Report: .gstack/qa-reports/qa-report-septicpath-intent-2026-08-27.md
    @Test
    void sendsAddressFirstVisitorsToTheAddressCountyResolver() throws Exception {
        String html = mockMvc.perform(get("/tdec-septic-records/"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(html)
                .contains("href=\"/septic-record-finder/\"")
                .contains("Find the county from a property address")
                .contains("data-track-source-context=\"tdec_hero_address_finder\"")
                .doesNotContain("href=\"#tdec-record-search\">Find the correct county or field office");
    }
}
