package com.example.septic;

import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.StateProfile;
import com.example.septic.service.ResearchDataService;
import com.example.septic.web.CountyAccessProfileCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CountyCompletionLoopRegressionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResearchDataService researchDataService;

    @Test
    void everyDemandBackedCountyRouteKeepsTheUserThroughReturnAndDocumentReview() throws Exception {
        List<CountyRecordsPage> demandBackedPages = researchDataService.getPublicCountyRecordsPages().stream()
                .filter(page -> CountyAccessProfileCatalog.find(page.key()) != null)
                .toList();

        assertEquals(
                CountyAccessProfileCatalog.countySpecificProfileCount(),
                demandBackedPages.size(),
                "Every researched county access profile must have a published county page."
        );
        assertTrue(demandBackedPages.size() >= 35);

        for (CountyRecordsPage countyPage : demandBackedPages) {
            StateProfile state = researchDataService.findStateByCode(countyPage.stateCode()).orElseThrow();
            String html = mockMvc.perform(get(countyPage.path(state.slug())))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertTrue(html.contains("data-county-access-workflow"), countyPage.key());
            assertTrue(html.contains("data-county-access-official"), countyPage.key());
            assertTrue(html.contains("data-county-access-return"), countyPage.key());
            assertTrue(html.contains("data-county-access-outcome=\"artifact\""), countyPage.key());
            if (!"TN::hamilton-county".equals(countyPage.key())
                    && !"NC::alamance-county".equals(countyPage.key())) {
                assertTrue(html.contains("data-county-access-outcome=\"request_submitted\""), countyPage.key());
            }
            assertTrue(html.contains("data-county-access-reference"), countyPage.key());
            assertTrue(html.contains("data-county-access-next"), countyPage.key());
        }
    }

    @Test
    void countyDocumentHandoffRestoresTheSameTaskInTheSharedAnalyzer() throws Exception {
        String javascript = mockMvc.perform(get("/app.js"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(javascript.contains("saveDocumentWorkspaceHandoff"));
        assertTrue(javascript.contains("septicpath-official-return-v1"));
        assertTrue(javascript.contains("septicpath-record-task-progress-v1"));
        assertTrue(javascript.contains("workflowRunId"));
        assertTrue(javascript.contains("document_handoff"));
    }
}
