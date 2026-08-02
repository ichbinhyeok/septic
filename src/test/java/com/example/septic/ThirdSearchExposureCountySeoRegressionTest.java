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
class ThirdSearchExposureCountySeoRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void promotedPagesExposeDistinctTitlesCanonicalRoutesAndCountyWorkflows() throws Exception {
        Map<String, String> expectedTitles = Map.of(
                "/septic-records-checklist/tennessee/anderson-county/", "Anderson County TN Septic Permit Search and TDEC Records",
                "/septic-records-checklist/north-carolina/randolph-county/", "Randolph County NC Septic Permits, Repairs and ePermits",
                "/septic-records-checklist/alabama/tuscaloosa-county/", "Tuscaloosa County AL Septic Permits and Records Contact",
                "/septic-records-checklist/alabama/calhoun-county/", "Calhoun County AL Septic Permits and Environmental Office",
                "/septic-records-checklist/south-carolina/charleston-county/", "Charleston County SC Septic Permit and Final Inspection Records",
                "/septic-records-checklist/south-carolina/greenville-county/", "Greenville County SC Septic Permit and Final Inspection Records",
                "/septic-records-checklist/south-carolina/anderson-county/", "Anderson County SC Septic Permit and Final Inspection Records",
                "/septic-records-checklist/south-carolina/spartanburg-county/", "Spartanburg County SC Septic Permit and Final Inspection Records"
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
    void renderedPagesExposeTheSpecificOfficialRouteAndBoundary() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/randolph-county/"))
                .andExpect(content().string(containsString("https://esuite.randolphcountync.gov/eSuite.Permits/WelcomePage.aspx")))
                .andExpect(content().string(containsString("two residential bedrooms")));

        mockMvc.perform(get("/septic-records-checklist/alabama/tuscaloosa-county/"))
                .andExpect(content().string(containsString("tel:205-562-6900")))
                .andExpect(content().string(containsString("Environmental Office")));

        mockMvc.perform(get("/septic-records-checklist/south-carolina/charleston-county/"))
                .andExpect(content().string(containsString("https://prcweb.charlestoncounty.org/")))
                .andExpect(content().string(containsString("Coastal")));

        mockMvc.perform(get("/septic-records-checklist/south-carolina/spartanburg-county/"))
                .andExpect(content().string(containsString("Piedmont I")))
                .andExpect(content().string(containsString("tel:18557312504")));
    }
}
