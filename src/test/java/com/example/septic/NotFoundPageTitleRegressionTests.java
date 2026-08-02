package com.example.septic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "app.storage.root=./build/test-storage",
        "app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class NotFoundPageTitleRegressionTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void genericNotFoundPageUsesAGenericBrowserTitle() throws Exception {
        mockMvc.perform(get("/not-a-real-route-for-title-regression/"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(Matchers.containsString("<title>Page Not Found | SepticPath")));
    }
}
