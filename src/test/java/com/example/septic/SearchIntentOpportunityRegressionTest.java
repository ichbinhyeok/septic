package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SearchIntentOpportunityRegressionTest {
    @Autowired MockMvc mvc;

    @Test void maricopaAnswersSearchIntentBeforeTheServiceProof() throws Exception {
        String html = mvc.perform(get("/septic-records-checklist/arizona/maricopa-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Maricopa County Septic Search | Free Records &amp; Permit Lookup")))
                .andExpect(content().string(containsString("Online-Septic-Search-92")))
                .andExpect(content().string(containsString("data-track-source-context=\"maricopa_search_quick_start\"")))
                .andReturn().getResponse().getContentAsString();
        assertTrue(html.indexOf("data-search-intent-answer") < html.indexOf("data-record-evidence "));
    }

    @Test void unionSeparatesExistingRecordRequestFromNewPermit() throws Exception {
        mvc.perform(get("/septic-records-checklist/north-carolina/union-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("not an application for a new system")))
                .andExpect(content().string(containsString("MyHD")));
        mvc.perform(get("/septic-records-checklist/tennessee/knox-county/"))
                .andExpect(content().string(not(containsString("data-search-intent-answer"))));
    }

    @Test void newHampshireProvidesSearchAndArchiveWithoutInventingAvailability() throws Exception {
        mvc.perform(get("/septic-records-checklist/new-hampshire/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>NHDES Septic Records &amp; OneStop Plans | New Hampshire")))
                .andExpect(content().string(containsString("https://www4.des.state.nh.us/SSBOneStop/")))
                .andExpect(content().string(containsString("formtag=NHDES-W-05-010")))
                .andExpect(content().string(containsString("cannot certify permit existence")))
                .andExpect(content().string(not(containsString("operational-approval and archive-gap friction"))));
    }

    @Test void arizonaHubLinksToTheSpecificSearchIntentRoute() throws Exception {
        mvc.perform(get("/septic-records-checklist/arizona/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Start with the free Maricopa County septic search and records guide")));
    }
}
