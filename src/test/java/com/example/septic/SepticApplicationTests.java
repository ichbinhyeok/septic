package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SepticApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void homePageRenders() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic System Cost & Size Estimator")));
	}

	@Test
	void calculatorReturnsEstimate() throws Exception {
		mockMvc.perform(post("/septic-system-cost-calculator/")
						.param("stateCode", "GA")
						.param("projectType", "replacement")
						.param("bedrooms", "4")
						.param("occupants", "5")
						.param("soilPercStatus", "poor_drainage")
						.param("accessDifficulty", "hard")
						.param("timeline", "this_month")
						.param("garbageDisposal", "true")
						.param("highWaterTableOrShallowBedrock", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Likely total cost range")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia")));
	}

	@Test
	void stateGuideRenders() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia septic planning guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official sources")));
	}

}
