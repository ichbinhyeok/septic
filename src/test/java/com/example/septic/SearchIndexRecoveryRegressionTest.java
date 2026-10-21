package com.example.septic;

import com.example.septic.service.ResearchDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"app.storage.root=./build/test-storage", "app.site.base-url=https://example.test"})
@AutoConfigureMockMvc
class SearchIndexRecoveryRegressionTest {
    @Autowired private MockMvc mvc;
    @Autowired private ResearchDataService data;

    @ParameterizedTest
    @ValueSource(strings = {"alabama", "arkansas", "maine", "new-hampshire", "oregon", "rhode-island", "virginia", "vermont"})
    void everyInstitutionRelatedTargetExistsWithoutInventingStatePages(String state) throws Exception {
        String html = mvc.perform(get("/septic-records-checklist/" + state + "/"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var section = Pattern.compile("<nav aria-label=\"Related [^\"]* septic guidance\">(.*?)</nav>", Pattern.DOTALL).matcher(html);
        assertTrue(section.find(), "Related guidance missing for " + state);
        var anchors = Pattern.compile("href=\"([^\"]+)\"").matcher(section.group(1));
        Set<String> targets = new HashSet<>();
        while (anchors.find()) {
            String target = anchors.group(1).replace("&amp;", "&");
            assertTrue(targets.add(target), "Duplicate related target " + target);
            mvc.perform(get(target)).andExpect(status().isOk());
        }
        assertTrue(targets.size() >= 4, "The fix must preserve useful next steps, not empty the section");
        for (String topic : Set.of("drain-field-replacement-cost", "failed-perc-test-septic", "septic-replacement-area")) {
            String expected = data.findPublicStateMoneyPage(topic, state)
                    .map(p -> p.path(state)).orElse("/" + topic + "/");
            assertTrue(targets.contains(expected), "Missing verified state route or national fallback " + expected);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "septic-replacement-cost/arizona", "septic-replacement-cost/colorado", "septic-replacement-cost/kansas",
            "septic-replacement-cost/maryland", "septic-replacement-cost/new-york", "septic-replacement-cost/nevada",
            "septic-replacement-cost/tennessee", "septic-replacement-cost/south-carolina", "septic-replacement-cost/texas",
            "septic-replacement-cost/virginia", "septic-replacement-cost/utah", "septic-replacement-cost/wisconsin",
            "septic-replacement-cost/wyoming", "septic-inspection-cost/california", "septic-inspection-cost/colorado",
            "septic-inspection-cost/indiana", "septic-inspection-cost/kansas", "septic-inspection-cost/nevada",
            "septic-inspection-cost/south-carolina", "septic-inspection-cost/texas", "septic-inspection-cost/utah",
            "septic-inspection-cost/wyoming"
    })
    void reviewedCostCohortStaysUsableButIsNotSubmittedForSearch(String suffix) throws Exception {
        String route = "/" + suffix + "/";
        mvc.perform(get(route)).andExpect(status().isOk())
                .andExpect(content().string(containsString("<meta name=\"robots\" content=\"noindex,follow\">")))
                .andExpect(content().string(containsString("<link rel=\"canonical\" href=\"https://example.test" + route + "\">")));
        mvc.perform(get("/sitemap.xml"))
                .andExpect(content().string(not(containsString("<loc>https://example.test" + route + "</loc>"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/septic-records-checklist/alabama/morgan-county/", "/septic-records-checklist/north-carolina/harnett-county/",
            "/septic-records-checklist/ohio/", "/how-to-find-septic-records-online/", "/septic-repair-records/",
            "/septic-inspection-cost/arizona/", "/septic-inspection-cost/minnesota/", "/septic-replacement-cost/georgia/"
    })
    void recordsNewCaseAndDistinctLocalWorkflowAssetsRemainIndexable(String route) throws Exception {
        mvc.perform(get(route)).andExpect(status().isOk())
                .andExpect(content().string(containsString("<meta name=\"robots\" content=\"index,follow\">")));
        String sitemap = route.split("/").length > 3 && route.startsWith("/septic-records-checklist/")
                ? "/sitemap-county.xml" : "/sitemap.xml";
        mvc.perform(get(sitemap)).andExpect(content().string(containsString("<loc>https://example.test" + route + "</loc>")));
    }

    @Test
    void strongerContextLinksAreRenderedAndSnippetClaimsStayBounded() throws Exception {
        String al = mvc.perform(get("/septic-records-checklist/alabama/")).andReturn().getResponse().getContentAsString();
        int start = al.indexOf("institution-counties__links");
        assertTrue(start > 0);
        assertTrue(al.substring(start, al.indexOf("</div>", start)).contains("/alabama/morgan-county/"));
        mvc.perform(get("/septic-records-checklist/north-carolina/"))
                .andExpect(content().string(containsString("Read a North Carolina file review")))
                .andExpect(content().string(containsString("Open the Harnett County search and request route")));
        mvc.perform(get("/dhec-septic-permit-lookup/"))
                .andExpect(content().string(containsString("<title>DHEC Septic Permit Lookup | South Carolina SCDES Records</title>")))
                .andExpect(content().string(containsString("through SCDES, formerly DHEC")));
    }
}
