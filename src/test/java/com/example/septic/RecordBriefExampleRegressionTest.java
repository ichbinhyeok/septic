package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class RecordBriefExampleRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void completedRecordBriefShowsEvidenceFactsLimitsAndPilotPath() throws Exception {
        mockMvc.perform(get("/septic-record-brief-example/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Official records.")))
                .andExpect(content().string(containsString("Answers you can understand.")))
                .andExpect(content().string(containsString("THE RECORD QUESTION")))
                .andExpect(content().string(containsString("SEPTICPATH DESK WORK")))
                .andExpect(content().string(containsString("RECORDS AND EXPLANATION")))
                .andExpect(content().string(containsString("Finding the missing file.")))
                .andExpect(content().string(containsString("Official portals")))
                .andExpect(content().string(containsString("Parcel + GIS")))
                .andExpect(content().string(containsString("An outdated contact was a failed route, not a final answer.")))
                .andExpect(content().string(containsString("One permit in the file is not proof that nothing ever changed.")))
                .andExpect(content().string(containsString("The plan supplied the missing specifications—not a current inspection.")))
                .andExpect(content().string(containsString("3 COUNTY PAGES → 1 RECORD BRIEF")))
                .andExpect(content().string(containsString("Original files + clear findings + next steps")))
                .andExpect(content().string(containsString("SIGNATURES EXCLUDED")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-approval-letter-excerpt-public.png")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-site-plan-detail-public.png")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-brief-findings-public.png")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-brief-field-use-public.png")))
                .andExpect(content().string(containsString("href=\"/offer-prep-septic-file-check/#record-help\"")));
    }

    @Test
    void realCasesKeepTheirSourcesSeparateFromTheIllustrativePreview() throws Exception {
        mockMvc.perform(get("/septic-record-brief-example/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"sale-case\"")))
                .andExpect(content().string(containsString("data-reading-image=\"sale-plan\"")))
                .andExpect(content().string(containsString("id=\"sale-case-limits\"")))
                .andExpect(content().string(containsString("href=\"/septic-as-built-records/#layout-case\"")))
                .andExpect(content().string(containsString("separate from the sale-preparation case below")))
                .andExpect(content().string(containsString("Illustrative preview")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("SP–TN–028"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("storage/operations"))));
        mockMvc.perform(get("/septic-as-built-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"layout-case\"")))
                .andExpect(content().string(containsString("data-reading-milestone=\"layout-case\"")))
                .andExpect(content().string(containsString("href=\"/septic-record-brief-example/#sale-case\"")))
                .andExpect(content().string(containsString("not a completed site inspection")));
    }

    @Test
    void caseCollectionLinksFiveRealStoriesAndKeepsTheLimitedMatchAccurate() throws Exception {
        var response = mockMvc.perform(get("/septic-record-brief-example/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"real-cases\"")))
                .andExpect(content().string(containsString("data-reading-milestone=\"real-cases\"")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("explained why the lengths differed"))))
                .andExpect(content().string(containsString("Start with a question like yours.")))
                .andExpect(content().string(containsString("without presenting it as a confirmed property match")));
        for (String target : new String[] {
                "/septic-as-built-records/#layout-case",
                "/septic-record-brief-example/#sale-case",
                "/septic-permit-process/#design-final-case",
                "/septic-tank-location-records/#addition-file-case",
                "/septic-permit-search-by-address/#old-address-case"}) {
            response.andExpect(content().string(containsString("href=\"" + target + "\"")));
        }
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Explore all five real record-help cases")))
                .andExpect(content().string(containsString("href=\"/septic-record-brief-example/#real-cases\"")));
    }

    @Test
    void originalFindingVisualUsesRecordedFiguresWithoutPretendingToBeASitePlan() throws Exception {
        mockMvc.perform(get("/septic-record-brief-example/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"record-finding-comparison\"")))
                .andExpect(content().string(containsString("data-reading-milestone=\"record-finding-comparison\"")))
                .andExpect(content().string(containsString("40 + 80 + 80 ft")))
                .andExpect(content().string(containsString("Source: Permit to Construct")))
                .andExpect(content().string(containsString("Source: Final approval")))
                .andExpect(content().string(containsString("Planned vs. recorded.")))
                .andExpect(content().string(containsString("SepticPath-created comparison based on recorded figures")))
                .andExpect(content().string(containsString("Not an original document, site plan or physical layout")))
                .andExpect(content().string(containsString("How we reached this finding")));
    }

    @Test
    void eachCompletedCaseHasItsOwnSourceBoundEditorialVisual() throws Exception {
        String[][] visuals = {
                {"/septic-as-built-records/", "record-finding-roane", "225 linear feet", "not the original drawing"},
                {"/septic-record-brief-example/", "record-finding-shelby", "A dead end was not the answer.", "does not reproduce the County file"},
                {"/septic-tank-location-records/", "record-finding-stcroix", "Twenty-six pages in. Three pages out.", "both recorded values"},
                {"/septic-permit-search-by-address/", "record-finding-overton", "LIMITED MATCH", "not proof of the current property's system"}
        };

        for (String[] visual : visuals) {
            mockMvc.perform(get(visual[0]))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("id=\"" + visual[1] + "\"")))
                    .andExpect(content().string(containsString("data-reading-milestone=\"" + visual[1] + "\"")))
                    .andExpect(content().string(containsString(visual[2])))
                    .andExpect(content().string(containsString(visual[3])))
                    .andExpect(content().string(containsString("How we reached this finding")))
                    .andExpect(content().string(org.hamcrest.Matchers.not(containsString("storage/operations"))));
        }
    }

    @Test
    void additionalRealCaseGuidesAreServerRenderedAndDiscoverableFromHome() throws Exception {
        String[][] cases = {
                {"/septic-permit-search-by-address/", "old-address-case"},
                {"/septic-permit-process/", "design-final-case"},
                {"/septic-tank-location-records/", "addition-file-case"}
        };
        for (String[] story : cases) {
            mockMvc.perform(get(story[0]))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("id=\"" + story[1] + "\"")))
                    .andExpect(content().string(containsString("data-reading-milestone=\"" + story[1] + "\"")))
                    .andExpect(content().string(org.hamcrest.Matchers.not(containsString("storage/operations"))));
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("href=\"" + story[0] + "#" + story[1] + "\"")));
        }
    }
}
