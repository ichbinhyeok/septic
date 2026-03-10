package com.example.septic;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.FaqBlock;
import com.example.septic.data.model.StateMoneyPage;
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
import java.util.List;
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
	void contentPagesRequireExplicitPublishApproval() {
		ContentPage unpublished = new ContentPage(
				"sample-slug",
				"money_page",
				"sample keyword",
				List.of("sample secondary"),
				"Sample title",
				"Sample description",
				"Sample intro",
				"main_cost_estimator",
				"replacement",
				"Best for users who need a concrete next step.",
				List.of("Fit bullet one", "Fit bullet two"),
				List.of("Step one", "Step two"),
				List.of("Risk one"),
				List.of("Checklist item"),
				List.of("Driver one", "Driver two"),
				List.of(
						new FaqBlock("Question one?", "Answer one."),
						new FaqBlock("Question two?", "Answer two.")
				),
				List.of("/septic-replacement-cost/"),
				null
		);

		ContentPage published = new ContentPage(
				unpublished.slug(),
				unpublished.intentType(),
				unpublished.primaryKeyword(),
				unpublished.secondaryKeywords(),
				unpublished.title(),
				unpublished.metaDescription(),
				unpublished.introCopy(),
				unpublished.calculatorModule(),
				unpublished.calculatorProjectType(),
				unpublished.targetReader(),
				unpublished.fitBullets(),
				unpublished.decisionSteps(),
				unpublished.lowEndBreakers(),
				unpublished.quotePrepChecklist(),
				unpublished.driverBullets(),
				unpublished.faqBlocks(),
				unpublished.internalLinkTargets(),
				"published"
		);

		org.junit.jupiter.api.Assertions.assertFalse(unpublished.isPublished());
		org.junit.jupiter.api.Assertions.assertTrue(published.isPublished());
	}

	@Test
	void stateMoneyPagesRequireExplicitPublishApproval() {
		StateMoneyPage unpublished = new StateMoneyPage(
				"septic-replacement-cost",
				"GA",
				"Georgia Septic Replacement Cost",
				"Replacement planning page",
				"Sample intro",
				"Sample unique angle",
				"Best for owners who need a verified replacement path.",
				List.of("Fit bullet one", "Fit bullet two"),
				List.of("Step one", "Step two"),
				List.of("Risk one"),
				List.of("Checklist item"),
				List.of("Driver one", "Driver two"),
				List.of(
						new FaqBlock("Question one?", "Answer one."),
						new FaqBlock("Question two?", "Answer two.")
				),
				List.of("/septic-system-cost-calculator/georgia/"),
				List.of("ga_01"),
				"replacement",
				null
		);

		StateMoneyPage published = new StateMoneyPage(
				unpublished.contentSlug(),
				unpublished.stateCode(),
				unpublished.title(),
				unpublished.metaDescription(),
				unpublished.introCopy(),
				unpublished.uniqueAngle(),
				unpublished.targetReader(),
				unpublished.fitBullets(),
				unpublished.decisionSteps(),
				unpublished.lowEndBreakers(),
				unpublished.quotePrepChecklist(),
				unpublished.driverBullets(),
				unpublished.faqBlocks(),
				unpublished.internalLinkTargets(),
				unpublished.officialSourceIds(),
				unpublished.calculatorProjectType(),
				"published"
		);

		org.junit.jupiter.api.Assertions.assertFalse(unpublished.isPublished());
		org.junit.jupiter.api.Assertions.assertTrue(published.isPublished());
	}

	@Test
	void homePageRenders() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic System Cost & Size Estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-site-nav-toggle")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-nav-menu")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/privacy-policy/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("application/ld+json")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State guides")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/social-card.svg")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Anchor states"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Supporting states"))));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<lastmod>2026-03-09</lastmod>")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("payment-card")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/terms-of-use/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("early-stage file-backed application"))));
	}

	@Test
	void aboutPageRenders() throws Exception {
		mockMvc.perform(get("/about/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("About this project")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Not engineering design software")));
	}

	@Test
	void termsOfUsePageRendersServiceTone() throws Exception {
		mockMvc.perform(get("/terms-of-use/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Terms of Use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("planning tool, not a permit or compliance service")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("active development"))));
	}

	@Test
	void contactPageRendersWorkingIntake() throws Exception {
		mockMvc.perform(get("/contact/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("General questions, source corrections, and privacy requests")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Send a contact request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Source correction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?quoteMode=true#quote-request")));
	}

	@Test
	void contactRequestValidationShowsError() throws Exception {
		mockMvc.perform(post("/contact/")
						.param("topic", "source_correction")
						.param("stateCode", "WA")
						.param("message", "Please review the Washington inspection wording."))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Finish the required fields")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Name, email, message, and acknowledgement are required before this request can be stored.")));
	}

	@Test
	void contactRequestCreatesArtifact() throws Exception {
		mockMvc.perform(post("/contact/")
						.param("fullName", "Taylor Shin")
						.param("email", "taylor@example.com")
						.param("topic", "source_correction")
						.param("stateCode", "WA")
						.param("message", "Please review the Washington inspection page for advanced-system cadence wording.")
						.param("acknowledgementAccepted", "true")
						.header("User-Agent", "MockBrowser/1.0")
						.header("Referer", "https://example.test/contact/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request received")));

		try (Stream<Path> requestFiles = Files.walk(Path.of("build/test-storage/contact-requests"))) {
			Path requestFile = requestFiles
					.filter(path -> path.toString().endsWith(".json"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one contact request JSON file"));
			String requestContent = Files.readString(requestFile);
			org.junit.jupiter.api.Assertions.assertTrue(requestContent.contains("\"topic\" : \"source_correction\""));
			org.junit.jupiter.api.Assertions.assertTrue(requestContent.contains("\"stateCode\" : \"WA\""));
			org.junit.jupiter.api.Assertions.assertTrue(requestContent.contains("advanced-system cadence wording"));
		}

		try (Stream<Path> eventFiles = Files.walk(Path.of("build/test-storage/events"))) {
			Path eventFile = eventFiles
					.filter(path -> path.toString().endsWith(".ndjson"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one event NDJSON file"));
			String eventContent = Files.readString(eventFile);
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"eventType\":\"contact_request_submitted\""));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"topic\":\"source_correction\""));
		}
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last verified")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cost evidence behind this planning range")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("National replacement planning anchor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia regional price-level adjustment")));
	}

	@Test
	void calculatorSupportsDirectQuoteMode() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/")
						.param("state", "GA")
						.param("projectType", "replacement")
						.param("quoteMode", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Get matched with local septic pros")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Skip the estimate if you already know the job type")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Full name")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Project type")));
	}

	@Test
	void calculatorLandingRendersGuidance() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who should use this calculator first")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use the estimate before you ask for quotes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("High-intent next paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use this when failure scope or full replacement risk is the real blocker.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/perc-test-cost/")));
	}

	@Test
	void tankSizeEstimatorPageRenders() throws Exception {
		mockMvc.perform(get("/septic-tank-size-estimator/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Tank Size Estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Occupancy profile")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia sizing rule snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Source-backed sizing facts for Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minimum approved tank size")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Garbage disposal rule")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How should a homeowner use this septic tank size estimator?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQPage")));
	}

	@Test
	void tankSizeEstimatorSupportsStatePrefill() throws Exception {
		mockMvc.perform(get("/septic-tank-size-estimator/").param("state", "CT"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut sizing rule snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Source-backed sizing facts for Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Residential design flow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Connecticut guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-tank-size-estimator/?state=CT")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the full cost estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open pump schedule estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Georgia guide")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia septic cost guide and tank size estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Cost Guide, Tank Size, and Permit Notes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Prepared by")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Homeowner Planning Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed by")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State Source Review Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 2 official sources listed below.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-03-09\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"editor\":{\"@type\":\"Organization\",\"name\":\"State Source Review Desk\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Quick facts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Source-backed rule facts for Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Very high confidence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Planning cost snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority directory")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How the core six launch states differ")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("You are here")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Local action checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQ")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQPage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with the disposal rule in mind")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("quoteMode=true#quote-request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Get matched with local septic pros")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit timeline watch")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Verify locally")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official sources")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_primary_calculator\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_next_high_intent\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<link rel=\"canonical\" href=\"https://example.test/septic-system-cost-calculator/georgia/\">")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Still under review"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Confidence:"))));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida septic cost guide and DEP vs county path")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 2 official sources tied to this page and state workflow.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Homeowner Planning Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State Source Review Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last reviewed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-03-09\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county environmental health office and pull the latest permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Planning cost snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Replacement midpoint")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50 percent larger")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with the disposal rule in mind")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("quote-request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Get matched with local septic pros")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official-source context")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Georgia guide for permit path, local office, and records workflow context.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_primary_calculator\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_related_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=replacement")));
	}

	@Test
	void navigationClickEventIsStored() throws Exception {
		mockMvc.perform(post("/events/nav-click")
						.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
						.content("""
								{
								  "sourcePage": "/septic-system-cost-calculator/georgia/",
								  "sourceContext": "state_guide_next_high_intent",
								  "targetPath": "/septic-replacement-cost/georgia/",
								  "targetType": "state_money_page",
								  "targetLabel": "Georgia Septic Replacement Cost"
								}
								""")
						.header("User-Agent", "MockBrowser/1.0")
						.header("Referer", "https://example.test/septic-system-cost-calculator/georgia/"))
				.andExpect(status().isNoContent());

		try (Stream<Path> eventFiles = Files.walk(Path.of("build/test-storage/events"))) {
			Path eventFile = eventFiles
					.filter(path -> path.toString().endsWith(".ndjson"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one event NDJSON file"));
			String eventContent = Files.readString(eventFile);
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"eventType\":\"internal_navigation_click\""));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"sourceContext\":\"state_guide_next_high_intent\""));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"targetPath\":\"/septic-replacement-cost/georgia/\""));
		}
	}

	@Test
	void washingtonReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The as-built drawing and confirmation of whether the system is gravity or advanced.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health jurisdiction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=replacement")));
	}

	@Test
	void massachusettsReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest Title 5 inspection report and inspection date.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=replacement")));
	}

	@Test
	void oregonReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any record of use change, bedroom increase, ADU plan, or added sewage flow tied to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=replacement")));
	}

	@Test
	void pennsylvaniaReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sewage Enforcement Officer before trusting any statewide replacement average.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=replacement")));
	}

	@Test
	void missouriReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/missouri/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Missouri Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Missouri")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and the county or local authority that controls the onsite file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MO&projectType=replacement")));
	}

	@Test
	void statePercMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation, not a generic perc quote")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What widens this Oregon site-testing range")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=perc_test")));
	}

	@Test
	void georgiaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and county environmental health office handling the lot.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=perc_test")));
	}

	@Test
	void connecticutPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any contractor or inspector note already questioning the reserve area or code-complying area.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=perc_test")));
	}

	@Test
	void missouriPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/missouri/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Missouri Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Missouri")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and county or local authority handling the lot.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MO&projectType=perc_test")));
	}

	@Test
	void northCarolinaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The county health department contact and file reference for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=perc_test")));
	}

	@Test
	void pennsylvaniaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sewage Enforcement Officer handling the property")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=perc_test")));
	}

	@Test
	void massachusettsPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The address and local Board of Health contact so you can confirm what the town already expects before comparing prices.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=perc_test")));
	}

	@Test
	void floridaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and county so you can identify the right Florida authority first.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=perc_test")));
	}

	@Test
	void washingtonPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The parcel address and local health jurisdiction handling the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=perc_test")));
	}

	@Test
	void newJerseyPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Jersey Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and local board of health contact for the lot.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NJ&projectType=perc_test")));
	}

	@Test
	void stateBuyerMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest Title 5 inspection report with the inspection date clearly visible.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Title 5")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=buying_home")));
	}

	@Test
	void floridaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and county so you can confirm the correct Florida authority path.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=buying_home")));
	}

	@Test
	void newJerseyBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any service contract, maintenance agreement, or board-of-health notice tied to advanced treatment or special-area oversight.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NJ&projectType=buying_home")));
	}

	@Test
	void connecticutReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("potential-bedroom issue")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("workable code-complying and reserve area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=replacement")));
	}

	@Test
	void floridaReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and county so you can confirm whether DEP or the county health department controls the next step.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=replacement")));
	}

	@Test
	void northCarolinaReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The county health department file reference and contact for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=replacement")));
	}

	@Test
	void newJerseyReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Jersey Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any service contract, management notice, or recurring certification document tied to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NJ&projectType=replacement")));
	}

	@Test
	void stateDrainFieldMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and county environmental health office handling the file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=drainfield_replacement")));
	}

	@Test
	void oregonDrainFieldPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any contractor note suggesting the current field footprint or replacement area may not work.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=drainfield_replacement")));
	}

	@Test
	void statePumpingMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/septic-pumping-cost/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Pumping Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest pumping and inspection records for the system.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("once every three years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=pumping")));
	}

	@Test
	void moneyPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use this page before you ask for quotes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Main estimate drivers")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a replacement planning estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=replacement")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Drain Field Replacement Cost<")));
	}

	@Test
	void inspectionCostContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run an inspection-risk estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Buying a House With a Septic System<")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("This page exists to support high-intent search"))));
	}

	@Test
	void permitProcessContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use this page before you ask for quotes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a permit-path estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=new_install")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Permit Process")));
	}

	@Test
	void recordsChecklistContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What usually kills the low end")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a records-aware estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=buying_home")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against the linked state-specific pages and source policy.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Intent Map Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State Source Review Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last reviewed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-03-09\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"editor\":{\"@type\":\"Organization\",\"name\":\"State Source Review Desk\"")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DEP-managed county or a county health department path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Quick facts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Find the office handling this permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DEP-managed county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Health Department Locations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=new_install")));
	}

	@Test
	void pennsylvaniaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sewage Enforcement Officer contact or directory result for that municipality.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=new_install")));
	}

	@Test
	void connecticutPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any site investigation, soil-testing, or approval-to-construct record already on file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=new_install")));
	}

	@Test
	void georgiaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The county environmental health office handling the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=new_install")));
	}

	@Test
	void massachusettsPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The local Board of Health contact for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=new_install")));
	}

	@Test
	void oregonPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and local onsite program or county contact handling the file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=new_install")));
	}

	@Test
	void newJerseyPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Jersey Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any service contract, management notice, or recurring reporting document already connected to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NJ&projectType=new_install")));
	}

	@Test
	void washingtonPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and local health jurisdiction handling the parcel.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=new_install")));
	}

	@Test
	void massachusettsInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest Title 5 inspection report with the inspection date visible.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Title 5")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=inspection")));
	}

	@Test
	void pennsylvaniaInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The municipality and Sewage Enforcement Officer contact for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=inspection")));
	}

	@Test
	void connecticutInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any site investigation, approval-to-construct, and permit-to-discharge record tied to the system.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=inspection")));
	}

	@Test
	void washingtonInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The as-built drawing and confirmation of the actual system type.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("every three years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=inspection")));
	}

	@Test
	void floridaInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("private-provider or operating-permit records already tied to the system.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=inspection")));
	}

	@Test
	void georgiaInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any soil analysis, county permit, or repair record tied to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("garbage disposal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=inspection")));
	}

	@Test
	void newJerseyInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Jersey Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any service contract or maintenance agreement for advanced treatment components.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NJ&projectType=inspection")));
	}

	@Test
	void oregonInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The online septic-record lookup result and the latest site evaluation for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=inspection")));
	}

	@Test
	void northCarolinaInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The county health department file reference and contact for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=inspection")));
	}

	@Test
	void missouriInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/missouri/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Missouri Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Missouri")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and county or local authority that controls the onsite file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MO&projectType=inspection")));
	}

	@Test
	void washingtonRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built drawing or approved design")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built drawing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Local Health Jurisdictions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=buying_home")));
	}

	@Test
	void connecticutRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The site investigation and any soil-testing record already on file for the lot.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=buying_home")));
	}

	@Test
	void massachusettsRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Annual pumping receipts if the seller is claiming a longer validity window.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=buying_home")));
	}

	@Test
	void pennsylvaniaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The municipality or local agency name plus the Sewage Enforcement Officer contact.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=buying_home")));
	}

	@Test
	void floridaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any private-provider or operating-permit record connected to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=buying_home")));
	}

	@Test
	void georgiaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The soil analysis or county site-review record for the lot.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=buying_home")));
	}

	@Test
	void newJerseyRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("service agreement")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NJ&projectType=buying_home")));
	}

	@Test
	void missouriPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/missouri/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Missouri Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-by-county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local authority that controls onsite sewage permitting")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MO&projectType=new_install")));
	}

	@Test
	void missouriRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Missouri")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and the county or local authority that handled onsite permitting.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MO&projectType=buying_home")));
	}

	@Test
	void northCarolinaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The county health department file reference for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=buying_home")));
	}

	@Test
	void northCarolinaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("construction authorization already exists or needs to be updated")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("improvement permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=new_install")));
	}

	@Test
	void oregonRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online septic-record lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Locating Septic System Records Online")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=buying_home")));
	}

	@Test
	void connecticutBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("potential bedrooms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The legal bedroom count and any potential-bedroom issue already visible in the home.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=buying_home")));
	}

	@Test
	void tankSizeContentPagePointsToDedicatedEstimator() throws Exception {
		mockMvc.perform(get("/septic-tank-size/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Tank Size Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the tank size estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next estimate or quote")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-tank-size-estimator/")));
	}

	@Test
	void pumpingContentPagePointsToDedicatedEstimator() throws Exception {
		mockMvc.perform(get("/septic-pumping-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the pump schedule estimator")))
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
