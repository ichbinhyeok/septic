package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class StateRecordsAcquisitionRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-005 — state records pages surfaced a buyer-transfer artifact instead of the state's first permit file.
    // Found by /qa on 2026-08-02
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-02.md
    @Test
    void recordsPagesLeadWithStateSpecificArtifactsAndSearchFields() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/indiana/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Any county permit, site-review, or design record already tied to the property.")))
                .andExpect(content().string(containsString("property address, owner name, parcel number, county, and any permit or application number")));

        mockMvc.perform(get("/septic-records-checklist/north-carolina/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Any prior improvement permit, construction authorization, or operation permit for the site.")))
                .andExpect(content().string(containsString("parcel or PIN, owner name, county, subdivision or lot")));

        mockMvc.perform(get("/septic-records-checklist/south-carolina/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("The permit copy already on file for the parcel.")))
                .andExpect(content().string(containsString("tax map number, lot and block, physical address")))
                .andExpect(content().string(containsString("licensed septic contractor for field location")));
    }
}
