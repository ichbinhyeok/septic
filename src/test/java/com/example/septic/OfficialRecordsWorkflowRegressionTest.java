package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

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
                .andExpect(content().string(containsString("Search TDEC septic permits and Tennessee SSDS records")))
                .andExpect(content().string(containsString("Choose the property county")))
                .andExpect(content().string(containsString("Open official TDEC SSDS record search")))
                .andExpect(content().string(containsString("SepticPath does not retrieve or certify government records")))
                .andExpect(content().string(containsString("data-contract-county=\"true\"")))
                .andExpect(content().string(containsString("data-tdec-outcome=\"blocked\"")))
                .andExpect(content().string(not(containsString("403 Help"))))
                .andExpect(content().string(not(containsString("Enter the address to find the record owner"))));
    }

    @Test
    void southCarolinaRouteUsesThePublicScdesSearchAndHonestFallbacks() throws Exception {
        mockMvc.perform(get("/dhec-septic-permit-lookup/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Search SCDES for the property’s septic file")))
                .andExpect(content().string(containsString("Open SCDES Site Explorer")))
                .andExpect(content().string(containsString("data-sc-age")))
                .andExpect(content().string(containsString("Older than about 20 years")))
                .andExpect(content().string(containsString("46")))
                .andExpect(content().string(containsString("OSWWCentral@des.sc.gov")))
                .andExpect(content().string(containsString("query SCDES, retrieve a PDF, confirm a permit")))
                .andExpect(content().string(not(containsString("Enter the address to find the record owner"))))
                .andExpect(content().string(not(containsString("403 Help"))));
    }

    @Test
    void floridaRouteSeparatesCurrentAuthorityFromHistoricalRecords() throws Exception {
        mockMvc.perform(get("/florida-ostds-permit-lookup/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Find the office that holds the Florida septic file")))
                .andExpect(content().string(containsString("17 counties use DEP; 50 still use county health departments")))
                .andExpect(content().string(containsString("data-route-owner=\"dep\"")))
                .andExpect(content().string(containsString("data-route-owner=\"county-doh\"")))
                .andExpect(content().string(containsString("Marion County")))
                .andExpect(content().string(containsString("Orange County")))
                .andExpect(content().string(containsString("cabinet HCHD")))
                .andExpect(content().string(containsString("cabinet PASCODOH")))
                .andExpect(content().string(containsString("Open county eBridge records")))
                .andExpect(content().string(containsString("data-fl-open-request")))
                .andExpect(content().string(not(containsString("Enter the address to find the record owner"))));
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
                .andExpect(content().string(containsString("<title>NC Septic Permit Lookup by County &amp; Address | SepticPath</title>")))
                .andExpect(content().string(containsString("Find an NC septic permit by county, address, or parcel. Open Environmental Health routes for as-builts, final approvals, repairs, and no-record replies.")))
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
    void northCarolinaWorkspaceLinksEveryCurrentHighDemandCountyRoute() throws Exception {
        String html = mockMvc.perform(get("/north-carolina-septic-permit-lookup/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Popular county permit searches")))
                .andExpect(content().string(containsString("Buncombe County septic permit lookup")))
                .andExpect(content().string(containsString("Union County septic permit lookup")))
                .andExpect(content().string(containsString("Pender County septic permit lookup")))
                .andExpect(content().string(containsString("Johnston County septic permit lookup")))
                .andExpect(content().string(containsString("Wake County septic permit lookup")))
                .andReturn().getResponse().getContentAsString();

        for (String countySlug : java.util.List.of(
                "buncombe-county", "union-county", "pender-county", "johnston-county",
                "wake-county", "lincoln-county", "onslow-county", "franklin-county",
                "durham-county", "iredell-county", "mecklenburg-county", "davidson-county",
                "forsyth-county", "guilford-county", "alamance-county", "henderson-county"
        )) {
            org.assertj.core.api.Assertions.assertThat(html)
                    .contains("/septic-records-checklist/north-carolina/" + countySlug + "/");
        }
    }

    @Test
    void acquisitionPagesUseQueryAlignedTitlesAndDescriptions() throws Exception {
        mockMvc.perform(get("/tdec-septic-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>TDEC Septic Records Search | Tennessee SSDS Permit Lookup</title>")))
                .andExpect(content().string(containsString("Open Tennessee&#39;s official TDEC SSDS record search")));
        mockMvc.perform(get("/septic-system-cost-calculator/alabama/"))
                .andExpect(content().string(containsString("<title>Alabama Perc Test Cost ($300-$2,700) &amp; County Fees | SepticPath</title>")));
        mockMvc.perform(get("/septic-system-cost-calculator/georgia/"))
                .andExpect(content().string(containsString("<title>Georgia Septic Permit Cost &amp; County Records | SepticPath</title>")));
        mockMvc.perform(get("/septic-system-cost-calculator/alaska/"))
                .andExpect(content().string(containsString("<title>Alaska Septic System Cost &amp; Permit Records | SepticPath</title>")));
        mockMvc.perform(get("/perc-test-cost/arkansas/"))
                .andExpect(content().string(containsString("<title>Arkansas Perc Test Cost &amp; County Permit Steps | SepticPath</title>")));
        mockMvc.perform(get("/septic-records-checklist/georgia/forsyth-county/"))
                .andExpect(content().string(containsString("<title>Forsyth County GA Septic Permit Lookup | SepticPath</title>")));
    }

    @Test
    void stateRecordsHubUsesAddressFinderAndCountyPickerInsteadOfALinkWall() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("North Carolina septic records by county")))
                .andExpect(content().string(containsString("permit copy, as-built, final approval, repair record")))
                .andExpect(content().string(containsString("Enter the property address")))
                .andExpect(content().string(containsString("state-records-priority-counties")))
                .andExpect(content().string(containsString("href=\"/septic-records-checklist/north-carolina/buncombe-county/\"")))
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
    void stateHubsAndAgencyWorkspacesHaveDistinctSearchIntents() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/"))
                .andExpect(content().string(containsString("North Carolina Septic Records by County | Address &amp; As-Builts")))
                .andExpect(content().string(containsString("/north-carolina-septic-permit-lookup/")));
        mockMvc.perform(get("/septic-records-checklist/south-carolina/"))
                .andExpect(content().string(containsString("South Carolina Septic Records by County | Address &amp; File Routes")))
                .andExpect(content().string(containsString("/dhec-septic-permit-lookup/")));
        mockMvc.perform(get("/septic-records-checklist/texas/"))
                .andExpect(content().string(containsString("Texas Septic Records by County | Address &amp; Authorized Agents")))
                .andExpect(content().string(containsString("/texas-ossf-records-search/")));
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
