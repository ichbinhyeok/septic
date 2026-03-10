package com.example.septic;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.FaqBlock;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.service.EstimatorResult;
import com.example.septic.service.EstimatorService;
import com.example.septic.web.EstimateForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"app.storage.root=./build/test-storage",
		"app.site.base-url=https://example.test"
})
@AutoConfigureMockMvc
class SepticApplicationTests {
	private static final Path TEST_STORAGE_ROOT = Path.of("build/test-storage");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EstimatorService estimatorService;

	@BeforeEach
	void resetTestStorage() throws IOException {
		if (Files.notExists(TEST_STORAGE_ROOT)) {
			return;
		}
		try (Stream<Path> paths = Files.walk(TEST_STORAGE_ROOT)) {
			paths.sorted(Comparator.reverseOrder())
					.forEach(path -> {
						try {
							Files.deleteIfExists(path);
						} catch (IOException exception) {
							throw new RuntimeException("Failed to clean test storage at " + path, exception);
						}
					});
		}
	}

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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath")))
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
	void canonicalHostRedirectsWwwHttpRequests() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/")
						.queryParam("state", "GA")
						.with(request -> {
							request.setScheme("http");
							request.setServerName("www.example.test");
							request.setServerPort(80);
							return request;
						}))
				.andExpect(status().is(308))
				.andExpect(header().string("Location", "https://example.test/septic-system-cost-calculator/?state=GA"));
	}

	@Test
	void canonicalHostDoesNotRedirectWhenForwardedProtoIsAlreadyHttps() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/")
						.queryParam("state", "GA")
						.header("X-Forwarded-Proto", "https")
						.with(request -> {
							request.setScheme("http");
							request.setServerName("example.test");
							request.setServerPort(80);
							return request;
						}))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic System Cost Calculator")));
	}

	@Test
	void canonicalHostDoesNotRedirectWhenCloudflareVisitorSchemeIsHttps() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/")
						.queryParam("state", "GA")
						.header("CF-Visitor", "{\"scheme\":\"https\"}")
						.with(request -> {
							request.setScheme("http");
							request.setServerName("example.test");
							request.setServerPort(80);
							return request;
						}))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic System Cost Calculator")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<lastmod>2026-03-10</lastmod>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/privacy-policy/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-tank-size-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-pump-schedule-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/florida/")));
	}

	@Test
	@org.junit.jupiter.api.Disabled("Queue priorities shifted after California, Texas, and New York moved live")
	void stateCoveragePageShowsQueuedStateLinks() throws Exception {
		mockMvc.perform(get("/states/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50-state coverage is expanding in waves")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Next rollout wave")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wave 2 · #1")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Target angle: county permit file retrieval plus environmental health routing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open rollout plan")));
	}

	@Test
	void stateCoveragePageShowsNextQueuedWaveAfterPromotions() throws Exception {
		mockMvc.perform(get("/states/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50-state coverage is expanding in waves")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Next rollout wave")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wave 6 | #1")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Target angle: buyer diligence, repair triggers, and DEM file retrieval before the small-state story looks simpler than it is.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open rollout plan")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official rule and state anchor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Heuristic adjustments applied")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Methodology limits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Likely system class \"Alternative system likely\" adds +45% to the planning cost model.")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Already know the job type?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Full name")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Project type")));
	}

	@Test
	void calculatorAcceptsQueuedStatePrefill() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/")
						.param("state", "CA"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"CA\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">California</option>")));
	}

	@Test
	void calculatorLandingRendersGuidance() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who should use this first")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What widens the range fastest")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Next best pages")))
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
	void queuedStateCalculatorFallsBackToNationalPlanningProfile() throws Exception {
		mockMvc.perform(post("/septic-system-cost-calculator/")
						.param("stateCode", "RI")
						.param("projectType", "replacement")
						.param("bedrooms", "3")
						.param("soilPercStatus", "unknown")
						.param("accessDifficulty", "easy")
						.param("timeline", "researching"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The public Rhode Island guide is still in the research queue")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("planning range only until the Rhode Island source set is published")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State guide research queue")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("National replacement planning anchor")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")))
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
	void californiaStateGuideShowsLocalAgencyPath() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/california/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California septic cost guide and county permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Cost Guide and County Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OWTS Policy")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LAMP")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the county file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California's OWTS Policy explicitly authorizes local agencies")));
	}

	@Test
	void texasStateGuideShowsPermitAndSiteEvaluationContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas septic cost guide and local OSSF permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Cost Guide and Local OSSF Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("30 days")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("licensed site evaluator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OARS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before calling the permitting authority")));
	}

	@Test
	void newYorkStateGuideShowsAppendix75AContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York septic cost guide and Appendix 75-A rules")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Cost Guide and Appendix 75-A Rules")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("110 gallons per bedroom")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with Appendix 75-A context")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("District office or county health department")));
	}

	@Test
	void ohioStateGuideShowsLocalHealthPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/ohio/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio septic cost guide and local health permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Cost Guide and Local Health Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Chapter 3701-29")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health departments are responsible for permitting")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("installation-permit and operation-permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before calling the health district")));
	}

	@Test
	void michiganStateGuideShowsLocalHealthRecordsContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/michigan/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan septic cost guide and local health records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Cost Guide and Local Health Records Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("required service for local health departments")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("contact the local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the local file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void arizonaStateGuideShowsCountyDelegationAndSiteApprovalContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/arizona/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona septic cost guide and site approval path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Cost Guide and Site Approval Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("delegated permitting authority to all 15 counties")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Intent to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Uniform Site Investigation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before site approval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void coloradoStateGuideShowsLocalHealthAndSiteEvaluationContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/colorado/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado septic cost guide and local OWTS permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Septic Cost Guide and Local OWTS Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permits are required before installing, altering, or repairing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("2,000 gallons per day or less")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Site and Soil Evaluation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before calling the local public health agency")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void virginiaStateGuideShowsInspectionAndOperationPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia septic cost guide and inspection obligations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Cost Guide and Inspection Obligations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("construction permit from the health department or VDH")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("conventional system generally should be pumped every 3 to 5 years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("licensed operator visits every 3, 6, or 12 months")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the local health-district call")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void tennesseeStateGuideShowsPermitFileAndRepairContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/tennessee/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee septic cost guide and permit file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Cost Guide and Permit File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit should always be obtained before starting dirt work")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair permit is required before work begins on a failing septic system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Inspection Letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the permit-file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void southCarolinaStateGuideShowsPermitCopyAndOfficeRoutingContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/south-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina septic cost guide and permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Cost Guide and Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site approvals and permits for all septic systems")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit must be issued before the county can issue a building permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("D-1740")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the permit copy pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void alabamaStateGuideShowsCountyHealthPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/alabama/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama septic cost guide and county permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Cost Guide and County Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health departments")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit to Install")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Approval for Use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before calling the county health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void illinoisStateGuideShowsLocalHealthFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/illinois/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois septic cost guide and local health file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Cost Guide and Local Health File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("about 90 local health departments review construction plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("evaluation form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the local file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void marylandStateGuideShowsPropertyTransferRiskContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/maryland/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland septic cost guide and property transfer risk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Cost Guide and Property Transfer Risk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("proper PTI includes a file search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Public Information Act request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the property-transfer file search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void wisconsinStateGuideShowsPowtsInspectionContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/wisconsin/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin septic cost guide and POWTS inspection path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Septic Cost Guide and POWTS Inspection Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("counties have primary responsibility to inspect POWTS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("visually inspected at least once every three years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with county maintenance tracking in mind")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void louisianaStateGuideShowsParishSiteRiskContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/louisiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana septic cost guide and parish site-risk path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Septic Cost Guide and Parish Site-Risk Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("community sewer must be used when available")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("homeowner must be the applicant")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the parish health unit call")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void indianaStateGuideShowsCountyPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/indiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana septic cost guide and county permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Septic Cost Guide and County Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary sewer is available within a reasonable distance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("150 gallons per day per bedroom")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the county permit call")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void oklahomaStateGuideShowsSoilTestContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/oklahoma/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma septic cost guide and soil-test path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Septic Cost Guide and Soil-Test Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("first step is a soil test")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("21 local offices")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the soil-test request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void kentuckyStateGuideShowsLocalFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/kentucky/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky septic cost guide and local file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky Septic Cost Guide and Local File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("administered through local health departments")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("onsite evaluations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the local health file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void minnesotaStateGuideShowsBuyerRiskContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/minnesota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota septic cost guide and property transfer risk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Septic Cost Guide and Property Transfer Risk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("some local governments require compliance inspections prior to property transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("written seller disclosure")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the disclosure check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void arkansasStateGuideShowsCountyPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/arkansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas septic cost guide and county permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Septic Cost Guide and County Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("call the county health unit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit copies")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the health-unit call")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void mississippiStateGuideShowsPublicRecordsContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/mississippi/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi septic cost guide and public records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi Septic Cost Guide and Public Records Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operates entirely in public records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit or Recommendation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the file lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void iowaStateGuideShowsCountyRecordsContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/iowa/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iowa Septic Cost Guide and County Records Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county sanitarian")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the county file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void kansasStateGuideShowsSoilProfileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/kansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Septic Cost Guide and Soil-Profile Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local sanitary codes vary from county to county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil profile is required on all lots")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the soil-profile check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void nebraskaStateGuideShowsPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/nebraska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Septic Cost Guide and Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registered onsite wastewater treatment systems")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registered systems from 2004 forward")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the permit filing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void newMexicoStateGuideShowsBuyerFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/new-mexico/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Septic Cost Guide and Buyer File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("buying a home connected to a liquid waste system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit search request form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the buyer file check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void utahStateGuideShowsLocalHealthPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/utah/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Cost Guide and Local Health Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health departments have jurisdiction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("13 local health departments")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the health-district handoff")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void westVirginiaStateGuideShowsLocalFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/west-virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Cost Guide and Local File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regulatory interpretation and technical assistance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the local file check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public-records request")));
	}

	@Test
	void southDakotaStateGuideShowsPermitCertificateContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/south-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Septic Cost Guide and Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Homeowner Plumbing Installation Certificate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("underground, rough-in, and final inspections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the permit certificate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void idahoStateGuideShowsSiteApprovalContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/idaho/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Idaho Septic Cost Guide and Site Approval Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public health districts administer septic rules")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation should be performed before buying property")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void nevadaStateGuideShowsBuyerFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/nevada/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Cost Guide and Buyer File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Southern Nevada Health District")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("occupancy requires inspection and as-built plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the buyer file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void delawareStateGuideShowsPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/delaware/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware Septic Cost Guide and Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation reports")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection reports")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the permit-file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void alaskaStateGuideShowsBuyerFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/alaska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Septic Cost Guide and Buyer File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Municipality of Anchorage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("$25")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the buyer file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void hawaiiStateGuideShowsCesspoolUpgradeContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/hawaii/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Septic Cost Guide and Cesspool Upgrade Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("2050")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approval-to-use letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the cesspool-upgrade path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void maineStateGuideShowsHhe200FileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/maine/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Septic Cost Guide and HHE-200 File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("HHE-200")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Local Plumbing Inspector")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the buyer file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void newHampshireStateGuideShowsApprovalStatusContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/new-hampshire/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hampshire Septic Cost Guide and Approval Status Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operationally approved septic system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OneStop")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the approval-status check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
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
	void queuedStateGuideRendersResearchLanding() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/rhode-island/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island septic guide is in the research queue.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use the Rhode Island estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=RI")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wave 6 priority #1")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Why Rhode Island moves early")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First source pack to build")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("buyer diligence, repair triggers, and DEM file retrieval before the small-state story looks simpler than it is")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What unlocks a live Rhode Island guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia septic guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<meta name=\"robots\" content=\"noindex,follow\">")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<link rel=\"canonical\" href=\"https://example.test/septic-system-cost-calculator/rhode-island/\">")));
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
	void tennesseeReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/tennessee/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Tennessee")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("construction permit, any repair permit, and any inspection letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("contract county or TDEC regional contact")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TN&projectType=replacement")));
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
	void arizonaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/arizona/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Arizona")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Uniform Site Investigation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Intent to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AZ&projectType=perc_test")));
	}

	@Test
	void coloradoPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/colorado/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Colorado")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Site and Soil Evaluation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local public health agency")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CO&projectType=perc_test")));
	}

	@Test
	void louisianaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/louisiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Louisiana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("community sewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parish health unit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=LA&projectType=perc_test")));
	}

	@Test
	void oklahomaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/oklahoma/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oklahoma")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil profile")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local DEQ office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OK&projectType=perc_test")));
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
	void marylandBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/maryland/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Maryland")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Maryland")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("PTI")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Public Information Act request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MD&projectType=buying_home")));
	}

	@Test
	void minnesotaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/minnesota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Minnesota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Minnesota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("seller disclosure")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("prior inspection report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MN&projectType=buying_home")));
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
	void newYorkBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in New York")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New York")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("specific waiver")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NY&projectType=buying_home")));
	}

	@Test
	void nevadaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/nevada/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Nevada")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Nevada")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("occupancy signoff")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NV&projectType=buying_home")));
	}

	@Test
	void delawarePermitPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/delaware/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Delaware")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Regulations 7101 and 7102")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("building permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=DE&projectType=new_install")));
	}

	@Test
	void alaskaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/alaska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Alaska")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Alaska")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved-system record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Municipality of Anchorage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AK&projectType=buying_home")));
	}

	@Test
	void hawaiiPermitPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/hawaii/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Hawaii")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("cesspool")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approval-to-use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=HI&projectType=new_install")));
	}

	@Test
	void maineBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/maine/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Maine")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Maine")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("HHE-200")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Local Plumbing Inspector")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ME&projectType=buying_home")));
	}

	@Test
	void newHampshirePermitPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/new-hampshire/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hampshire Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New Hampshire")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operationally approved")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OneStop")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NH&projectType=new_install")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 4 source-backed state-specific pages and the source policy.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Intent Map Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State Source Review Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last reviewed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-03-09\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"editor\":{\"@type\":\"Organization\",\"name\":\"State Source Review Desk\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this page is sourced")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State-specific pages carry the official sources behind this national overview.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 3 official sources tied to the Connecticut workflow.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Records Checklist")))
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
	void virginiaInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation and maintenance manual")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("construction permit and any operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VA&projectType=inspection")));
	}

	@Test
	void wisconsinInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/wisconsin/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Wisconsin")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance-tracking")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("three years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WI&projectType=inspection")));
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
	void californiaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county environmental health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LAMP-driven local program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CA&projectType=buying_home")));
	}

	@Test
	void michiganRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Michigan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failed sewage system evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("system is located")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MI&projectType=buying_home")));
	}

	@Test
	void illinoisRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/illinois/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("evaluation form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IL&projectType=buying_home")));
	}

	@Test
	void kentuckyRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/kentucky/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Kentucky")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=KY&projectType=buying_home")));
	}

	@Test
	void mississippiRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/mississippi/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Mississippi")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit or Recommendation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MS&projectType=buying_home")));
	}

	@Test
	void westVirginiaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/west-virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in West Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public-records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WV&projectType=buying_home")));
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
	void texasPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OARS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("licensed site evaluator or professional engineer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TX&projectType=new_install")));
	}

	@Test
	void ohioPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/ohio/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OH&projectType=new_install")));
	}

	@Test
	void southCarolinaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/south-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit copy")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("D-1740")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SCDES")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=SC&projectType=new_install")));
	}

	@Test
	void alabamaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/alabama/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Alabama")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit to Install")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Approval for Use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AL&projectType=new_install")));
	}

	@Test
	void indianaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/indiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Indiana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary sewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IN&projectType=new_install")));
	}

	@Test
	void arkansasPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/arkansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Arkansas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health unit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Onsite Environmental Specialist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AR&projectType=new_install")));
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
	void iowaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/iowa/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iowa Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Iowa")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county sanitarian")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IA&projectType=buying_home")));
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
	void kansasPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/kansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Kansas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil profile")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local sanitary code")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=KS&projectType=perc_test")));
	}

	@Test
	void idahoPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/idaho/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Idaho Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Idaho")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public health district")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation should be performed before buying property")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ID&projectType=perc_test")));
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
	void nebraskaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/nebraska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Nebraska")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registered onsite wastewater treatment system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registered systems from 2004 forward")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NE&projectType=new_install")));
	}

	@Test
	void utahPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/utah/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Utah")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil logs and percolation test results")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=UT&projectType=new_install")));
	}

	@Test
	void southDakotaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/south-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in South Dakota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Homeowner Plumbing Installation Certificate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("underground, rough-in, and final inspections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=SD&projectType=new_install")));
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
	void newMexicoBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/new-mexico/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in New Mexico")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit search request form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("homeowner notice")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NM&projectType=buying_home")));
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
