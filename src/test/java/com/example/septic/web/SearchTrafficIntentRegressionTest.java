package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SearchTrafficIntentRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void tdecSnippetMatchesHighImpressionTennesseeSearchIntent() throws Exception {
        mockMvc.perform(get("/tdec-septic-records/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>TDEC Septic Permit Search | Tennessee Records &amp; Free Help</title>")))
                .andExpect(content().string(containsString("Get free record research and an explanation when needed")))
                .andExpect(content().string(containsString("href=\"/septic-backup-slow-drains/\"")))
                .andExpect(content().string(containsString("href=\"/wet-yard-over-septic-drain-field/\"")));
    }

    @Test
    void backupAndSlowDrainIntentHasAnIndexableProblemFirstPage() throws Exception {
        mockMvc.perform(get("/septic-backup-slow-drains/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Septic Backup or Slow Drains: What to Do First | SepticPath</title>")))
                .andExpect(content().string(containsString("<h1>Septic Backup or Slow Drains: What to Do First</h1>")))
                .andExpect(content().string(containsString("Reduce water use immediately")))
                .andExpect(content().string(containsString("/septic-pumping-cost/")))
                .andExpect(content().string(containsString("/septic-as-built-records/")));
    }
}
