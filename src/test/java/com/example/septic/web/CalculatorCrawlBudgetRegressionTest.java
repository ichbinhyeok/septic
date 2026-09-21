package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.storage.root=./build/calculator-crawl-budget-test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class CalculatorCrawlBudgetRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void parameterizedCalculatorKeepsCleanCanonicalAndNoindex() throws Exception {
        mockMvc.perform(get("/septic-system-cost-calculator/")
                        .param("state", "AL")
                        .param("projectType", "replacement")
                        .param("sourcePageHint", "/septic-replacement-cost/")
                        .param("quoteMode", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "<link rel=\"canonical\" href=\"https://example.test/septic-system-cost-calculator/\">")))
                .andExpect(content().string(containsString(
                        "<meta name=\"robots\" content=\"noindex,follow\">")));
    }

    @Test
    void contentPagePrefillAndQuoteLinksAreNofollow() throws Exception {
        mockMvc.perform(get("/septic-replacement-cost/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "href=\"/septic-system-cost-calculator/?projectType=replacement&amp;sourcePageHint=/septic-replacement-cost/\" rel=\"nofollow\"")))
                .andExpect(content().string(containsString(
                        "sourcePageHint=/septic-replacement-cost/&amp;quoteMode=true#quote-request\" rel=\"nofollow\"")));
    }

    @Test
    void secondaryCalculatorActionWithPrefillIsNofollow() throws Exception {
        mockMvc.perform(get("/perc-test-cost/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "href=\"/septic-system-cost-calculator/?projectType=perc_test&amp;sourcePageHint=/perc-test-cost/\"")))
                .andExpect(content().string(containsString(
                        "href=\"/septic-system-cost-calculator/?projectType=perc_test&amp;sourcePageHint=/perc-test-cost/\"\n                                           rel=\"nofollow\"")));
    }

    @Test
    void retiredStandaloneToolsRedirectAndLeaveTheSitemap() throws Exception {
        mockMvc.perform(get("/septic-tank-size-estimator/").param("state", "GA"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/septic-system-cost-calculator/?mode=tank_size&state=GA"));
        mockMvc.perform(get("/septic-pump-schedule-estimator/"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "/septic-system-cost-calculator/?mode=pump_schedule"));
        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("/septic-tank-size-estimator/"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("/septic-pump-schedule-estimator/"))));
    }

}
