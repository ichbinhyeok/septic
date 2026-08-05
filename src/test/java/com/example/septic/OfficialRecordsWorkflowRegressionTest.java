package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class OfficialRecordsWorkflowRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void tennesseeRouteIsAnHonestCountyFirstRecordsDesk() throws Exception {
        mockMvc.perform(get("/tdec-septic-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Find a Tennessee septic permit or system record")))
                .andExpect(content().string(containsString("Which county is the property in?")))
                .andExpect(content().string(containsString("SepticPath does not")))
                .andExpect(content().string(containsString("search TDEC databases, retrieve a PDF, or confirm that a permit exists")))
                .andExpect(content().string(containsString("data-contract-county=\"true\"")))
                .andExpect(content().string(containsString("data-tdec-open-request")))
                .andExpect(content().string(not(containsString("403 Help"))))
                .andExpect(content().string(not(containsString("Enter the address to find the record owner"))));
    }

    @Test
    void stateSpecificOfficialPagesDoNotRenderTennesseeRoutes() throws Exception {
        Map<String, String> expectedHeadings = Map.of(
                "/florida-ostds-permit-lookup/", "Find Florida OSTDS permits and septic records",
                "/dhec-septic-permit-lookup/", "Find South Carolina septic permits and SCDES records"
        );

        for (var route : expectedHeadings.entrySet()) {
            mockMvc.perform(get(route.getKey()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString(route.getValue())))
                    .andExpect(content().string(containsString("Enter the address to find the record owner")))
                    .andExpect(content().string(not(containsString("Tennessee SSDS records and office routing"))))
                    .andExpect(content().string(not(containsString("Open official SSDS page"))))
                    .andExpect(content().string(not(containsString("Use TDEC guide"))));
        }
    }

    @Test
    void texasRouteLeadsWithTheOfficialAuthoritySearchAndVerifiedCountyRecords() throws Exception {
        mockMvc.perform(get("/texas-ossf-records-search/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Find who permitted the Texas septic system")))
                .andExpect(content().string(containsString("Open TCEQ authority search")))
                .andExpect(content().string(containsString("18 county workflows have a source-reviewed record route")))
                .andExpect(content().string(containsString("query OARS or county systems, retrieve a permit")))
                .andExpect(content().string(containsString("data-tx-open-request")))
                .andExpect(content().string(not(containsString("Enter the address to find the record owner"))));
    }

    @Test
    void northCarolinaRouteUsesVerifiedCountyWorkflowsAndAnHonestBoundary() throws Exception {
        mockMvc.perform(get("/north-carolina-septic-permit-lookup/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Find the county that holds the septic permit")))
                .andExpect(content().string(containsString("29 county workflows have a source-reviewed route")))
                .andExpect(content().string(containsString("SepticPath does not")))
                .andExpect(content().string(containsString("query county systems, download a permit, or confirm that a record exists")))
                .andExpect(content().string(containsString("data-nc-open-request")))
                .andExpect(content().string(containsString("Environmental Health staff by county")))
                .andExpect(content().string(not(containsString("Enter the address to find the record owner"))))
                .andExpect(content().string(not(containsString("North Carolina public records"))));
    }

    @Test
    void stateRecordsHubUsesAddressFinderAndCountyPickerInsteadOfALinkWall() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enter the property address")))
                .andExpect(content().string(containsString("data-county-route-picker")))
                .andExpect(content().string(containsString("Choose a county")))
                .andExpect(content().string(containsString("An empty search is not a no-record determination")))
                .andExpect(content().string(not(containsString("Next move board"))))
                .andExpect(content().string(not(containsString("PREPARED BY"))));
    }

    @Test
    void priorityStateRecordHubsShareTheTaskFirstContract() throws Exception {
        for (String state : java.util.List.of("alabama", "indiana", "north-carolina", "south-carolina", "tennessee", "texas")) {
            mockMvc.perform(get("/septic-records-checklist/" + state + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Enter the property address")))
                    .andExpect(content().string(containsString("data-county-route-picker")))
                    .andExpect(content().string(containsString("An empty search is not a no-record determination")))
                    .andExpect(content().string(not(containsString("Records proof ladder"))));
        }
    }

    @Test
    void officialLookupIndexStillLinksEverySpecializedStateRoute() throws Exception {
        mockMvc.perform(get("/official-septic-lookup-tools/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/tdec-septic-records/")))
                .andExpect(content().string(containsString("/north-carolina-septic-permit-lookup/")))
                .andExpect(content().string(containsString("/texas-ossf-records-search/")))
                .andExpect(content().string(containsString("/florida-ostds-permit-lookup/")))
                .andExpect(content().string(containsString("/dhec-septic-permit-lookup/")));
    }

    @Test
    void nationalRecordIntentPagesLeadWithARealAddressWorkflowAndTruthBoundary() throws Exception {
        for (String path : java.util.List.of(
                "/how-to-find-septic-records-online/",
                "/septic-records-by-county/",
                "/septic-permit-search-by-address/",
                "/septic-permit-lookup/",
                "/septic-as-built-records/",
                "/septic-tank-location-records/"
        )) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Enter the address to find the right office")))
                    .andExpect(content().string(containsString("does not retrieve or certify the government record")))
                    .andExpect(content().string(containsString("A failed address search is not proof that no file exists")))
                    .andExpect(content().string(not(containsString("OFFICIAL LOOKUP COMMAND BOARD"))))
                    .andExpect(content().string(not(containsString("PREPARED BY"))));
        }
    }
}
