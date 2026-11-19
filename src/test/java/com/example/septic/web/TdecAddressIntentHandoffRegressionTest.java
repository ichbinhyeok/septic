package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void sendsAddressFirstVisitorsToTheNationalPermitSearch() throws Exception {
        String tdec = html("/tdec-septic-records/");
        String permitDirectory = html("/septic-permit-lookup/");
        String addressSearch = html("/septic-permit-search-by-address/");

        assertThat(tdec)
                .containsPattern("href=\"/septic-permit-search-by-address/\"[^>]*data-track-source-context=\"tdec_hero_address_finder\"")
                .contains("Search existing septic permits by U.S. address")
                .doesNotContain("href=\"#tdec-record-search\">Find the correct county or field office");
        assertThat(permitDirectory)
                .containsPattern("href=\"/septic-permit-search-by-address/\"[^>]*data-track-source-context=\"permit_lookup_hero_address_search\"")
                .contains("Search existing septic permits by address");
        assertThat(addressSearch)
                .contains("Looking for an existing septic permit?", "data-address-record-finder-input",
                        "Find the permit route", "href=\"/tdec-septic-records/\"");
    }

    private String html(String path) throws Exception {
        return mockMvc.perform(get(path)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }
}
