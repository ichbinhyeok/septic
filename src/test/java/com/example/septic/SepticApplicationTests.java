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
				List.of("Longform paragraph one.", "Longform paragraph two."),
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
				unpublished.deepDiveParagraphs(),
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/app.css?v=")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/app.js?v=")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("rel=\"preload\" href=\"https://fonts.googleapis.com/css2?family=Manrope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-site-nav-toggle")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-nav-menu")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/editorial-standards/")))
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
	void canonicalHostRedirectsNonSlashHtmlPathToTrailingSlash() throws Exception {
		mockMvc.perform(get("/about")
						.with(request -> {
							request.setScheme("https");
							request.setServerName("example.test");
							request.setServerPort(443);
							return request;
						}))
				.andExpect(status().is(308))
				.andExpect(header().string("Location", "https://example.test/about/"));
	}

	@Test
	void canonicalHostRedirectsNonSlashStatePathToTrailingSlash() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/georgia")
						.with(request -> {
							request.setScheme("https");
							request.setServerName("example.test");
							request.setServerPort(443);
							return request;
						}))
				.andExpect(status().is(308))
				.andExpect(header().string("Location", "https://example.test/septic-replacement-cost/georgia/"));
	}

	@Test
	void canonicalHostCombinesOriginAndTrailingSlashRedirect() throws Exception {
		mockMvc.perform(get("/states")
						.with(request -> {
							request.setScheme("http");
							request.setServerName("www.example.test");
							request.setServerPort(80);
							return request;
						}))
				.andExpect(status().is(308))
				.andExpect(header().string("Location", "https://example.test/states/"));
	}

	@Test
	void canonicalHostDoesNotRedirectStaticAssetsForTrailingSlashNormalization() throws Exception {
		mockMvc.perform(get("/app.css")
						.with(request -> {
							request.setScheme("https");
							request.setServerName("example.test");
							request.setServerPort(443);
							return request;
						}))
				.andExpect(status().isOk());
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<lastmod>2026-03-11</lastmod>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/privacy-policy/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-tank-size-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-pump-schedule-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/new-jersey/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/north-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/new-jersey/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/north-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/new-jersey/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/north-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/new-jersey/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/north-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/wet-yard-over-septic-drain-field/colorado/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/vermont/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/montana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/north-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/wyoming/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/ohio/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/michigan/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/arizona/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/illinois/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/maryland/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/wisconsin/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/louisiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/oklahoma/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/kentucky/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/minnesota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/iowa/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/kansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/nebraska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/new-mexico/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/utah/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/west-virginia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/north-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/vermont/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/montana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/north-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/wyoming/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/missouri/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/montana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/montana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/north-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/vermont/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/vermont/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/wyoming/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/wyoming/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/alaska/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/arkansas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/delaware/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/hawaii/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/idaho/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/maine/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/mississippi/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/montana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/montana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/montana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/north-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/north-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/north-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/new-hampshire/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/nevada/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/rhode-island/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/south-dakota/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/vermont/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/vermont/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/vermont/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/wyoming/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/wyoming/")));
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
	void stateCoveragePageShowsCompletedGuideCoverage() throws Exception {
		mockMvc.perform(get("/states/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("All 50 state guides are live, and the next gains come from deeper intent pages.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Queue closed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Queue states")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Next rollout wave"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Open rollout plan"))));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Not engineering design software")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("aria-label=\"Breadcrumb\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">About SepticPath<")));
	}

	@Test
	void editorialStandardsPageRenders() throws Exception {
		mockMvc.perform(get("/editorial-standards/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Editorial standards")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What we prefer as evidence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust the source trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("aria-label=\"Breadcrumb\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Editorial Standards<")));
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
	void drainfieldEstimatorPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-estimator/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Drain Field Replacement Estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No clear replacement area or reserve area identified")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia field-replacement snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Why does replacement area matter so much to drain field cost?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQPage")));
	}

	@Test
	void drainfieldEstimatorSupportsStatePrefill() throws Exception {
		mockMvc.perform(get("/drain-field-estimator/").param("state", "OR"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon field-replacement snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Oregon guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/oregon/")));
	}

	@Test
	void drainfieldEstimatorReturnsResult() throws Exception {
		mockMvc.perform(post("/drain-field-estimator/")
						.param("stateCode", "GA")
						.param("bedrooms", "4")
						.param("soilPercStatus", "failed")
						.param("accessDifficulty", "hard")
						.param("timeline", "this_month")
						.param("wetGroundOrSurfacing", "true")
						.param("noClearReplacementArea", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia drain field planning range")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alternative field layout or site-specific redesign likely")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reserve-area or layout risk is the main blocker")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the full cost estimator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Drain Field Replacement Cost")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia septic permit cost, tank size, and county records guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Permit Cost, Tank Size, and Records Guide")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use this Georgia guide before you click into one intent page")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("aria-label=\"Breadcrumb\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Septic System Cost Calculator<")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_primary_calculator\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_inline_intent_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_inline_source_link\"")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Records Checklist")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Perc Test Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Michigan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Perc Test Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Arizona")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jump between sections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Arizona Septic Cost Guide and Site Approval Path | SepticPath</title>")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Colorado")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Perc Test Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Tennessee")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Records Checklist")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the permit copy pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void alabamaStateGuideShowsCountyHealthPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/alabama/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama septic permit cost, records, and county health guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Permit Cost, Records, and County Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health departments")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit to Install")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Approval for Use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Alabama")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Perc Test Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Perc Test Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Wisconsin")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Perc Test Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Louisiana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Indiana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Oklahoma")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Kentucky")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Perc Test Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Arkansas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Mississippi")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi Septic Permit Process")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iowa Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Iowa")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iowa Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iowa Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iowa Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Kansas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Nebraska")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Utah")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the health-district handoff")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void westVirginiaStateGuideShowsLocalFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/west-virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Cost Guide and Local File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regulatory interpretation and technical assistance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in West Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in South Dakota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Septic Records Checklist")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Idaho Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Idaho Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Idaho Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Idaho")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Idaho Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Permit Process")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Delaware")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Septic Permit Process")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Hawaii")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Septic Inspection Cost")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Septic Permit Process")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in New Hampshire")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hampshire Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hampshire Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hampshire Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hampshire Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the approval-status check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void rhodeIslandStateGuideShowsDemFileContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/rhode-island/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Cost Guide and DEM File Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("1968 forward")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("historic permit searches")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the DEM file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void vermontStateGuideShowsRegionalOfficePermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/vermont/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Septic Cost Guide and WW Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("five regional offices")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("check with the Town")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Vermont")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the regional-office handoff")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void montanaStateGuideShowsLotReviewContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/montana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Septic Cost Guide and Site-Risk Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("COSA")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("drainfield permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Montana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the lot-review check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void northDakotaStateGuideShowsLocalPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/north-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Dakota Septic Cost Guide and Local Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local public health units issue permits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("investigate complaints")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Dakota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Dakota Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Dakota Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Dakota Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in North Dakota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the local permit call")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void wyomingStateGuideShowsSiteRiskContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/wyoming/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Cost Guide and Site-Risk Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("all new systems require a permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("engineer-designed systems")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Wyoming")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the county site check")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The first practical check is usually the office, file path, or reviewer identified in this state workflow:")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate with the disposal rule in mind")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("quote-request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Get matched with local septic pros")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official-source context")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_primary_calculator\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_featured_link\"")))
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
	void californiaReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/california/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tier 1")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LAMP-driven")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CA&projectType=replacement")));
	}

	@Test
	void arizonaReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/arizona/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Arizona")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Uniform Site Investigation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Intent to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AZ&projectType=replacement")));
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
	void texasReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TX&projectType=replacement")));
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
	void virginiaReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation and maintenance manual")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("construction permit and operation permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VA&projectType=replacement")));
	}

	@Test
	void newYorkReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New York")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("under-1,000-gpd residential baseline")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NY&projectType=replacement")));
	}

	@Test
	void ohioReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/ohio/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("off-lot discharge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OH&projectType=replacement")));
	}

	@Test
	void illinoisReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/illinois/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("evaluation-form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IL&projectType=replacement")));
	}

	@Test
	void michiganReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/michigan/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Michigan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failed-system evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("system is located")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MI&projectType=replacement")));
	}

	@Test
	void marylandReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/maryland/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Maryland")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("file search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local approving authority permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MD&projectType=replacement")));
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
	void georgiaFailedPercPageRenders() throws Exception {
		mockMvc.perform(get("/failed-perc-test-septic/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The current bedroom count, disposal status, and any added kitchen or load change that affects how much field area is needed.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=perc_test")));
	}

	@Test
	void georgiaReplacementAreaPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-area/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Replacement Area Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The current bedroom count, disposal status, and any load change that affects required field area.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=drainfield_replacement")));
	}

	@Test
	void georgiaWetYardPageRenders() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Wet Yard Over Septic Drain Field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Where the wet area shows up, whether odor or surfacing is present, and how long the symptom has been recurring.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=drainfield_replacement")));
	}

	@Test
	void virginiaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation and maintenance manual")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("construction permit and operation permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VA&projectType=perc_test")));
	}

	@Test
	void wisconsinPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/wisconsin/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Wisconsin")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance-tracking history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WI&projectType=perc_test")));
	}

	@Test
	void marylandPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/maryland/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Maryland")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("file search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local approving authority permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MD&projectType=perc_test")));
	}

	@Test
	void minnesotaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/minnesota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Minnesota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("prior compliance-inspection report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local permit and inspection path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MN&projectType=perc_test")));
	}

	@Test
	void ohioPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/ohio/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("off-lot discharge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OH&projectType=perc_test")));
	}

	@Test
	void illinoisPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/illinois/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("evaluation-form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IL&projectType=perc_test")));
	}

	@Test
	void michiganPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/michigan/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Michigan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failed-system evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("system is located")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MI&projectType=perc_test")));
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
	void connecticutFailedPercPageRenders() throws Exception {
		mockMvc.perform(get("/failed-perc-test-septic/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and local health department or approved-agent contact for the file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=perc_test")));
	}

	@Test
	void connecticutReplacementAreaPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-area/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Septic Replacement Area Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and local health department or approved-agent contact for the file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=drainfield_replacement")));
	}

	@Test
	void connecticutWetYardPageRenders() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Wet Yard Over Septic Drain Field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The property address and local health department or approved-agent contact for the file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=drainfield_replacement")));
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
	void pennsylvaniaFailedPercPageRenders() throws Exception {
		mockMvc.perform(get("/failed-perc-test-septic/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The municipality, county, and any Sewage Enforcement Officer contact already tied to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=perc_test")));
	}

	@Test
	void pennsylvaniaReplacementAreaPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-area/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Septic Replacement Area Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The municipality, county, and Sewage Enforcement Officer contact already tied to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=drainfield_replacement")));
	}

	@Test
	void pennsylvaniaWetYardPageRenders() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Wet Yard Over Septic Drain Field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The municipality, county, and any Sewage Enforcement Officer contact already tied to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=drainfield_replacement")));
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
	void massachusettsFailedPercPageRenders() throws Exception {
		mockMvc.perform(get("/failed-perc-test-septic/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest Title 5 inspection report and inspection date.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=perc_test")));
	}

	@Test
	void massachusettsReplacementAreaPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-area/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Replacement Area Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest Title 5 inspection report and any note describing the field issue.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=drainfield_replacement")));
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
	void floridaFailedPercPageRenders() throws Exception {
		mockMvc.perform(get("/failed-perc-test-septic/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jurisdiction confirmation showing whether DEP or the county health department controls the parcel.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=perc_test")));
	}

	@Test
	void floridaReplacementAreaPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-area/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Replacement Area Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jurisdiction confirmation showing whether DEP or the county health department controls the parcel.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=drainfield_replacement")));
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
	void californiaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/california/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built drawing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LAMP-driven local program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CA&projectType=buying_home")));
	}

	@Test
	void californiaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/california/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tier 1")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LAMP-driven local program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CA&projectType=perc_test")));
	}

	@Test
	void texasBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OARS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("aerobic-system maintenance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TX&projectType=buying_home")));
	}

	@Test
	void texasInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("three- to five-year pumping guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("aerobic or advanced system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TX&projectType=inspection")));
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
	void newYorkPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New York")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("under-1,000-gpd residential baseline")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NY&projectType=perc_test")));
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
	void rhodeIslandBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/rhode-island/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Rhode Island")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Rhode Island")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("1968 forward")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("suitability determination")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=RI&projectType=buying_home")));
	}

	@Test
	void vermontPermitPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/vermont/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Vermont")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("five regional offices")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("check with the Town")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VT&projectType=new_install")));
	}

	@Test
	void alaskaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/alaska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved-system record and archive-scanning note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("remote-site conditions and archive-scanning delay")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AK&projectType=buying_home")));
	}

	@Test
	void alabamaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/alabama/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Alabama")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Approval for Use, Permit to Install, and soil-test history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-file and soil-test friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AL&projectType=buying_home")));
	}

	@Test
	void arkansasRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arkansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit copy and county health-unit file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-file and soil-suitability friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AR&projectType=buying_home")));
	}

	@Test
	void delawareBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/delaware/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Delaware")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation report and inspection report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-handoff and suitability-review friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=DE&projectType=buying_home")));
	}

	@Test
	void hawaiiRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/hawaii/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approval-to-use letter and local branch record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("cesspool-upgrade and TMK-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=HI&projectType=buying_home")));
	}

	@Test
	void idahoPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/idaho/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Idaho Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("installation permit and district site-evaluation file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("district-file and site-evaluation friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ID&projectType=new_install")));
	}

	@Test
	void maineInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/maine/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Local Plumbing Inspector trail and HHE-200 file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("town-office file gaps and online-search limits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ME&projectType=inspection")));
	}

	@Test
	void mississippiBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/mississippi/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Mississippi")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit or Recommendation and county file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public-records and county-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MS&projectType=buying_home")));
	}

	@Test
	void montanaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/montana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("drainfield permit and local-health file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lot-review and local-delegation friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MT&projectType=new_install")));
	}

	@Test
	void northDakotaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Dakota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit and inspection file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local-permit and complaint-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ND&projectType=buying_home")));
	}

	@Test
	void newHampshireBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/new-hampshire/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in New Hampshire")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operational-approval status and local failure-verification note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operational-approval and archive-gap friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NH&projectType=buying_home")));
	}

	@Test
	void nevadaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/nevada/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection note and occupancy signoff")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("authority-split and as-built-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NV&projectType=inspection")));
	}

	@Test
	void rhodeIslandRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/rhode-island/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("active and historic permit search plus the underlying file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-history and suitability-review friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=RI&projectType=buying_home")));
	}

	@Test
	void southCarolinaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/south-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit copy and final-inspection history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-copy and county-office friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=SC&projectType=buying_home")));
	}

	@Test
	void southDakotaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/south-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection certificate path and local-rule note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection-certificate and local-rule friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=SD&projectType=inspection")));
	}

	@Test
	void tennesseeInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/tennessee/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection letter and permit file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regional-contact and repair-permit friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TN&projectType=inspection")));
	}

	@Test
	void vermontRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/vermont/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-search result and state-issued permit file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regional-office and town-review friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VT&projectType=buying_home")));
	}

	@Test
	void wyomingPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/wyoming/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permit, site plan, and DEQ-delegation file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("delegated-county and engineer-design friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WY&projectType=new_install")));
	}

	@Test
	void alaskaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/alaska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alaska Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved-system record and local DEC file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("remote-site conditions and archive-scanning delay")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AK&projectType=new_install")));
	}

	@Test
	void alabamaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/alabama/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Approval for Use and county inspection file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-file and soil-test friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AL&projectType=inspection")));
	}

	@Test
	void arkansasPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/arkansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arkansas Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("onsite specialist site-suitability note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-file and soil-suitability friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AR&projectType=perc_test")));
	}

	@Test
	void delawareInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/delaware/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection report and county-handoff note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-handoff and suitability-review friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=DE&projectType=inspection")));
	}

	@Test
	void hawaiiInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/hawaii/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hawaii Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approval-to-use letter and cesspool-upgrade note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("cesspool-upgrade and TMK-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=HI&projectType=inspection")));
	}

	@Test
	void idahoBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/idaho/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Idaho")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation and district permit file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("district-file and site-evaluation friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ID&projectType=buying_home")));
	}

	@Test
	void mainePermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/maine/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maine Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("HHE-200 permit path and town-office file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("town-office file gaps and online-search limits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ME&projectType=new_install")));
	}

	@Test
	void mississippiPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/mississippi/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mississippi Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permit file and site-evaluation note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public-records and county-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MS&projectType=new_install")));
	}

	@Test
	void montanaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/montana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("subdivision file and drainfield-permit note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lot-review and local-delegation friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MT&projectType=buying_home")));
	}

	@Test
	void northDakotaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/north-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in North Dakota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit and inspection file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local-permit and complaint-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ND&projectType=buying_home")));
	}

	@Test
	void newHampshireInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/new-hampshire/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hampshire Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local verification file and failure note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operational-approval and archive-gap friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NH&projectType=inspection")));
	}

	@Test
	void nevadaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/nevada/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file and authority-split note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("authority-split and as-built-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NV&projectType=new_install")));
	}

	@Test
	void rhodeIslandPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/rhode-island/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DEM permit file and suitability-determination note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-history and suitability-review friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=RI&projectType=new_install")));
	}

	@Test
	void southCarolinaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/south-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("final-inspection history and permit-copy trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-copy and county-office friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=SC&projectType=inspection")));
	}

	@Test
	void southDakotaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/south-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Dakota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Homeowner Plumbing Installation Certificate and inspection notes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection-certificate and local-rule friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=SD&projectType=buying_home")));
	}

	@Test
	void tennesseeRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/tennessee/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file and inspection letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regional-contact and repair-permit friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TN&projectType=buying_home")));
	}

	@Test
	void vermontInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/vermont/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Vermont Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("town-review note and permit-search history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regional-office and town-review friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VT&projectType=inspection")));
	}

	@Test
	void wyomingInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/wyoming/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county inspection file and engineer-design note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("delegated-county and engineer-design friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WY&projectType=inspection")));
	}

	@Test
	void montanaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/montana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montana Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Montana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("COSA")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("drainfield permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MT&projectType=perc_test")));
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
	void pennsylvaniaDrainFieldPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pennsylvania Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The municipality, county, and Sewage Enforcement Officer contact already tied to the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=PA&projectType=drainfield_replacement")));
	}

	@Test
	void connecticutDrainFieldPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/connecticut/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Connecticut Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Connecticut")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The current and intended bedroom count or use of the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CT&projectType=drainfield_replacement")));
	}

	@Test
	void massachusettsDrainFieldPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest Title 5 inspection report and any note already describing the field issue.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=drainfield_replacement")));
	}

	@Test
	void floridaDrainFieldPageRenders() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jurisdiction confirmation showing whether DEP or the county health department controls the parcel.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=drainfield_replacement")));
	}

	@Test
	void oregonFailedPercPageRenders() throws Exception {
		mockMvc.perform(get("/failed-perc-test-septic/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any ADU plan, change in use, or added-flow detail that could reshape the approval path.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=perc_test")));
	}

	@Test
	void oregonReplacementAreaPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-area/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Replacement Area Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any ADU, use-change, or added-flow detail that changes what the replacement area has to support.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=drainfield_replacement")));
	}

	@Test
	void oregonWetYardPageRenders() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Wet Yard Over Septic Drain Field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any ADU, use-change, or added-flow detail that could reshape the replacement-area story.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=drainfield_replacement")));
	}

	@Test
	void massachusettsWetYardPageRenders() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/massachusetts/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Wet Yard Over Septic Drain Field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Massachusetts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The latest Title 5 inspection report and any pumping receipts tied to the current validity story.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MA&projectType=drainfield_replacement")));
	}

	@Test
	void floridaWetYardPageRenders() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/florida/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Wet Yard Over Septic Drain Field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Florida")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jurisdiction confirmation showing whether DEP or the county health department controls the parcel.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=FL&projectType=drainfield_replacement")));
	}

	@Test
	void newJerseyDrainfieldClusterPagesRender() throws Exception {
		assertStateMoneyPageRenders(
				"/failed-perc-test-septic/new-jersey/",
				"New Jersey Failed Perc Test for Septic",
				"Any service contract, management notice, or recurring certification document tied to the property.",
				"/septic-system-cost-calculator/?state=NJ&projectType=perc_test"
		);
		assertStateMoneyPageRenders(
				"/septic-replacement-area/new-jersey/",
				"New Jersey Septic Replacement Area Guide",
				"Any service contract, management notice, or recurring certification document tied to the property.",
				"/septic-system-cost-calculator/?state=NJ&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/wet-yard-over-septic-drain-field/new-jersey/",
				"New Jersey Wet Yard Over Septic Drain Field",
				"A note on whether the property is in the Pinelands or already carries advanced-treatment obligations.",
				"/septic-system-cost-calculator/?state=NJ&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/drain-field-replacement-cost/new-jersey/",
				"New Jersey Drain Field Replacement Cost",
				"Any service contract, management notice, or recurring certification document tied to the property.",
				"/septic-system-cost-calculator/?state=NJ&projectType=drainfield_replacement"
		);
	}

	@Test
	void washingtonDrainfieldClusterPagesRender() throws Exception {
		assertStateMoneyPageRenders(
				"/failed-perc-test-septic/washington/",
				"Washington Failed Perc Test for Septic",
				"The as-built drawing and any prior design or permit file tied to the system.",
				"/septic-system-cost-calculator/?state=WA&projectType=perc_test"
		);
		assertStateMoneyPageRenders(
				"/septic-replacement-area/washington/",
				"Washington Septic Replacement Area Guide",
				"Any contractor note already suggesting the field path or actual system type may not match the current assumption.",
				"/septic-system-cost-calculator/?state=WA&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/wet-yard-over-septic-drain-field/washington/",
				"Washington Wet Yard Over Septic Drain Field",
				"The as-built drawing and confirmation of the actual system type.",
				"/septic-system-cost-calculator/?state=WA&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/drain-field-replacement-cost/washington/",
				"Washington Drain Field Replacement Cost",
				"The as-built drawing and confirmation of whether the system is gravity or advanced.",
				"/septic-system-cost-calculator/?state=WA&projectType=drainfield_replacement"
		);
	}

	@Test
	void northCarolinaDrainfieldClusterPagesRender() throws Exception {
		assertStateMoneyPageRenders(
				"/failed-perc-test-septic/north-carolina/",
				"North Carolina Failed Perc Test for Septic",
				"The county health department contact and file reference for the property.",
				"/septic-system-cost-calculator/?state=NC&projectType=perc_test"
		);
		assertStateMoneyPageRenders(
				"/septic-replacement-area/north-carolina/",
				"North Carolina Septic Replacement Area Guide",
				"Any contractor note already suggesting the current field path may not match the old approval story.",
				"/septic-system-cost-calculator/?state=NC&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/wet-yard-over-septic-drain-field/north-carolina/",
				"North Carolina Wet Yard Over Septic Drain Field",
				"The county health department contact and file reference for the property.",
				"/septic-system-cost-calculator/?state=NC&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/drain-field-replacement-cost/north-carolina/",
				"North Carolina Drain Field Replacement Cost",
				"The county health department file reference and contact for the property.",
				"/septic-system-cost-calculator/?state=NC&projectType=drainfield_replacement"
		);
	}

	@Test
	void coloradoDrainfieldClusterPagesRender() throws Exception {
		assertStateMoneyPageRenders(
				"/failed-perc-test-septic/colorado/",
				"Colorado Failed Perc Test for Septic",
				"The local public health agency contact with jurisdiction over the property.",
				"/septic-system-cost-calculator/?state=CO&projectType=perc_test"
		);
		assertStateMoneyPageRenders(
				"/septic-replacement-area/colorado/",
				"Colorado Septic Replacement Area Guide",
				"The Site and Soil Evaluation Report, permit file, and any transfer-of-title or inspection note tied to the parcel.",
				"/septic-system-cost-calculator/?state=CO&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/wet-yard-over-septic-drain-field/colorado/",
				"Colorado Wet Yard Over Septic Drain Field",
				"The local public health agency contact with jurisdiction over the property.",
				"/septic-system-cost-calculator/?state=CO&projectType=drainfield_replacement"
		);
		assertStateMoneyPageRenders(
				"/drain-field-replacement-cost/colorado/",
				"Colorado Drain Field Replacement Cost",
				"The Site and Soil Evaluation Report, permit file, and any transfer-of-title or field note tied to the parcel.",
				"/septic-system-cost-calculator/?state=CO&projectType=drainfield_replacement"
		);
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("aria-label=\"Breadcrumb\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use this page before you ask for quotes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use a live state page before you trust the national range")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What this page is really helping you decide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Homeowners usually get anchored to one replacement number too early.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A strong replacement page should help you name what is actually widening the spread")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Representative state examples behind this national page")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What this national page can answer before you touch a quote")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("When this page stops being enough")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_inline_internal_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_inline_state_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_inline_evidence_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_state_example_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_state_example_source\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The national page frames the question; the state page carries the file, office, and risk context that changes the answer.")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Permit Process by State | Permits, records, and next steps | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use this page before you ask for quotes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jump between sections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a permit-path estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start short quote form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=new_install&amp;quoteMode=true#quote-request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=new_install")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_state_specific\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most homeowners get stuck because permit sounds like one step")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Permit Process")));
	}

	@Test
	void percTestCostContentPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Perc Test Cost and Percolation Test Price | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("percolation result will keep the project conventional")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A cheap perc or percolation test can still be the event")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How much does a perc test cost?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Is a perc test the same as a percolation test or a perk test?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/"))));
	}

	@Test
	void recordsChecklistContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Records Checklist | Permit records, as-builts, and file lookup | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What this page is really helping you decide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Records change the estimate because they change what you can safely assume.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("one missing as-built or permit can matter more than several contractor opinions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Representative state examples behind this national page")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What this national page can answer before you touch a quote")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("When this page stops being enough")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What usually kills the low end")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jump between sections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a records-aware estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start short quote form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=buying_home&amp;quoteMode=true#quote-request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=buying_home")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 6 source-backed state-specific pages and the source policy.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Intent Map Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State Source Review Desk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last reviewed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-03-11\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"editor\":{\"@type\":\"Organization\",\"name\":\"State Source Review Desk\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this page is sourced")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State-specific pages carry the official sources behind this national overview.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 3 official sources tied to the Connecticut workflow.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_state_specific\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Massachusetts Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oregon Septic Records Checklist")));
	}

	@Test
	void buyerContentPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic buyer risk is rarely about one inspection fee.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The real buyer question is not just whether the house has septic.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=buying_home")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/georgia/")));
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
	void arizonaInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/arizona/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Arizona")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Uniform Site Investigation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AZ&projectType=inspection")));
	}

	@Test
	void coloradoInspectionPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/colorado/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Colorado")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Site and Soil Evaluation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer-of-title inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CO&projectType=inspection")));
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
	void marylandInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/maryland/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Maryland")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("PTI-backed transfer report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("file search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MD&projectType=inspection")));
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
	void pennsylvaniaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/pennsylvania/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Pennsylvania")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sewage Enforcement Officer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("municipality or local agency")))
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
	void coloradoRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Colorado")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Site and Soil Evaluation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer-of-title inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CO&projectType=buying_home")));
	}

	@Test
	void texasRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OARS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TX&projectType=buying_home")));
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
	void michiganBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/michigan/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Michigan")))
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
	void illinoisBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/illinois/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("evaluation-form")))
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
	void newYorkRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New York")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("specific waiver")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NY&projectType=buying_home")));
	}

	@Test
	void ohioRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("off-lot-discharge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OH&projectType=buying_home")));
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
	void virginiaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Virginia Septic Permit Process | Office, file, and approval steps | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("construction permit and operation permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health district environmental health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VA&projectType=new_install")));
	}

	@Test
	void californiaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/california/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tier 1")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LAMP-driven local program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CA&projectType=new_install")));
	}

	@Test
	void californiaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/california/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("California Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in California")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county environmental health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LAMP-driven local program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=CA&projectType=inspection")));
	}

	@Test
	void wisconsinPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/wisconsin/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Wisconsin")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or delegated agent")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WI&projectType=new_install")));
	}

	@Test
	void georgiaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county environmental health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("garbage disposal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=buying_home")));
	}

	@Test
	void arizonaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/arizona/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Arizona")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Arizona")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Uniform Site Investigation Report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AZ&projectType=buying_home")));
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
	void illinoisPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/illinois/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local offices review many private sewage construction plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("evaluation-form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IL&projectType=new_install")));
	}

	@Test
	void illinoisInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/illinois/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Illinois Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Illinois")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("evaluation-form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IL&projectType=inspection")));
	}

	@Test
	void ohioBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/ohio/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("off-lot discharge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OH&projectType=buying_home")));
	}

	@Test
	void ohioInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/ohio/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ohio Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operational inspections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("off-lot discharge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OH&projectType=inspection")));
	}

	@Test
	void minnesotaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/minnesota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Minnesota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local SSTS program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("prior compliance-inspection report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MN&projectType=new_install")));
	}

	@Test
	void michiganPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/michigan/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Michigan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failed-system evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("system is located")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MI&projectType=new_install")));
	}

	@Test
	void michiganInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/michigan/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Michigan Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Michigan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failed-system evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("system is located")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MI&projectType=inspection")));
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
	void northDakotaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/north-dakota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Dakota Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in North Dakota")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local public health unit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("investigate complaints")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=ND&projectType=new_install")));
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
	void texasPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("licensed site evaluator or professional engineer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved-plan path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TX&projectType=perc_test")));
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
	void wyomingPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/wyoming/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Wyoming")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county programs delegated by DEQ")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("engineer-designed systems are required")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WY&projectType=perc_test")));
	}

	@Test
	void newYorkPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New York")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("specific waiver")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NY&projectType=new_install")));
	}

	@Test
	void newYorkInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in New York")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("specific waiver")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NY&projectType=inspection")));
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
	void northCarolinaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("improvement permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=buying_home")));
	}

	@Test
	void louisianaReplacementPageRenders() throws Exception {
		mockMvc.perform(get("/septic-replacement-cost/louisiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parish health unit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("community-sewer gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=LA&projectType=replacement")));
	}

	@Test
	void indianaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/indiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permit and site file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IN&projectType=buying_home")));
	}

	@Test
	void oklahomaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/oklahoma/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local DEQ office or county environmental specialist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("authorization or permit to construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OK&projectType=new_install")));
	}

	@Test
	void kentuckyBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/kentucky/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Kentucky")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-evaluation report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=KY&projectType=buying_home")));
	}

	@Test
	void iowaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/iowa/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Iowa")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county sanitarian")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IA&projectType=buying_home")));
	}

	@Test
	void kansasPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/kansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or city sanitary-code office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil profile")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=KS&projectType=new_install")));
	}

	@Test
	void nebraskaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nebraska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska DHHS or local office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registered-system record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NE&projectType=buying_home")));
	}

	@Test
	void newMexicoRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-mexico/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-search result")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("homeowner notice")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NM&projectType=buying_home")));
	}

	@Test
	void utahRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department or district engineer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil log and percolation test results")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=UT&projectType=buying_home")));
	}

	@Test
	void westVirginiaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/west-virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in West Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public-records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewage permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WV&projectType=buying_home")));
	}

	@Test
	void louisianaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/louisiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Louisiana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("application packet and property plat")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("community-sewer gate and parish packet friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=LA&projectType=buying_home")));
	}

	@Test
	void oklahomaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/oklahoma/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oklahoma Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("existing-system evaluation record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil-profile path and system-choice friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OK&projectType=inspection")));
	}

	@Test
	void indianaBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/indiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Indiana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permit and site file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewer-availability gate and local-board variation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IN&projectType=buying_home")));
	}

	@Test
	void kentuckyPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/kentucky/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kentucky Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OSDS construction permit and site-evaluation report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-suitability and local-file friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=KY&projectType=new_install")));
	}

	@Test
	void iowaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/iowa/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iowa Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-transfer inspection and escrow-or-waiver record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-transfer and county-sanitarian friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=IA&projectType=inspection")));
	}

	@Test
	void nebraskaPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/nebraska/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nebraska Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-suitability review and registered-system file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registered-system file gaps and local requirement friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NE&projectType=perc_test")));
	}

	@Test
	void newMexicoPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/new-mexico/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Mexico Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("onsite liquid-waste permit file and forms path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-search gaps and forms-path friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NM&projectType=new_install")));
	}

	@Test
	void utahInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/utah/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operating-permit note and local health file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local control and operating-permit friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=UT&projectType=inspection")));
	}

	@Test
	void westVirginiaInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/west-virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitarian file and public-records request trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("thin local file and public-record delay")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WV&projectType=inspection")));
	}

	@Test
	void missouriBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/missouri/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Missouri")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permitting-jurisdiction map and existing permit file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local-authority routing and acreage-based permit friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MO&projectType=buying_home")));
	}

	@Test
	void oregonBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/oregon/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Oregon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("latest site evaluation and any authorization notice")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-evaluation-first sequencing and authorization-notice friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=OR&projectType=buying_home")));
	}

	@Test
	void washingtonBuyerPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built permit record and O&M logs")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local-LHJ control and O&M-log risk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WA&projectType=buying_home")));
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
	void drainfieldContentPagePointsToDedicatedEstimator() throws Exception {
		mockMvc.perform(get("/drain-field-replacement-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Drain field tool")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A field problem stops being local trench work the moment the replacement footprint is uncertain.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a drain field estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start short quote form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=drainfield_replacement&amp;quoteMode=true#quote-request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/connecticut/")));
	}

	@Test
	void drainfieldCalculatorResultPointsToDedicatedEstimator() throws Exception {
		mockMvc.perform(post("/septic-system-cost-calculator/")
						.param("stateCode", "GA")
						.param("projectType", "drainfield_replacement")
						.param("bedrooms", "4")
						.param("soilPercStatus", "poor_drainage")
						.param("accessDifficulty", "hard")
						.param("timeline", "researching"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Field-specific next move")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/?state=GA")));
	}

	@Test
	void failedPercContentPageRenders() throws Exception {
		mockMvc.perform(get("/failed-perc-test-septic/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Failed Perc Test for Septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a failed-perc estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=perc_test")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/")));
	}

	@Test
	void replacementAreaContentPagePointsToDrainfieldEstimator() throws Exception {
		mockMvc.perform(get("/septic-replacement-area/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Replacement Area Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a replacement-area estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start short quote form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=drainfield_replacement&amp;quoteMode=true#quote-request")));
	}

	@Test
	void wetYardContentPagePointsToDrainfieldEstimator() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wet Yard Over Septic Drain Field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a field-failure estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/oregon/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/pennsylvania/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/connecticut/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")));
	}

	@Test
	void priorityStateDueDiligencePagesLinkIntoDrainfieldCluster() throws Exception {
		String[][] expectations = {
				{"/septic-records-checklist/georgia/", "/drain-field-replacement-cost/georgia/"},
				{"/septic-records-checklist/georgia/", "/failed-perc-test-septic/georgia/"},
				{"/septic-inspection-cost/georgia/", "/wet-yard-over-septic-drain-field/georgia/"},
				{"/buying-a-house-with-a-septic-system/georgia/", "/septic-replacement-area/georgia/"},
				{"/septic-records-checklist/pennsylvania/", "/drain-field-replacement-cost/pennsylvania/"},
				{"/septic-inspection-cost/pennsylvania/", "/wet-yard-over-septic-drain-field/pennsylvania/"},
				{"/buying-a-house-with-a-septic-system/pennsylvania/", "/septic-replacement-area/pennsylvania/"},
				{"/septic-records-checklist/connecticut/", "/drain-field-replacement-cost/connecticut/"},
				{"/septic-inspection-cost/connecticut/", "/failed-perc-test-septic/connecticut/"},
				{"/buying-a-house-with-a-septic-system/connecticut/", "/septic-replacement-area/connecticut/"},
				{"/septic-records-checklist/oregon/", "/drain-field-replacement-cost/oregon/"},
				{"/septic-inspection-cost/oregon/", "/wet-yard-over-septic-drain-field/oregon/"},
				{"/buying-a-house-with-a-septic-system/oregon/", "/septic-replacement-area/oregon/"},
				{"/septic-records-checklist/massachusetts/", "/failed-perc-test-septic/massachusetts/"},
				{"/septic-inspection-cost/massachusetts/", "/drain-field-replacement-cost/massachusetts/"},
				{"/buying-a-house-with-a-septic-system/massachusetts/", "/wet-yard-over-septic-drain-field/massachusetts/"},
				{"/septic-records-checklist/florida/", "/drain-field-replacement-cost/florida/"},
				{"/septic-inspection-cost/florida/", "/failed-perc-test-septic/florida/"},
				{"/buying-a-house-with-a-septic-system/florida/", "/septic-replacement-area/florida/"}
		};

		for (String[] expectation : expectations) {
			mockMvc.perform(get(expectation[0]))
					.andExpect(status().isOk())
					.andExpect(content().string(org.hamcrest.Matchers.containsString(expectation[1])));
		}
	}

	@Test
	void secondaryStateDueDiligencePagesLinkIntoDrainfieldCluster() throws Exception {
		String[][] expectations = {
				{"/septic-records-checklist/new-jersey/", "/drain-field-replacement-cost/new-jersey/"},
				{"/septic-inspection-cost/new-jersey/", "/wet-yard-over-septic-drain-field/new-jersey/"},
				{"/buying-a-house-with-a-septic-system/new-jersey/", "/septic-replacement-area/new-jersey/"},
				{"/septic-records-checklist/washington/", "/failed-perc-test-septic/washington/"},
				{"/septic-inspection-cost/washington/", "/drain-field-replacement-cost/washington/"},
				{"/buying-a-house-with-a-septic-system/washington/", "/wet-yard-over-septic-drain-field/washington/"},
				{"/septic-records-checklist/north-carolina/", "/septic-replacement-area/north-carolina/"},
				{"/septic-inspection-cost/north-carolina/", "/failed-perc-test-septic/north-carolina/"},
				{"/buying-a-house-with-a-septic-system/north-carolina/", "/drain-field-replacement-cost/north-carolina/"},
				{"/septic-records-checklist/colorado/", "/failed-perc-test-septic/colorado/"},
				{"/septic-inspection-cost/colorado/", "/wet-yard-over-septic-drain-field/colorado/"},
				{"/buying-a-house-with-a-septic-system/colorado/", "/drain-field-replacement-cost/colorado/"}
		};

		for (String[] expectation : expectations) {
			mockMvc.perform(get(expectation[0]))
					.andExpect(status().isOk())
					.andExpect(content().string(org.hamcrest.Matchers.containsString(expectation[1])));
		}
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,nofollow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Closest next pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("closest intent match")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/states/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/")));
	}

	@Test
	void drainfieldLikeNotFoundPathShowsIntentAwareRecoveryLinks() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/not-a-real-state/"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/")));
	}

	private void assertStateMoneyPageRenders(String path, String title, String anchorText, String calculatorPath) throws Exception {
		mockMvc.perform(get(path))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString(title)))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(anchorText)))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(calculatorPath)));
	}

}
