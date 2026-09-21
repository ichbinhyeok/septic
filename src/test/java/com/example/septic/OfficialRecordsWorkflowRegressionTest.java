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
                .andExpect(content().string(containsString("TDEC search failed or came back empty?")))
                .andExpect(content().string(containsString("Send the address.")))
                .andExpect(content().string(containsString("We chase the record.")))
                .andExpect(content().string(containsString("Choose the property county")))
                .andExpect(content().string(containsString("Ask SepticPath to investigate my property")))
                .andExpect(content().string(containsString("Try the official viewer anyway")))
                .andExpect(content().string(containsString("Viewer status:")))
                .andExpect(content().string(containsString("https://www.tn.gov/environment/about-tdec/tdec-dataviewers.html")))
                .andExpect(content().string(containsString("https://dataviewers.tdec.tn.gov/dataviewers/f?p=175")))
                .andExpect(content().string(containsString("Research and agency requests are free. Optional $29 result unlock")))
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
                .andExpect(content().string(containsString("This self-serve tool")))
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

        int priorityStart = html.indexOf("Popular county permit searches");
        int priorityEnd = html.indexOf("More high-demand county routes", priorityStart);
        String visiblePriorityRoutes = html.substring(priorityStart, priorityEnd);
        org.assertj.core.api.Assertions.assertThat(visiblePriorityRoutes)
                .containsSubsequence(
                        "Johnston County septic permit lookup",
                        "Forsyth County septic permit lookup",
                        "Pender County septic permit lookup",
                        "Wake County septic permit lookup",
                        "Pitt County septic permit lookup",
                        "Buncombe County septic permit lookup"
                );
    }

    @Test
    void northCarolinaCountyTitlesMatchObservedPermitSearchIntent() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/north-carolina/forsyth-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Forsyth County NC Septic Permit Lookup &amp; Records | SepticPath</title>")))
                .andExpect(content().string(containsString("<h1 id=\"premium-county-title\" class=\"county-records-page__search-title\">Forsyth County NC septic permit lookup and records</h1>")));
        mockMvc.perform(get("/septic-records-checklist/north-carolina/pender-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Pender County NC Septic Permit Search &amp; Records | SepticPath</title>")))
                .andExpect(content().string(containsString("Pender County NC septic permit search and records</h1>")));
        mockMvc.perform(get("/septic-records-checklist/north-carolina/alamance-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Alamance County NC Septic Records &amp; Permit Lookup | SepticPath</title>")))
                .andExpect(content().string(containsString("Alamance County NC septic records and permit lookup</h1>")));
    }

    @Test
    void additionalCountyPagesMatchObservedSystemSearchIntentWithoutAddingDuplicates() throws Exception {
        mockMvc.perform(get("/septic-records-checklist/washington/king-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>King County Septic System Search &amp; As-Built Records | SepticPath</title>")))
                .andExpect(content().string(containsString("King County septic system search and as-built records</h1>")))
                .andExpect(content().string(containsString("<link rel=\"canonical\" href=\"https://example.test/septic-records-checklist/washington/king-county/\">")));
        mockMvc.perform(get("/septic-records-checklist/arizona/maricopa-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Maricopa County septic search and permit records</h1>")));
    }

    @Test
    void acquisitionPagesUseQueryAlignedTitlesAndDescriptions() throws Exception {
        mockMvc.perform(get("/tdec-septic-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>TN Septic Permit Search | TDEC Records &amp; File Help</title>")))
                .andExpect(content().string(containsString("If you get a 403, no record, or the wrong address")));
        mockMvc.perform(get("/septic-system-cost-calculator/alabama/"))
                .andExpect(content().string(containsString("<title>Alabama Perc Test Cost: $300-$2,700 + County Fees</title>")));
        mockMvc.perform(get("/texas-ossf-records-search/"))
                .andExpect(content().string(containsString("<title>Texas OSSF Permit Search by Address | Local Records</title>")))
                .andExpect(content().string(containsString("identify the local permitting authority")));
        mockMvc.perform(get("/septic-records-checklist/new-hampshire/"))
                .andExpect(content().string(containsString("<title>NH Septic Permit Lookup | NHDES OneStop &amp; Plans</title>")));
        mockMvc.perform(get("/septic-records-checklist/north-carolina/union-county/"))
                .andExpect(content().string(containsString("<title>Union County NC Septic Permit Search | Existing Records | SepticPath</title>")));
        mockMvc.perform(get("/septic-records-checklist/tennessee/wilson-county/"))
                .andExpect(content().string(containsString("<title>Wilson County TN Septic Permit Search | TDEC Records | SepticPath</title>")));
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
                .andExpect(content().string(containsString("North Carolina Septic Records by County | Permit Lookup")))
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
                    .andExpect(content().string(containsString("SepticPath can investigate the record and contact the responsible office for you")))
                    .andExpect(content().string(containsString("Ask SepticPath to investigate")))
                    .andExpect(content().string(containsString("A failed address search is not proof that no file exists")))
                    .andExpect(content().string(not(containsString("OFFICIAL LOOKUP COMMAND BOARD"))))
                    .andExpect(content().string(not(containsString("PREPARED BY"))));
        }
    }

    @Test
    void tankLocationPagePublishesAFilterableOfficialMapDirectory() throws Exception {
        mockMvc.perform(get("/septic-tank-location-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Official map &amp; GIS directory")))
                .andExpect(content().string(containsString("data-official-map-directory")))
                .andExpect(content().string(containsString("data-map-directory-type")))
                .andExpect(content().string(containsString("data-map-directory-state")))
                .andExpect(content().string(containsString("value=\"GA\" data-map-directory-state-option")))
                .andExpect(content().string(containsString("value=\"NC\" data-map-directory-state-option")))
                .andExpect(content().string(not(containsString("value=\"AK\" data-map-directory-state-option"))))
                .andExpect(content().string(containsString("data-map-directory-documents")))
                .andExpect(content().string(containsString("data-map-directory-empty")))
                .andExpect(content().string(containsString("data-map-type=\"record_map\"")))
                .andExpect(content().string(containsString("data-map-type=\"direct_search\"")))
                .andExpect(content().string(containsString("Gwinnett County")))
                .andExpect(content().string(containsString("Craven County")))
                .andExpect(content().string(containsString("tank_location_official_map")))
                .andExpect(content().string(containsString("A missing map does not mean the septic file is missing.")))
                .andExpect(content().string(containsString("Find the county request route")));
    }
}
