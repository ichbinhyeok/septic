package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchExposureCountySeoRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    // Regression: ISSUE-005 —new county workflows still emitted the generic county title and description.
    // Found by /qa on 2026-08-03
    // Report: .gstack/qa-reports/qa-report-septicpath-com-2026-08-03.md
    @Test
    void promotedCountyPagesExposeDistinctSearchSnippetsAndCanonicalRoutes() throws Exception {
        Map<String, String> expectedTitles = Map.of(
                "/septic-records-checklist/tennessee/bradley-county/", "Bradley County TN Septic Permit Lookup and TDEC Records",
                "/septic-records-checklist/tennessee/sullivan-county/", "Sullivan County TN Septic Layout and Records Request",
                "/septic-records-checklist/tennessee/loudon-county/", "Loudon County TN Septic Records Search and Permit Files",
                "/septic-records-checklist/tennessee/maury-county/", "Maury County TN Septic Permit Lookup and TDEC Records",
                "/septic-records-checklist/tennessee/jefferson-county/", "Jefferson County TN Septic Permits, Final Approval and Records"
        );

        for (Map.Entry<String, String> entry : expectedTitles.entrySet()) {
            String canonical = "https://septicpath.com" + entry.getKey();
            mockMvc.perform(get(entry.getKey()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("<title>" + entry.getValue())))
                    .andExpect(content().string(containsString("<link rel=\"canonical\" href=\"" + canonical + "\">")))
                    .andExpect(content().string(containsString("data-county-access-workflow")))
                    .andExpect(content().string(containsString("data-county-profile-scope=\"county_specific\"")));
        }
    }

    @Test
    void snippetsExposeTheSpecificSearchJobAndTruthBoundary() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/tennessee/sullivan-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("original owner, and previous owner")))
                .andExpect(content().string(containsString("mailto:TDEC.Johnsoncity.EFO@tn.gov")));

        mockMvc.perform(get("/septic-records-checklist/tennessee/jefferson-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("conflicting new-permit prices")))
                .andExpect(content().string(containsString("Septic permit and final approval")));
    }
}
