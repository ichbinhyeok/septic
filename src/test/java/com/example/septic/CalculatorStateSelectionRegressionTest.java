package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class CalculatorStateSelectionRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void nationalCalculatorRequiresAnExplicitStateInsteadOfAssumingGeorgia() throws Exception {
        mockMvc.perform(get("/septic-system-cost-calculator/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<option value=\"\" selected disabled>Choose a state</option>")))
                .andExpect(content().string(not(containsString("<option value=\"GA\" selected"))));
    }

    @Test
    void missingStatePostReturnsAnActionableFormErrorInsteadOfServerFailure() throws Exception {
        mockMvc.perform(post("/septic-system-cost-calculator/")
                        .param("projectType", "replacement")
                        .param("bedrooms", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Choose the property state before showing an estimate.")))
                .andExpect(content().string(not(containsString("Planning cost range"))));
    }
}
