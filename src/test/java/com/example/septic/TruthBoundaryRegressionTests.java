package com.example.septic;

import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.service.ResearchDataService;
import com.example.septic.web.CountyAccessProfileCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "iMAPS parcel match and Permit Search attachments · verified field pack"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Official iMAPS search guide · starting-point route")
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Go straight to a proven local path.")
                )));
    }

    @Test
    void wakeRouteKeepsOfficeIdentitySeparateFromTheVerifiedSearchWorkflow() {
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
        assertTrue(CountyAccessProfileCatalog.findOrBaseline(wake).countySpecific());
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

    @Test
    void primaryNavigationOpensTheProductAndKeepsGuidesSeparatelyNamed() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "<a href=\"/septic-record-finder/\">Find records</a>"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "<a href=\"/septic-records-checklist/\">Records guides</a>"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "County access dataset"
                )));
    }

    @Test
    void nationalPagesDoNotExposeTemplateOrDoorwayInstructions() throws Exception {
        for (String path : java.util.List.of(
                "/septic-system-cost-calculator/",
                "/perc-test-cost/",
                "/septic-records-by-county/",
                "/tdec-septic-records/"
        )) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.not(
                            org.hamcrest.Matchers.containsString("choose this page")
                    )))
                    .andExpect(content().string(org.hamcrest.Matchers.not(
                            org.hamcrest.Matchers.containsString(" page: Use this when")
                    )))
                    .andExpect(content().string(org.hamcrest.Matchers.not(
                            org.hamcrest.Matchers.containsString("Conversion-ready")
                    )));
        }
    }
}
