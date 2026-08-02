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
class BedroomCheckerStateSelectionRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullAndEmbeddedCheckersRequireTheActualPropertyState() throws Exception {
        assertStateChoiceRequired("/septic-bedroom-permit-checker/");
        assertStateChoiceRequired("/embed/septic-bedroom-permit-checker/");
    }

    private void assertStateChoiceRequired(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<select required data-bedroom-state>")))
                .andExpect(content().string(containsString("<option value=\"\" selected disabled>Choose a state</option>")))
                .andExpect(content().string(not(containsString("<option value=\"TN\" selected"))));
    }
}
