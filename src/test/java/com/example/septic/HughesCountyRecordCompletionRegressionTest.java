package com.example.septic;

import com.example.septic.web.CountyAccessProfileCatalog;
import com.example.septic.web.CountyAccessProfileView;
import com.example.septic.web.CountyAcquisitionProfileCatalog;
import com.example.septic.web.CountyAcquisitionProfileView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HughesCountyRecordCompletionRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publishesJurisdictionFirstCountyCompletionRoute() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/south-dakota/hughes-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Hughes County South Dakota Septic Records and Jurisdiction Check"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Confirm whether Hughes County Planning &amp; Zoning"
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"tel:6057737441\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("605-773-7441")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Unincorporated Hughes County")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("written no-record response")));
    }

    @Test
    void keepsCountyPhoneSheetSeparateFromAnOfficialFieldPack() {
        CountyAccessProfileView access = CountyAccessProfileCatalog.find("SD::hughes-county");
        CountyAcquisitionProfileView acquisition = CountyAcquisitionProfileCatalog.find("SD::hughes-county");

        assertNotNull(access);
        assertNotNull(acquisition);
        assertEquals("jurisdiction_first", access.mode());
        assertEquals("official_contact", acquisition.acquisitionMethod());
        assertEquals("605-773-7441", acquisition.phone());
        assertFalse(acquisition.hasPreparedFieldPack());
        assertTrue(acquisition.preparationNote().contains("not a dedicated historical-record form"));
        assertTrue(access.limitation().contains("unincorporated Hughes County"));
    }

    @Test
    void countyFinderExposesTheNewSouthDakotaRoute() throws Exception {
        mockMvc.perform(get("/api/county-finder").param("q", "Hughes County South Dakota"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"countyName\":\"Hughes County\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "\"path\":\"/septic-records-checklist/south-dakota/hughes-county/\""
                )))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "\"confidenceLabel\":\"High-confidence county route\""
                )));
    }
}
