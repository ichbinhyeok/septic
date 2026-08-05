package com.example.septic.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CountyFallbackFinderRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-007 — the address form linked to a county-search anchor that was not rendered.
    @Test
    void countyFallbackAnchorTargetsAnActualSearchableFinder() throws Exception {
        mockMvc.perform(get("/septic-records-by-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "href=\"/septic-records-by-county/#septic-records-by-county-county-finder\"")))
                .andExpect(content().string(containsString(
                        "id=\"septic-records-by-county-county-finder\"")))
                .andExpect(content().string(containsString("data-county-finder")))
                .andExpect(content().string(containsString("Find a county route")))
                .andExpect(content().string(containsString("county routes searchable")));
    }

    // Regression: ISSUE-008 — the restored finder displayed zero states and omitted the state filter.
    @Test
    void countyFallbackFinderIncludesThePublishedStateOptions() throws Exception {
        mockMvc.perform(get("/septic-records-by-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-county-finder-state")))
                .andExpect(content().string(containsString("<option value=\"TN\">Tennessee</option>")))
                .andExpect(content().string(containsString("<option value=\"NC\">North Carolina</option>")));
    }
}
