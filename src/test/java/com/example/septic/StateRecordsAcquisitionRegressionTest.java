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
        for (String state : java.util.List.of("indiana", "north-carolina", "south-carolina")) {
            mockMvc.perform(get("/septic-records-checklist/" + state + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Enter the property address")))
                    .andExpect(content().string(containsString("data-county-route-picker")))
                    .andExpect(content().string(containsString("An empty search is not a no-record determination")));
        }
    }
}
