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

@SpringBootTest(properties = {
        "app.storage.root=./build/search-intent-test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class SearchIntentConsolidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void observedTennesseePermitDemandIsIndexableAndHasQueryExactSnippet() throws Exception {
        mockMvc.perform(get("/septic-permit-process/tennessee/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Tennessee Septic Permit Process: TDEC Steps &amp; Records | SepticPath</title>")))
                .andExpect(content().string(containsString("<meta name=\"robots\" content=\"index,follow\">")))
                .andExpect(content().string(not(containsString("<meta name=\"robots\" content=\"noindex,follow\">"))))
                .andExpect(content().string(containsString("data-track-source-context=\"tn_permit_process_intent_missing\"")))
                .andExpect(content().string(containsString("data-track-source-context=\"tn_permit_process_intent_document\"")))
                .andExpect(content().string(containsString("data-track-source-context=\"tn_permit_process_intent_human\"")));

        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<loc>https://example.test/septic-permit-process/tennessee/</loc><lastmod>2026-09-14</lastmod>")));
    }

    @Test
    void demandBackedCountyGetsIntentHandoffsButUntargetedCountyDoesNot() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/tennessee/blount-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-track-source-context=\"county_search_intent_tn_blount-county_original\"")))
                .andExpect(content().string(containsString("Resolve the failed search")))
                .andExpect(content().string(containsString("Check what the document proves")));

        mockMvc.perform(get("/septic-records-checklist/tennessee/sullivan-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("county_search_intent_tn_sullivan-county"))));

        mockMvc.perform(get("/sitemap-county.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<loc>https://example.test/septic-records-checklist/tennessee/blount-county/</loc><lastmod>2026-09-14</lastmod>")));
    }

    @Test
    void demandBackedStateRecordsPageOffersAllFourSituations() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/tennessee/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-track-source-context=\"state_records_intent_tn_original\"")))
                .andExpect(content().string(containsString("data-track-source-context=\"state_records_intent_tn_missing\"")))
                .andExpect(content().string(containsString("data-track-source-context=\"state_records_intent_tn_document\"")))
                .andExpect(content().string(containsString("data-track-source-context=\"state_records_intent_tn_human\"")));
    }
}
