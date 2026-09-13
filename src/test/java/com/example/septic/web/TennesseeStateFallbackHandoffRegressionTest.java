package com.example.septic.web;

import com.example.septic.service.CensusAddressLookupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class TennesseeStateFallbackHandoffRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CensusAddressLookupService censusAddressLookupService;

    // Regression: ISSUE-001 — a Tennessee county without a dedicated page linked back to the page already open.
    // Found by /qa on 2026-09-06 using the first real record-help request's Roane County address.
    // Report: .gstack/qa-reports/qa-report-septicpath-record-help-handoff-2026-09-06.md
    @Test
    void routesAnUnpublishedTennesseeCountyIntoThePreparedTdecWorkspace() throws Exception {
        when(censusAddressLookupService.lookup(anyString())).thenReturn(
                new CensusAddressLookupService.CensusAddressLookupResult(
                        CensusAddressLookupService.CensusAddressLookupResult.Status.MATCHED,
                        "123 EXAMPLE RD, WARTBURG, TN, 37887",
                        "TN",
                        "Morgan"
                )
        );

        mockMvc.perform(post("/api/address-record-finder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\":\"123 Example Rd, Wartburg, TN 37887\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("state_route"))
                .andExpect(jsonPath("$.countyName").value("Morgan"))
                .andExpect(jsonPath("$.routeTitle").value("Continue with Morgan County"))
                .andExpect(jsonPath("$.routePath").value("/tdec-septic-records/?county=morgan"));
    }
}
