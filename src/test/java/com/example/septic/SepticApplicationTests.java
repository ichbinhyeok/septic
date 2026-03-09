package com.example.septic;

import com.example.septic.service.EstimatorResult;
import com.example.septic.service.EstimatorService;
import com.example.septic.web.EstimateForm;
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

	@Autowired
	private EstimatorService estimatorService;

	@Test
	void contextLoads() {
	}

	@Test
	void homePageRenders() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic System Cost & Size Estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/privacy-policy/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/privacy-policy/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-tank-size-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-pump-schedule-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/florida/")));
	}

	@Test
	void privacyPolicyPageRenders() throws Exception {
		mockMvc.perform(get("/privacy-policy/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Privacy Policy")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("consent snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/terms-of-use/")));
	}

	@Test
	void aboutPageRenders() throws Exception {
		mockMvc.perform(get("/about/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("About this project")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Not engineering design software")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("derived state planning cost anchor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last verified")));
	}

	@Test
	void tankSizeEstimatorPageRenders() throws Exception {
		mockMvc.perform(get("/septic-tank-size-estimator/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Tank Size Estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Occupancy profile")));
	}

	@Test
	void tankSizeEstimatorReturnsResult() throws Exception {
		mockMvc.perform(post("/septic-tank-size-estimator/")
						.param("stateCode", "GA")
						.param("bedrooms", "4")
						.param("occupancyProfile", "high")
						.param("garbageDisposal", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia tank size planning range")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("High occupancy")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50 percent larger")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the full cost estimator")));
	}

	@Test
	void pumpScheduleEstimatorPageRenders() throws Exception {
		mockMvc.perform(get("/septic-pump-schedule-estimator/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Pump Schedule Estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use profile")));
	}

	@Test
	void pumpScheduleEstimatorReturnsCadence() throws Exception {
		mockMvc.perform(post("/septic-pump-schedule-estimator/")
						.param("tankSizeGallons", "1000")
						.param("occupants", "5")
						.param("garbageDisposal", "true")
						.param("usageProfile", "full_time"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("About every 2 to 3 years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("homeowner check yearly")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the cost estimator")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Quick facts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Planning cost snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority directory")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Local action checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQ")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQPage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with the disposal rule in mind")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit timeline watch")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Verify locally")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official sources")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<link rel=\"canonical\" href=\"https://example.test/septic-system-cost-calculator/georgia/\">")));
	}

	@Test
	void massachusettsStateGuideShowsTitle5Context() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Title 5")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with Title 5 timing in mind")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying or Selling Property with a Septic System")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who to call first")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Records to request first")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance contract")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Special state wrinkle")));
	}

	@Test
	void stateReplacementMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Planning cost snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Replacement midpoint")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50 percent larger")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with the disposal rule in mind")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official-source context")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=replacement")));
	}

	@Test
	void statePercMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What can kill the low end")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=perc_test")));
	}

	@Test
	void stateBuyerMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Title 5")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/massachusetts/")))
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
	void inspectionCostContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/")));
	}

	@Test
	void permitProcessContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Permit Process")));
	}

	@Test
	void recordsChecklistContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Records Checklist")));
	}

	@Test
	void floridaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Quick facts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Find the local permitting authority")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DEP-managed county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Health Department Locations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=new_install")));
	}

	@Test
	void massachusettsInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Title 5")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=inspection")));
	}

	@Test
	void washingtonInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("every three years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=inspection")));
	}

	@Test
	void washingtonRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built drawing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Local Health Jurisdictions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=buying_home")));
	}

	@Test
	void northCarolinaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("improvement permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=new_install")));
	}

	@Test
	void oregonRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Locating Septic System Records Online")));
	}

	@Test
	void connecticutBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("potential bedrooms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=buying_home")));
	}

	@Test
	void tankSizeContentPagePointsToDedicatedEstimator() throws Exception {
		mockMvc.perform(get("/septic-tank-size/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-tank-size-estimator/")));
	}

	@Test
	void pumpingContentPagePointsToDedicatedEstimator() throws Exception {
		mockMvc.perform(get("/septic-pumping-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-pump-schedule-estimator/")));
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
	void quoteSubmissionAllowsMissingOptionalOccupants() throws Exception {
		mockMvc.perform(post("/quote-request/")
						.param("stateCode", "GA")
						.param("projectType", "replacement")
						.param("bedrooms", "4")
						.param("soilPercStatus", "poor_drainage")
						.param("accessDifficulty", "hard")
						.param("timeline", "this_month")
						.param("fullName", "Taylor Shin")
						.param("email", "taylor@example.com")
						.param("phone", "5551234567")
						.param("zipCode", "30301")
						.param("consentAccepted", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request received")));
	}

	@Test
	void massachusettsEstimateRunsHigherThanGeorgiaForSameBaseInputs() {
		EstimateForm georgia = new EstimateForm();
		georgia.setStateCode("GA");
		georgia.setProjectType("replacement");
		georgia.setBedrooms(4);
		georgia.setTimeline("researching");

		EstimateForm massachusetts = new EstimateForm();
		massachusetts.setStateCode("MA");
		massachusetts.setProjectType("replacement");
		massachusetts.setBedrooms(4);
		massachusetts.setTimeline("researching");

		EstimatorResult georgiaResult = estimatorService.estimate(georgia);
		EstimatorResult massachusettsResult = estimatorService.estimate(massachusetts);

		org.junit.jupiter.api.Assertions.assertTrue(
				massachusettsResult.totalCostMid() > georgiaResult.totalCostMid(),
				"Expected Massachusetts midpoint to exceed Georgia midpoint after state multiplier is applied"
		);
		org.junit.jupiter.api.Assertions.assertTrue(
				massachusettsResult.costAnchorNote().contains("derived planning anchor"),
				"Expected Massachusetts result to explain the derived state planning anchor"
		);
	}

	@Test
	void notFoundPageIsNoindex() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/not-a-real-state/"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,nofollow")));
	}

}
