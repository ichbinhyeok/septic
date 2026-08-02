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
class FourthSearchExposureCountySeoRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void promotedCountyPagesKeepDistinctTitlesCanonicalRoutesAndWorkflows() throws Exception {
        Map<String, String> expectedTitles = Map.of(
                "/septic-records-checklist/north-carolina/buncombe-county/", "Buncombe County NC Septic Permit Lookup and Accela Records",
                "/septic-records-checklist/north-carolina/wake-county/", "Wake County NC Septic Permit Search and iMAPS Records",
                "/septic-records-checklist/north-carolina/union-county/", "Union County NC Septic Records &amp; Existing System Inspection",
                "/septic-records-checklist/north-carolina/pitt-county/", "Pitt County NC Septic Permit Search and Authorization to Construct",
                "/septic-records-checklist/north-carolina/pender-county/", "Pender County NC Septic Permit Information Request"
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
    void renderedPagesExposeOfficialRoutesAndTheirDecisionBoundaries() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/buncombe-county/"))
                .andExpect(content().string(containsString("https://aca-prod.accela.com/buncombeconc/default.aspx")))
                .andExpect(content().string(containsString("An empty result is not proof")))
                .andExpect(content().string(containsString("id=\"county-acquisition-workspace\"")))
                .andExpect(content().string(containsString("data-county-acquisition-field=\"permitCaseNumber\"")))
                .andExpect(content().string(containsString("exact submission fields are not published or independently verified")));

        mockMvc.perform(get("/septic-records-checklist/north-carolina/wake-county/"))
                .andExpect(content().string(containsString("https://permitsearch.wake.gov/")))
                .andExpect(content().string(containsString("not an official no-record result")))
                .andExpect(content().string(containsString("data-county-acquisition-method=\"official_search\"")))
                .andExpect(content().string(containsString("data-county-address-or-parcel=\"true\"")))
                .andExpect(content().string(containsString("data-county-acquisition-field=\"realEstateId\"")))
                .andExpect(content().string(containsString("Verified route inputs")));

        mockMvc.perform(get("/septic-records-checklist/north-carolina/union-county/"))
                .andExpect(content().string(containsString("https://lfportal.unioncountync.gov/Forms/WellSepticPermitRequest")))
                .andExpect(content().string(containsString("does not clear new construction")))
                .andExpect(content().string(containsString("data-county-acquisition-field=\"projectType\"")))
                .andExpect(content().string(containsString("exact submission fields are not published or independently verified")));

        mockMvc.perform(get("/septic-records-checklist/north-carolina/pitt-county/"))
                .andExpect(content().string(containsString("energovweb.tylerhost.net")))
                .andExpect(content().string(containsString("primary and repair-area acceptance")))
                .andExpect(content().string(containsString("data-county-acquisition-field=\"sitePlanReady\"")))
                .andExpect(content().string(containsString("exact submission fields are not published or independently verified")));

        mockMvc.perform(get("/septic-records-checklist/north-carolina/pender-county/"))
                .andExpect(content().string(containsString("Septic-Permit-Information-Request-PDF")))
                .andExpect(content().string(containsString("does not promise a fee or turnaround")))
                .andExpect(content().string(containsString("data-county-acquisition-method=\"official_pdf\"")))
                .andExpect(content().string(containsString("data-county-acquisition-field=\"requestDate\"")))
                .andExpect(content().string(containsString("Verified route inputs")));
    }
}
