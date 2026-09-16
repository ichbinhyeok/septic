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
class RecordBriefExampleRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void completedRecordBriefShowsEvidenceFactsLimitsAndPilotPath() throws Exception {
        mockMvc.perform(get("/septic-record-brief-example/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Official records.")))
                .andExpect(content().string(containsString("Answers you can understand.")))
                .andExpect(content().string(containsString("INPUT FROM INSPECTOR")))
                .andExpect(content().string(containsString("SEPTICPATH DESK WORK")))
                .andExpect(content().string(containsString("DELIVERED BEFORE SITE VISIT")))
                .andExpect(content().string(containsString("You ask once.")))
                .andExpect(content().string(containsString("Official portals")))
                .andExpect(content().string(containsString("Parcel + GIS")))
                .andExpect(content().string(containsString("WHEN THE WEB STOPS")))
                .andExpect(content().string(containsString("follow up, and reroute")))
                .andExpect(content().string(containsString("If no record can be located")))
                .andExpect(content().string(containsString("We turn the source packet into a usable answer.")))
                .andExpect(content().string(containsString("3 COUNTY PAGES → 1 FIELD-READY BRIEF")))
                .andExpect(content().string(containsString("Original files + clear findings + next steps")))
                .andExpect(content().string(containsString("SIGNATURES EXCLUDED")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-approval-letter-excerpt-public.png")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-site-plan-detail-public.png")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-brief-findings-public.png")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-brief-field-use-public.png")))
                .andExpect(content().string(containsString("href=\"/offer-prep-septic-file-check/#record-help\"")));
    }
}
