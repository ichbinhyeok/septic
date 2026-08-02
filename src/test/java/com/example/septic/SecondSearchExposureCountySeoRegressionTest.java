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
class SecondSearchExposureCountySeoRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void promotedCountyPagesExposeDistinctTitlesCanonicalRoutesAndWorkflows() throws Exception {
        Map<String, String> expectedTitles = Map.of(
                "/septic-records-checklist/tennessee/davidson-county/", "Davidson County TN Septic Records and Property File Search",
                "/septic-records-checklist/tennessee/madison-county/", "Madison County TN Septic Drawing and Records Request",
                "/septic-records-checklist/tennessee/shelby-county/", "Shelby County TN Septic Permit, Repair and Inspection",
                "/septic-records-checklist/tennessee/putnam-county/", "Putnam County TN Septic Permit Search and TDEC Records",
                "/septic-records-checklist/indiana/monroe-county/", "Monroe County IN Septic Permit and OpenGov Workflow"
        );

        for (Map.Entry<String, String> entry : expectedTitles.entrySet()) {
            mockMvc.perform(get(entry.getKey()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("<title>" + entry.getValue())))
                    .andExpect(content().string(containsString(
                            "<link rel=\"canonical\" href=\"https://septicpath.com" + entry.getKey() + "\">"
                    )))
                    .andExpect(content().string(containsString("data-county-access-workflow")))
                    .andExpect(content().string(containsString("data-county-profile-scope=\"county_specific\"")));
        }
    }

    @Test
    void renderedPagesExposeTheActualCountyJob() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/tennessee/davidson-county/"))
                .andExpect(content().string(containsString("https://documents.nashville.gov/")))
                .andExpect(content().string(containsString("septicinfo@nashville.gov")));

        mockMvc.perform(get("/septic-records-checklist/tennessee/madison-county/"))
                .andExpect(content().string(containsString("https://madisoncountytn.gov/FormCenter/Health-Department-11/Septic-System-Records-Request-89")))
                .andExpect(content().string(containsString("reCAPTCHA")));

        mockMvc.perform(get("/septic-records-checklist/indiana/monroe-county/"))
                .andExpect(content().string(containsString("Minimum Specs")))
                .andExpect(content().string(containsString("mailto:wastewater@co.monroe.in.us")));
    }
}
