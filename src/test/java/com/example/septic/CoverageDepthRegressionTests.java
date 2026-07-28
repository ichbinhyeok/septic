package com.example.septic;

import com.example.septic.web.CountyAccessProfileCatalog;
import com.example.septic.web.CountyAcquisitionProfileCatalog;
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
class CoverageDepthRegressionTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void coverageSeparatesPublishedPagesFromDeepHandoffs() throws Exception {
        int countySpecific = CountyAccessProfileCatalog.countySpecificProfileCount();
        int preparedPacks = CountyAcquisitionProfileCatalog.preparedFieldPackCount();

        assertTrue(countySpecific > 0);
        assertTrue(preparedPacks > 0);

        mockMvc.perform(get("/coverage/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "County-specific handoffs"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Prepared official field packs"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Official starting points only"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        String.valueOf(countySpecific)
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        String.valueOf(preparedPacks)
                )));
    }

    @Test
    void locallyAuthoredCountyRequestCopyIsLabeledAsSuggestedWording() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/south-carolina/greenville-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Suggested request scope · not an official form"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "This wording was drafted by SepticPath"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Copy-ready request")
                )));
    }
}
