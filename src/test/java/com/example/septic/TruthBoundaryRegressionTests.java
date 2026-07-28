package com.example.septic;

import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.service.ResearchDataService;
import com.example.septic.web.CountyAccessProfileCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TruthBoundaryRegressionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResearchDataService researchDataService;

    @Test
    void homepageDoesNotDescribeStartingPointRoutesAsProven() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Start with the routes that already do more than explain."
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "starting-point route"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Go straight to a proven local path.")
                )));
    }

    @Test
    void wakeRouteKeepsOfficeIdentitySeparateFromLinkedApplication() {
        CountyRecordsPage wake = researchDataService
                .listPublicCountyRecordsPages("NC")
                .stream()
                .filter(page -> "wake-county".equals(page.countySlug()))
                .findFirst()
                .orElseThrow();

        assertEquals(
                "Wake County Environmental Services · Septic and Waste Water Division",
                wake.officeLabel()
        );
        assertFalse(CountyAccessProfileCatalog.findOrBaseline(wake).countySpecific());
    }

    @Test
    void addressFinderExplainsThatPublishedRoutesHaveDifferentDepth() throws Exception {
        mockMvc.perform(get("/septic-record-finder/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Route depth shown before handoff"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Responsible office or official source"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Exact file owner")
                )));
    }
}
