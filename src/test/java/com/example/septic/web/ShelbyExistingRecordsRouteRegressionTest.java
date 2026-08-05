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

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class ShelbyExistingRecordsRouteRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-005 - Shelby existing-record users were routed into a new installation or repair permit application.
    // Found by /qa on 2026-08-05.
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-05.md
    @Test
    void separatesExistingFileRequestsFromNewPermitWork() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/tennessee/shelby-county/"))
                .andExpect(content().string(containsString("Ask Shelby County Water Quality for the existing septic property file")))
                .andExpect(content().string(containsString("tel:901-222-9599")))
                .andExpect(content().string(containsString("no public existing-record search was confirmed")))
                .andExpect(content().string(not(containsString("Route a Shelby County septic installation, repair, modification, or abandonment"))));
    }
}
