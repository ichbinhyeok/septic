package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchDemandPriorityRegressionTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homepagePlacesDemandBackedDeepWorkflowsBeforeStartingPointOnlyExamples() throws Exception {
        String html = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "High-demand record workflows"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/septic-records-checklist/virginia/prince-william-county/"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/septic-records-checklist/texas/tarrant-county/"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/septic-records-checklist/tennessee/hamilton-county/"
                )))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(
                html.indexOf("/septic-records-checklist/virginia/prince-william-county/")
                        < html.indexOf("/septic-records-checklist/north-carolina/wake-county/")
        );
    }

    @Test
    void countyFinderDefaultResultsStartWithDeepPreparedRoutes() throws Exception {
        String json = mockMvc.perform(get("/api/county-finder"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int firstStartingPointOnly = json.indexOf("\"countyName\":\"Davidson County\"");
        int firstPreparedRoute = json.indexOf("\"countyName\":\"Alamance County\"");
        assertTrue(firstPreparedRoute >= 0);
        assertTrue(firstStartingPointOnly < 0 || firstPreparedRoute < firstStartingPointOnly);
    }
}
