package com.example.septic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"app.storage.root=./build/test-storage",
		"app.site.base-url=https://example.test"
})
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic System Cost & Size Estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("application/ld+json")));
	}

	@Test
	void robotsTxtExposesSitemap() throws Exception {
		mockMvc.perform(get("/robots.txt"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("User-agent: *")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sitemap: https://example.test/sitemap.xml")));
	}

	@Test
	void sitemapXmlIncludesCoreUrls() throws Exception {
		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/georgia/")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50 percent larger")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last verified")));
	}

	@Test
	void connecticutCalculatorShowsDesignFlowContext() throws Exception {
		mockMvc.perform(post("/septic-system-cost-calculator/")
						.param("stateCode", "CT")
						.param("projectType", "new_install")
						.param("bedrooms", "4")
						.param("occupants", "7")
						.param("soilPercStatus", "unknown")
						.param("accessDifficulty", "medium")
						.param("timeline", "researching"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("150 gallons per bedroom")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("product planning bridge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State rule context")));
	}

	@Test
	void oregonCalculatorShowsWideRangeReason() throws Exception {
		mockMvc.perform(post("/septic-system-cost-calculator/")
						.param("stateCode", "OR")
						.param("projectType", "replacement")
						.param("bedrooms", "3")
						.param("soilPercStatus", "unknown")
						.param("accessDifficulty", "easy")
						.param("timeline", "researching"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation does not guarantee approval of any specific system type")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Likely permit path")));
	}

	@Test
	void stateGuideRenders() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia septic planning guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official sources")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<link rel=\"canonical\" href=\"https://example.test/septic-system-cost-calculator/georgia/\">")));
	}

	@Test
	void massachusettsStateGuideShowsTitle5Context() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Title 5")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying or Selling Property with a Septic System")));
	}

	@Test
	void floridaStateGuideShowsJurisdictionSplit() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("16 counties")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health department")));
	}

	@Test
	void newJerseyStateGuideShowsManagementAngle() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pinelands")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance contract")));
	}

	@Test
	void stateReplacementMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50 percent larger")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official-source context")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=replacement")));
	}

	@Test
	void statePercMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What to verify before trusting the low end")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=perc_test")));
	}

	@Test
	void stateBuyerMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Title 5")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=buying_home")));
	}

	@Test
	void stateDrainFieldMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("usable drainfield area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=drainfield_replacement")));
	}

	@Test
	void statePumpingMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/septic-pumping-cost/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Pumping Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("once every three years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=pumping")));
	}

	@Test
	void moneyPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Main estimate drivers")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Replacement Cost")));
	}

	@Test
	void quoteSubmissionCreatesLeadArtifacts() throws Exception {
		mockMvc.perform(post("/quote-request/")
						.param("stateCode", "GA")
						.param("projectType", "replacement")
						.param("bedrooms", "4")
						.param("occupants", "5")
						.param("soilPercStatus", "poor_drainage")
						.param("accessDifficulty", "hard")
						.param("timeline", "this_month")
						.param("garbageDisposal", "true")
						.param("highWaterTableOrShallowBedrock", "true")
						.param("fullName", "Taylor Shin")
						.param("email", "taylor@example.com")
						.param("phone", "5551234567")
						.param("zipCode", "30301")
						.param("consentAccepted", "true")
						.header("User-Agent", "MockBrowser/1.0")
						.header("Referer", "https://example.test/septic-replacement-cost/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request received")));

		try (Stream<Path> leadFiles = Files.walk(Path.of("build/test-storage/leads"))) {
			org.junit.jupiter.api.Assertions.assertTrue(
					leadFiles.anyMatch(path -> path.toString().endsWith(".json")),
					"Expected at least one stored lead file"
			);
		}

		try (Stream<Path> eventFiles = Files.walk(Path.of("build/test-storage/events"))) {
			org.junit.jupiter.api.Assertions.assertTrue(
					eventFiles.anyMatch(path -> path.toString().endsWith(".ndjson")),
					"Expected at least one stored event file"
			);
		}

		Path exportJson;
		try (Stream<Path> exportFiles = Files.walk(Path.of("build/test-storage/exports/pending"))) {
			exportJson = exportFiles
					.filter(path -> path.toString().endsWith(".json"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one export JSON file"));
		}
		String exportContent = Files.readString(exportJson);
		org.junit.jupiter.api.Assertions.assertTrue(exportContent.contains("\"exportStatus\" : \"pending_routing\""));
		org.junit.jupiter.api.Assertions.assertTrue(exportContent.contains("\"consentLanguageVersion\" : \"2026-03-09-v1\""));
		org.junit.jupiter.api.Assertions.assertTrue(exportContent.contains("\"userAgent\" : \"MockBrowser/1.0\""));

		Path exportCsv;
		try (Stream<Path> exportCsvFiles = Files.walk(Path.of("build/test-storage/exports/daily"))) {
			exportCsv = exportCsvFiles
					.filter(path -> path.toString().endsWith(".csv"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one export CSV file"));
		}
		String exportCsvContent = Files.readString(exportCsv);
		org.junit.jupiter.api.Assertions.assertTrue(exportCsvContent.contains("lead_id,submitted_at,state_code"));
		org.junit.jupiter.api.Assertions.assertTrue(exportCsvContent.contains("\"GA\""));
	}

	@Test
	void notFoundPageIsNoindex() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/not-a-real-state/"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,nofollow")));
	}

}
