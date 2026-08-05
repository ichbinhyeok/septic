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
    void tennesseeRouteIsAnHonestAddressToOfficeWorkflow() throws Exception {
        mockMvc.perform(get("/tdec-septic-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Find Tennessee septic records")))
                .andExpect(content().string(containsString("Enter the address to find the record owner")))
                .andExpect(content().string(containsString("does not store records or claim a permit was found")))
                .andExpect(content().string(containsString("Blount, Davidson, Hamilton, Jefferson, Knox, Madison, Sevier, Shelby, and Williamson")))
                .andExpect(content().string(not(containsString("403 Help"))))
                .andExpect(content().string(not(containsString("OFFICIAL LOOKUP COMMAND BOARD"))));
    }

    @Test
    void stateSpecificOfficialPagesDoNotRenderTennesseeRoutes() throws Exception {
        Map<String, String> expectedHeadings = Map.of(
                "/north-carolina-septic-permit-lookup/", "Find a North Carolina septic permit by county",
                "/texas-ossf-records-search/", "Find Texas OSSF permits and septic records",
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
