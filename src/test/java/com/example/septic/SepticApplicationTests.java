package com.example.septic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.FaqBlock;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import com.example.septic.service.EstimatorResult;
import com.example.septic.service.EstimatorService;
import com.example.septic.service.OpsReportCredentialsService;
import com.example.septic.service.PublishingPolicyService;
import com.example.septic.service.ResearchDataService;
import com.example.septic.web.EstimateForm;
import com.example.septic.web.PageLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@Autowired
	private PublishingPolicyService publishingPolicyService;

	@Autowired
	private ResearchDataService researchDataService;

	@Autowired
	private OpsReportCredentialsService opsReportCredentialsService;

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
		Files.createDirectories(TEST_STORAGE_ROOT);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void compactPageLinkTitleShortensCountyHeroLabelsWithoutChangingSourceTitle() {
		PageLink countyRecordsLink = new PageLink(
				"Greenville County South Carolina Septic Records and Permit Lookup",
				"/septic-records-checklist/south-carolina/greenville-county/",
				"Greenville County records lookup path"
		);
		PageLink permitLookupLink = new PageLink(
				"Davidson County, TN permit lookup",
				"/septic-records-checklist/tennessee/davidson-county/",
				"Davidson County permit lookup path"
		);

		assertEquals("Greenville County South Carolina Septic Records and Permit Lookup", countyRecordsLink.title());
		assertEquals("Greenville County records", countyRecordsLink.compactTitle());
		assertEquals("Davidson County, TN permit lookup", permitLookupLink.compactTitle());
	}

	@Test
	void homeSurfacesCountyRouteBoardAboveGenericPlanningPages() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-finder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search live county septic record paths before you read another overview.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast county routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the exact county file path before another broad guide.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina county routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wake County records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("2-week search response routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/tdec-septic-records/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/dhec-septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/tarrant-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"@id\":\"https://example.test/#website\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"@id\":\"https://example.test/#organization\"")));
	}

	@Test
	void permitLookupHubSurfacesGroupedStateCountyRoutes() throws Exception {
		mockMvc.perform(get("/septic-permit-lookup/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-finder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast search intent routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("content_page_hero_command_primary")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Known county?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search the county route instead of staying on the national page.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State-by-state county routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee county routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Davidson County records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Davidson County, TN permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official content file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Owner, artifact, request, fallback")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No-record fallback")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Answer TDEC septic records searches directly")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/tdec-septic-records/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/dhec-septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/tarrant-county/")));
	}

	@Test
	void officialLookupToolsPagesRenderSourceBackedConsole() throws Exception {
		mockMvc.perform(get("/official-septic-lookup-tools/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Official Septic Lookup Tools")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast search intent routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#official-lookup-command-board\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#official-file-path\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official content file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official lookup command board")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://tdec.tn.gov/document-viewer/search/stp")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://www.deq.nc.gov/about/divisions/water-resources/water-resources-public-information/public-records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://www.tceq.texas.gov/permitting/ossf")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://floridadep.gov/water/onsite-sewage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://des.sc.gov/permits-regulations/septic-tanks")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/dhec-septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-request-builder/")));

		mockMvc.perform(get("/tdec-septic-records/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>TDEC Septic Permit Search &amp; SSDS Records | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/official-septic-lookup-tools/\">Official Septic Lookup Tools</a>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"Official Septic Lookup Tools\",\"item\":\"https://example.test/official-septic-lookup-tools/\",\"position\":2")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("TDEC Septic Permit Search and SSDS Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("TDEC records searches need the state search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open TDEC SSDS search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open TDEC search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("If the official tab returns 403 or blocks your region")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/wake-county/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/tarrant-county/"))));

		mockMvc.perform(get("/north-carolina-septic-permit-lookup/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>How to Find NC Septic Permits by County | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina septic permits are usually held by county environmental health.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina searches usually resolve at county environmental health.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open NC records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/wake-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/johnston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/tarrant-county/"))));

		mockMvc.perform(get("/texas-ossf-records-search/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas OSSF searches need the state context")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open TCEQ OSSF")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/tarrant-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/denton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/wake-county/"))));

		mockMvc.perform(get("/florida-ostds-permit-lookup/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Florida OSTDS searches start with the county health record path.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Florida OSTDS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/florida/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/"))));

		mockMvc.perform(get("/dhec-septic-permit-lookup/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>DHEC Septic Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DHEC searches should land on the current SCDES septic route.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open SCDES septic")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("If SCDES blocks the tab")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("D-1740 application trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/south-carolina/greenville-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/tarrant-county/"))));
	}

	@Test
	void recordsRequestBuilderRendersCopyReadyWorkspace() throws Exception {
		mockMvc.perform(get("/septic-records-request-builder/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Records Request Builder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-records-request-builder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Records request builder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Download packet contents")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Office-ready message")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fallback proof")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("PDF-ready cover packet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Submission route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Before sending checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Copy request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Download packet .txt")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Print / save PDF")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-records-request-download")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-records-request-print")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-records-request-filename")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas / OSSF agent")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina / SCDES")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/official-septic-lookup-tools/")));
	}

	@Test
	void countyFinderAddsOperationalFilters() throws Exception {
		mockMvc.perform(get("/septic-records-by-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-finder-method")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Online search or portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-finder-artifact")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First artifact")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-finder-confidence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("82%+ high-confidence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-finder-parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-confidence-score")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-county-finder-results")));
	}

	@Test
	void allPublishedCountyPagesHaveExplicitWorkflowStructure() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(Files.readString(Path.of("data/raw/county_records_pages.json")));
		JsonNode pages = root.path("pages");
		List<String> missingWorkflowStructure = new ArrayList<>();
		List<String> blankWorkflowFields = new ArrayList<>();

		for (JsonNode page : pages) {
			if (!"published".equals(page.path("publishStatus").asText())) {
				continue;
			}

			String countyLabel = page.path("countyName").asText() + ", " + page.path("stateCode").asText();
			JsonNode workflow = page.get("workflowStructure");
			if (workflow == null || workflow.isNull()) {
				missingWorkflowStructure.add(countyLabel);
				continue;
			}

			for (String field : List.of(
					"fileOwnerCategory",
					"fileOwnerModel",
					"firstArtifactToPull",
					"permitCloseoutCategory",
					"permitCloseoutSignal",
					"transferCategory",
					"transferArtifact",
					"specialProgramCategory",
					"specialProgramSignal",
					"malfunctionCategory",
					"malfunctionSignal",
					"quoteGate"
			)) {
				if (workflow.path(field).asText().isBlank()) {
					blankWorkflowFields.add(countyLabel + " :: " + field);
				}
			}
		}

		org.junit.jupiter.api.Assertions.assertTrue(
				missingWorkflowStructure.isEmpty(),
				"Published county pages missing workflowStructure: " + missingWorkflowStructure
		);
		org.junit.jupiter.api.Assertions.assertTrue(
				blankWorkflowFields.isEmpty(),
				"Published county workflowStructure fields must be nonblank: " + blankWorkflowFields
		);
	}

	@Test
	void workflowCostPagesUseEvidenceBasedIndexingCohorts() {
		List<String> workflowCostSlugs = List.of(
				"septic-replacement-cost",
				"perc-test-cost",
				"septic-inspection-cost",
				"drain-field-replacement-cost",
				"septic-pumping-cost",
				"failed-perc-test-septic",
				"septic-replacement-area",
				"wet-yard-over-septic-drain-field"
		);
		List<String> incomplete = new ArrayList<>();
		int indexableCount = 0;

		for (String workflowCostSlug : workflowCostSlugs) {
			for (StateMoneyPage page : researchDataService.listPublicStateMoneyPagesForContent(workflowCostSlug)) {
				StateProfile state = researchDataService.findStateByCode(page.stateCode()).orElseThrow();
				if (!publishingPolicyService.isCostReopenCandidate(page, state)) {
					incomplete.add(page.contentSlug() + "/" + state.slug());
				}
				if (publishingPolicyService.isIndexableStateMoneyPage(page, state)) {
					indexableCount++;
				}
			}
		}

		org.junit.jupiter.api.Assertions.assertTrue(
				incomplete.isEmpty(),
				"Workflow cost pages missing baseline content: " + incomplete
		);
		org.junit.jupiter.api.Assertions.assertEquals(76, indexableCount);
		org.junit.jupiter.api.Assertions.assertTrue(indexable("perc-test-cost", "tennessee"));
		org.junit.jupiter.api.Assertions.assertFalse(indexable("perc-test-cost", "alabama"));
		org.junit.jupiter.api.Assertions.assertTrue(indexable("septic-replacement-cost", "georgia"));
		org.junit.jupiter.api.Assertions.assertFalse(indexable("septic-replacement-cost", "idaho"));
		org.junit.jupiter.api.Assertions.assertTrue(indexable("septic-inspection-cost", "massachusetts"));
		org.junit.jupiter.api.Assertions.assertFalse(indexable("septic-inspection-cost", "hawaii"));
	}

	@Test
	void sitemapIncludesOnlyEvidenceBackedWorkflowCostPages() throws Exception {
		List<String> workflowCostSlugs = List.of(
				"septic-replacement-cost",
				"perc-test-cost",
				"septic-inspection-cost",
				"drain-field-replacement-cost",
				"septic-pumping-cost",
				"failed-perc-test-septic",
				"septic-replacement-area",
				"wet-yard-over-septic-drain-field"
		);
		String sitemap = mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		List<String> mismatches = new ArrayList<>();
		int indexableCount = 0;

		for (String workflowCostSlug : workflowCostSlugs) {
			for (StateMoneyPage page : researchDataService.listPublicStateMoneyPagesForContent(workflowCostSlug)) {
				StateProfile state = researchDataService.findStateByCode(page.stateCode()).orElseThrow();
				String url = "https://example.test" + page.path(state.slug());
				boolean indexable = publishingPolicyService.isIndexableStateMoneyPage(page, state);
				if (indexable) {
					indexableCount++;
				}
				if (sitemap.contains(url) != indexable) {
					mismatches.add(url + " expectedInSitemap=" + indexable);
				}
			}
		}

		org.junit.jupiter.api.Assertions.assertEquals(76, indexableCount);
		org.junit.jupiter.api.Assertions.assertTrue(
				mismatches.isEmpty(),
				"Workflow cost sitemap policy mismatches: " + mismatches
		);
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
				null,
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
				unpublished.updatedAt(),
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
				null,
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
				unpublished.updatedAt(),
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Records Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/app.css?v=")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/app-core.js?v=")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/app.js?v=")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("[data-county-finder]")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("rel=\"preload\" href=\"https://fonts.googleapis.com/css2?family=Manrope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-site-nav-toggle")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-nav-menu")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-by-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-record-finder/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-records-request/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-as-built-records/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-letter/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-transfer-compliance/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/editorial-standards/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/privacy-policy/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("application/ld+json")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State guides")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Find the county septic file before the quote.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Popular routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search records by county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Find records by address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("TDEC records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SCDES records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("NC permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most visits should start with a file, not a price.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Live county workflow backbone")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County-backed network")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-first follow-up instead of a generic state-only answer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county-backed state page")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/social-card.svg")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Anchor states"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Supporting states"))));
	}

	@Test
	void addressRecordFinderRendersAndRejectsIncompleteAddress() throws Exception {
		mockMvc.perform(get("/septic-record-finder/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Records Finder by Address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-address-record-finder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Address not saved")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/api/address-record-finder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Find the record route by county name")));

		mockMvc.perform(post("/api/address-record-finder")
					.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
					.content("{\"address\":\"short\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"status\":\"invalid\"")));
	}

	@Test
	void recordsAccessIndexAndEmbedRenderAndEnterSitemap() throws Exception {
		mockMvc.perform(get("/septic-records-access-index/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Records Access Index")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No address? Use county search instead.")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("data-record-finder-embed-copy"))));

		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-access-index/")));

		mockMvc.perform(get("/embed/septic-record-finder/"))
				.andExpect(status().isOk())
				.andExpect(header().doesNotExist("X-Frame-Options"))
				.andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("frame-ancestors *")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,nofollow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-address-record-finder")));
	}

	@Test
	void offerPrepFileCheckRendersForFourStatesAndEntersSitemap() throws Exception {
		mockMvc.perform(get("/offer-prep-septic-file-check/?src=tn-rural-buyer-guide"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Offer Prep Septic File Check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-offer-prep-file-check")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Address not saved")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-offer-prep-download")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("embed code"))));

		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/offer-prep-septic-file-check/")));

		mockMvc.perform(get("/api/county-finder/?q=Blount"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"stateCode\":\"TN\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Blount County")));
	}

	@Test
	void bedroomPermitCheckerRendersAndEntersSitemap() throws Exception {
		mockMvc.perform(get("/septic-bedroom-permit-checker/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Bedroom Permit Checker")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-bedroom-permit-checker")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Does the listing bedroom count match the septic permit?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nothing saved")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/embed/septic-bedroom-permit-checker/")));

		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-bedroom-permit-checker/")));

		mockMvc.perform(get("/embed/septic-bedroom-permit-checker/"))
				.andExpect(status().isOk())
				.andExpect(header().doesNotExist("X-Frame-Options"))
				.andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("frame-ancestors *")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,nofollow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-bedroom-permit-checker")));
	}

	@Test
	void trustOperationsPagesRenderAndEnterSitemap() throws Exception {
		mockMvc.perform(get("/methodology/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How pages earn the right to be public.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Public quality gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/source-policy/")));

		mockMvc.perform(get("/source-policy/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The source trail is part of the product.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Source registry")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/contact/")));

		mockMvc.perform(get("/coverage/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Live coverage, source depth, and county workflow density.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Coverage rows to use for prioritization")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Priority county routes to push first.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("trust-ops-priority-route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic permit lookup |")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Workflow pages")));

		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/methodology/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/source-policy/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/coverage/")));
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
	void htmlResponsesCarrySecurityHeaders() throws Exception {
		mockMvc.perform(get("/")
						.header("X-Forwarded-Proto", "https")
						.with(request -> {
							request.setScheme("http");
							request.setServerName("example.test");
							request.setServerPort(80);
							return request;
						}))
				.andExpect(status().isOk())
				.andExpect(header().string("X-Content-Type-Options", "nosniff"))
				.andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
				.andExpect(header().string("Permissions-Policy", org.hamcrest.Matchers.containsString("geolocation=()")))
				.andExpect(header().string("X-Frame-Options", "DENY"))
				.andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("frame-ancestors 'none'")))
				.andExpect(header().string("Content-Security-Policy", org.hamcrest.Matchers.containsString("https://www.google.com")))
				.andExpect(header().string("Strict-Transport-Security", org.hamcrest.Matchers.containsString("max-age=31536000")));
	}

	@Test
	void robotsTxtExposesSitemap() throws Exception {
		mockMvc.perform(get("/robots.txt"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("User-agent: *")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sitemap: https://example.test/sitemap.xml")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sitemap: https://example.test/sitemap-county.xml")));
	}

	@Test
	void sitemapXmlIncludesCoreUrls() throws Exception {
		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<lastmod>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<url><loc>https://example.test/</loc></url>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<url><loc>https://example.test/septic-permit-lookup/</loc></url>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<url><loc>https://example.test/tdec-septic-records/</loc><lastmod>2026-07-20</lastmod></url>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<url><loc>https://example.test/septic-records-checklist/tennessee/</loc><lastmod>2026-07-20</lastmod></url>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-transfer-compliance/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/how-to-find-septic-records-online/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-by-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-record-finder/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-search-by-address/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-records-request/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-as-built-records/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-letter/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/official-septic-lookup-tools/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-request-builder/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/dhec-septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/tdec-septic-records/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/north-carolina-septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/texas-ossf-records-search/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/florida-ostds-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/privacy-policy/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-system-cost-calculator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-tank-size-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-pump-schedule-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/washington/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/failed-perc-test-septic/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-area/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-process/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/buying-a-house-with-a-septic-system/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/drain-field-replacement-cost/colorado/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/massachusetts/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("https://example.test/septic-replacement-cost/idaho/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("https://example.test/perc-test-cost/alabama/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("https://example.test/septic-inspection-cost/hawaii/"))));
	}

	@Test
	void countySitemapXmlIncludesOnlyCountyWedgeUrls() throws Exception {
		mockMvc.perform(get("/sitemap-county.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<lastmod>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<url><loc>https://example.test/septic-records-checklist/tennessee/blount-county/</loc><lastmod>2026-07-20</lastmod></url>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<url><loc>https://example.test/septic-records-checklist/texas/tarrant-county/</loc><lastmod>2026-05-07</lastmod></url>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/south-carolina/greenville-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/comal-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("https://example.test/septic-permit-lookup/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/south-carolina/</loc>"))));
	}

	@Test
	void sitemapXmlLeavesCountyWedgeUrlsToCountySitemap() throws Exception {
		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/washington/king-county/"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/wake-county/"))));
	}

	@Test
	void countySitemapXmlIncludesCountyWedgeUrls() throws Exception {
		mockMvc.perform(get("/sitemap-county.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/baldwin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/madison-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/shelby-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/tuscaloosa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/montgomery-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/autauga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/alabama/st-clair-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/indiana/st-joseph-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/indiana/porter-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/indiana/grant-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/indiana/tippecanoe-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/durham-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/iredell-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/guilford-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/georgia/dekalb-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/georgia/fulton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/georgia/gwinnett-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/south-carolina/greenville-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/south-carolina/charleston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/south-carolina/horry-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/travis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/bexar-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/williamson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/montgomery-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/fort-bend-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/brazoria-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/comal-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/parker-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/loudon-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/harris-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/collin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/tarrant-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/denton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/texas/hays-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/davidson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/knox-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/shelby-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/rutherford-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/placer-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/el-dorado-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/trinity-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/sonoma-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/napa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/ventura-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/marin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/santa-cruz-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/san-luis-obispo-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/tuolumne-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/riverside-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/san-bernardino-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/san-diego-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/santa-clara-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/california/monterey-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/suffolk-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/westchester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/dutchess-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/rockland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/albany-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/monroe-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/livingston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/chautauqua-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/wyoming-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/putnam-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/erie-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/tompkins-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/broome-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/genesee-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/onondaga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/hamilton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/clermont-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/summit-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/lucas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/franklin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/geauga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/delaware-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/lorain-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/lake-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/hocking-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/tuscarawas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/portage-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/mahoning-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/clark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/stark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/medina-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/ohio/cuyahoga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/washington/king-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/washington/whatcom-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/washington/clark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/washington/thurston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/washington/snohomish-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/oregon/clackamas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/oregon/deschutes-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/oregon/washington-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/oregon/lane-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/oregon/clatsop-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/larimer-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/jefferson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/boulder-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/el-paso-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/pitkin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/mesa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/weld-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/douglas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/adams-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/colorado/routt-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/howard-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/garrett-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/st-marys-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/anne-arundel-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/frederick-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/dorchester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/carroll-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/worcester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/cecil-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/harford-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/baltimore-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/montgomery-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/prince-georges-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/charles-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/cayuga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/seneca-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/allegany-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/madison-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/cortland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-york/tioga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/washtenaw-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/ottawa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/livingston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/kent-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/genesee-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/oakland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/macomb-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/ingham-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/michigan/kalamazoo-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/ocean-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/monmouth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/minnesota/olmsted-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/minnesota/st-louis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/minnesota/chisago-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/minnesota/blue-earth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/minnesota/dakota-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wisconsin/kenosha-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wisconsin/washington-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wisconsin/waukesha-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wisconsin/st-croix-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wisconsin/calumet-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wisconsin/dane-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/illinois/mchenry-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/illinois/lake-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/illinois/kane-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kansas/johnson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kansas/sedgwick-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kansas/pottawatomie-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kansas/ellis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/kansas/kingman-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/boone-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/jackson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/st-charles-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/greene-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/pima-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/yavapai-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/maricopa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/coconino-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/pinal-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/santa-cruz-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/yuma-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/mohave-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/arizona/cochise-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/montana/flathead-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/montana/missoula-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/montana/gallatin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/buncombe-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/chatham-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/orange-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/brunswick-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/cabarrus-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/union-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/clarke-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/prince-william-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/mecklenburg-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/wake-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/forsyth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/pender-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/johnston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/loudoun-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/james-city-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/spotsylvania-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/fairfax-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/york-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/chesterfield-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/virginia/hanover-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/indiana/elkhart-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/sussex-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/burlington-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/atlantic-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/cape-may-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/gloucester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/salem-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/camden-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/middlesex-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/somerset-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/new-jersey/hunterdon-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/hamilton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/williamson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/tennessee/blount-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/georgia/hall-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/georgia/forsyth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/georgia/jackson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/talbot-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/queen-annes-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/wicomico-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/somerset-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/caroline-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/maryland/kent-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/davis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/utah-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/tooele-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/cache-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/rich-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/box-elder-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/iron-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/sanpete-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/san-juan-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/wasatch-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/summit-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/weber-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/utah/washington-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/washoe-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/lyon-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/douglas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/clark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/churchill-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/storey-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/carson-city/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/elko-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/humboldt-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/nevada/lincoln-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/teton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/johnson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/uinta-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/sublette-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/laramie-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/park-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/natrona-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/sheridan-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/albany-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/campbell-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/converse-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/wyoming/goshen-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/clay-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/franklin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/cole-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/christian-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/taney-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/missouri/butler-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/new-hanover-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/harnett-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/onslow-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/cumberland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/pitt-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/moore-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/alamance-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/carteret-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/dare-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://example.test/septic-records-checklist/north-carolina/craven-county/")));
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
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Start with the state, then narrow to the file path.")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("County file pages")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("States with county follow-up")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Live county workflow backbone")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Where the backbone is already thick")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Use these as routing pages, not final answers.")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Queue closed")))
					.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Queue states"))))
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
						.param("sourcePageHint", "/septic-replacement-cost/")
						.param("quoteMode", "true"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Get matched with local septic pros")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Already know the job type?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Full name")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Project type")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"sourcePageHint\" value=\"/septic-replacement-cost/\"")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("When this estimate is the right next step")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What widens the range fastest")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Next best pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Calculator start")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Get a planning range now.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start with workflow instead")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia septic permit cost, permit records, and soil analysis guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Permit Cost, Permit Records, and Soil Analysis Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Prepared by")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath Editorial Team")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed by")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath Source Review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 4 official sources listed below and 6 live county workflow pages already connected to this state.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-04-04\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"editor\":{\"@type\":\"Organization\",\"name\":\"SepticPath Source Review\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bedroom table sizing rule")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(">permit_path<"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Quick facts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Source-backed rule facts for Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How much is a perc test in Georgia?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Very high confidence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Planning cost snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County office and records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use this Georgia guide before you click into one intent page")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How the core six launch states differ")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("You are here")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County-aware prep checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Workflow Snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common quote gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQ")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FAQPage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run the estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the next workflow page")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit requirements and timing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Need a planning range after the county check?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official sources")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("aria-label=\"Breadcrumb\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Septic System Cost Calculator<")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_action_rail_workflow\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_action_rail_calculator_followup\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_inline_intent_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_inline_source_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_guide_next_high_intent\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<link rel=\"canonical\" href=\"https://example.test/septic-system-cost-calculator/georgia/\">")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Still under review"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Confidence:"))));
	}

	@Test
	void georgiaStateGuideShowsCountyWedgeLinks() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/georgia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County-backed reality")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County records pages now live in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia search path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia county file before the estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/dekalb-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/fulton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/gwinnett-county/")));
	}

	@Test
	void alabamaRecordsPageShowsCountyWedgeLinks() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/alabama/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County record pages behind this state workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/alabama/baldwin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/alabama/madison-county/")));
	}

	@Test
	void alabamaCountyRecordsPageUsesStateSpecificCopy() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/alabama/madison-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Madison County Alabama Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Alabama records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Alabama guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Related Alabama pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Check Alabama permit-copy and Approval for Use rules")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Open the Indiana records page"))));
	}

	@Test
	void georgiaCountyRecordsPageShowsCertificationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/georgia/dekalb-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DeKalb County Georgia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request a DeKalb certification letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("dekalb.eh@dph.ga.gov")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("445 Winn Way, Suite 320")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Georgia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Georgia guide")));
	}

	@Test
	void travisCountyRecordsPageShowsSplitRecordsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/travis-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Travis County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County workflow structure")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("File owner model")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit closeout signal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Travis County Transportation and Natural Resources usually owns the meaningful septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("both the older records lane and the post-2014 permit lane are checked")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not price yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Travis County septic records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("recordsctr@traviscountytx.gov")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("license-to-operate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void fortBendCountyRecordsPageShowsPermitAndLicenseWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/fort-bend-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Fort Bend County TX Septic Permit Lookup &amp; Records | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fort Bend County septic permit lookup and records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fort Bend County Texas Septic Records Checklist and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Share county route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-share-url=\"https://example.test/septic-records-checklist/texas/fort-bend-county/\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Turn the county file into a quote-ready estimate.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run quote-ready estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open quote request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("projectType=replacement")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sourcePageHint=/septic-records-checklist/texas/fort-bend-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fort Bend County TX septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fort Bend County septic permit search by address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Fort Bend County OSSF permits and packets")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit to construct from the license to operate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing-system modifications can require a new permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County record availability matrix")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Route confidence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First file to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit file request builder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search intent answer pack")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fort Bend County septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search by address only after you have the parcel anchor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Five-minute file workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use this page as a work surface, not just a reference page.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Other strong Texas county routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Parcel or property anchor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buyer file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fort Bend County Environmental Health")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")));
	}

	@Test
	void searchResponseCountyPageShowsDossierForReactiveCounty() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/alamance-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County search action board")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Choose the Alamance County route that matches the file problem.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Build exact request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alamance County records and permit guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Priority county route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("alamance county septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start here")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open official county route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-search-by-address/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-records-request/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-as-built-records/")));
	}

	@Test
	void stJosephCountyRecordsPageShowsSchematicLookupWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/indiana/st-joseph-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("St. Joseph County Indiana Septic Records Checklist and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request St. Joseph County septic schematic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("records from 1970 to present")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("some records are incomplete")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("St. Joseph County Department of Health")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records request guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records by county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Indiana records lookup")));
	}

	@Test
	void comalCountyRecordsPageShowsPermitSearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/comal-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Comal County Texas Septic Records and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County search action board")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Comal County records and permit guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("comal county septic permit search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Build exact request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Septic Permit Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OSSF permit, approved plan, license to operate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("dedicated septic permit records search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/how-to-find-septic-records-online/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")));
	}

	@Test
	void durhamCountyRecordsPageShowsOnlineRecordsRequestWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/durham-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Durham County NC Septic Records and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic and Well Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic and well records request page")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Durham County On-Site Water Protection office")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Durham County county"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("improvement permit, construction authorization, operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/how-to-find-septic-records-online/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")));
	}

	@Test
	void tippecanoeCountyRecordsPageShowsDiagramWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/indiana/tippecanoe-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tippecanoe County Indiana Septic Records and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sewage Disposal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic records from the county health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tippecanoe County Health Department onsite sewage office")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Tippecanoe County county"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("email or print diagrams when available")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/how-to-find-septic-records-online/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Indiana records lookup")));
	}

	@Test
	void bexarCountyRecordsPageShowsRegistrationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/bexar-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bexar County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Bexar County public-records requests")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bexar County Environmental Services owns the practical onsite-sewage file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registration, permit-status, and repair-or-renewal history are all clear")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("existing-system registration")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair or renewal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void williamsonCountyRecordsPageShowsJurisdictionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/williamson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Williamson County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Williamson County records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Williamson County's OSSF office owns the real file only after the jurisdiction check confirms the parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("jurisdiction check and county file both confirm the same authority and permit branch")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("property address or property ID")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("MyGovernment Online")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void harrisCountyRecordsPageShowsPermitPacketWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/harris-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Harris County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Harris County Engineer records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Harris County Engineering owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("HCAD-linked packet, site evaluation, and design file all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("HCAD property tax number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("GovQA")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void collinCountyRecordsPageShowsJurisdictionSplitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/collin-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Collin County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Collin County records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Collin County keeps the practical OSSF file only when the parcel stays in the outside-city-limits lane")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-lane check plus the live Citizen Self-Service trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("outside-city-limits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Citizen Self-Service")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void tarrantCountyRecordsPageShowsEtjWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/tarrant-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tarrant County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Tarrant County official records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tarrant County keeps the practical OSSF file only when the parcel stays in the county or contract-city lane")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("ETJ check plus the official-record trail and permit-packet story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("ETJ")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("23 cities")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official county file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tarrant County file route")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Tarrant County County"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No-record fallback")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records request wording")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void dentonCountyRecordsPageShowsPermitPacketWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/denton-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Denton County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Denton County septic permit packet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Denton County owns the practical OSSF file once the parcel is truly in the county's unincorporated lane")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("license-to-operate trail plus the site-plan packet and development-permit history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("license-to-operate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("development-permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void haysCountyRecordsPageShowsSubdivisionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/texas/hays-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hays County Texas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Hays County septic permit FAQs")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hays County Development Services owns the practical septic lane")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("customer-portal history plus the subdivision-clearance and complaint trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("subdivision-regulation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("customer portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Texas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Texas guide")));
	}

	@Test
	void placerCountyRecordsPageShowsApnSearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/placer-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Placer County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Placer Environmental Health septic documents")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Placer County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("12-digit APN search plus the scanned file return and any remodel or addition note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("12-digit APN")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("environmentalhealth@placer.ca.gov")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void trinityCountyRecordsPageShowsPermittedVersusFinaledWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/trinity-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trinity County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Trinity parcel records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trinity County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN search plus the ownership-history narrowing and finaled-status answer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permitted")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("finaled")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void elDoradoCountyRecordsPageShowsParcelResearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/el-dorado-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El Dorado County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open El Dorado parcel research request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El Dorado County Environmental Management owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel-research return plus the plot-plan and replacement-area review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement-area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("plot-plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void sonomaCountyRecordsPageShowsPortalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/sonoma-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sonoma County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Sonoma well and septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit Sonoma owns the practical well-and-septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("portal record plus the linked permit account and any design or site-evaluation note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void napaCountyRecordsPageShowsParcelSearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/napa-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Napa County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Napa PBES wastewater records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Napa County owns the practical wastewater file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("PBES search plus the inspection or permit file and any county follow-up note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("digitized results may not be the complete record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void venturaCountyRecordsPageShowsOnlineAndPaperWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/ventura-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ventura County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Ventura ISDS records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ventura County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online ISDS return plus the paper-file request and field-verification note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sitename")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("paper records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void marinCountyRecordsPageShowsSaleAndOperatingPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/marin-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Marin County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Marin septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Marin County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN search plus the property-sale question trail and any inspection requirement")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("property sale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("annual operating permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void santaCruzCountyRecordsPageShowsRecordationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/santa-cruz-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Santa Cruz County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Santa Cruz water and septic resources")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Santa Cruz County Environmental Health owns the practical OWTS file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("records-research return plus any repair-resource trail and deed-recordation note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("deed recordation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("nonstandard")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void sanLuisObispoCountyRecordsPageShowsMissingRecordWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/san-luis-obispo-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Luis Obispo County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open San Luis Obispo OWTS guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Luis Obispo County Environmental Health owns the practical OWTS file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county records answer plus any no-record response and LAMP or non-conventional note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("no official record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("licensed septic professional")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void tuolumneCountyRecordsPageShowsTwoStepWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/tuolumne-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tuolumne County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tuolumne County permitting process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tuolumne County Environmental Health owns the practical onsite-wastewater file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("branch decision plus the site-and-soils or repair file and any minor-deviation note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("two-step")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("minor deviation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void riversideCountyRecordsPageShowsCertificationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/riverside-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Riverside County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Riverside GIS and permit research path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Riverside County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit lookup, existing-system certification, and any Quail Valley or local-exception branch all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("certification of the existing septic system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Quail Valley")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void sanBernardinoCountyRecordsPageShowsTransferAndApnWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/san-bernardino-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Bernardino County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open San Bernardino parcel research guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Bernardino County Environmental Health owns the practical OWTS file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN, 30-day certification, and county transfer artifact all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("30 days")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void sanDiegoCountyRecordsPageShowsDocumentLibraryWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/san-diego-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Diego County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open San Diego environmental health document search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Diego County Environmental Health usually owns the practical OWTS file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Record ID or APN search and the PRRC fallback are both exhausted")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Record ID or APN")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Public Records Request Center")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void santaClaraCountyRecordsPageShowsAsBuiltWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/santa-clara-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Santa Clara County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Santa Clara as-built request guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Santa Clara County Environmental Health owns the practical OWTS file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built, OWTS clearance path, and drainfield expansion-area story all line up")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("drainfield expansion area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void montereyCountyRecordsPageShowsFileReviewWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/california/monterey-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Monterey County California Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Monterey file review request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Monterey County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("file-review return, permit-design packet, and future repair area all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("future repair area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("ten days")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open California records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the California guide")));
	}

	@Test
	void kingCountyRecordsPageShowsTransferAndRemodelWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/king-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("King County Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open King County buyer and seller septic guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("King County Public Health owns the practical onsite-sewage file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("record drawing, approved-use history, and any transfer or remodel review all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("record drawing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("remodel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://kingcounty.gov/en/dept/dph/health-safety/environmental-health/on-site-sewage-systems/sales-transfers")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("wcms-stage-a-cd.kingcounty.gov"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("cd10-prod.kingcounty.gov"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washington records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Washington guide")));
	}

	@Test
	void whatcomCountyRecordsPageShowsParcelAndAduWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/whatcom-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Whatcom County Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Whatcom County customer service portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Whatcom County owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel records, permit-history trail, and any ADU or added-use service notes all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("ADU")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washington records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Washington guide")));
	}

	@Test
	void clarkWashingtonCountyRecordsPageShowsAsBuiltRecoveryWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/clark-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clark County Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Clark County septic forms and record paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tax-account")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washington records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Washington guide")));
	}

	@Test
	void thurstonCountyRecordsPageShowsTransferAndOpcWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/thurston-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Thurston County Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Thurston homeowner maintenance requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Thurston County owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer report, record drawing, and operational-certificate story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operational certificate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washington records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Washington guide")));
	}

	@Test
	void clackamasCountyRecordsPageShowsAuthorizationNoticeWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/oregon/clackamas-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clackamas County Oregon Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Clackamas authorization notice guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("change in use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("no-records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Oregon records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Oregon guide")));
	}

	@Test
	void deschutesCountyRecordsPageShowsDialAndReplacementAreaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/oregon/deschutes-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Deschutes County Oregon Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Deschutes DIAL research guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DIAL")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement-area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Oregon records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Oregon guide")));
	}

	@Test
	void washingtonOregonCountyRecordsPageShowsAuthorizationAndInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/oregon/washington-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington County Oregon Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washington County inspections and permit portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("added bedrooms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection results")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Oregon records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Oregon guide")));
	}

	@Test
	void laneCountyRecordsPageShowsLmdProWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/oregon/lane-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lane County Oregon Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lane County LMD-PRO property records search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("authorization notice")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("LMD-PRO")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Oregon records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Oregon guide")));
	}

	@Test
	void larimerCountyRecordsPageShowsTransferAndBedroomWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/larimer-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Larimer County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Larimer County transfer-of-title guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Larimer County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel-level septic file plus the transfer-of-title review and approved-use trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void jeffersonCountyRecordsPageShowsUsePermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/jefferson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jefferson County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Jefferson County OWTS program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jefferson County owns the practical OWTS file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer-of-title permit, parcel lookup, and current-use story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer-of-title")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void boulderCountyRecordsPageShowsChangeInUseWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/boulder-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Boulder County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Boulder County property transfer guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Boulder County owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic record search, transfer certificate, and change-in-use story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("ADUs")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("conditional transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void elPasoCountyRecordsPageShowsTransferInspectorWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/el-paso-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El Paso County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El Paso County assessor parcel and property search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El Paso County keeps the practical OWTS file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel-linked file, the county help return, and any transfer-of-title inspection or installer-side path all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer-of-title")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("licensed OWTS installer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void howardCountyRecordsPageShowsPublicFileAndRepairWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/howard-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Howard County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Howard County well and septic records request path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("the owner often has to move between the public search and a formal PIA request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not move into pricing until the public file and the PIA fallback are both resolved")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void garrettCountyRecordsPageShowsParcelAndPercWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/garrett-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Garrett County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Garrett County parcel-based septic records workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("backup area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void stMarysCountyRecordsPageShowsGisAndRepairPercWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/st-marys-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>St. Mary's County Septic Records &amp; GIS Lookup | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("St. Mary's County septic records and GIS lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search St. Mary's County environmental health records in the official GIS by address or Tax ID.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search St. Mary's County environmental health records by GIS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("GIS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair perc")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void anneArundelCountyRecordsPageShowsPercFileWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/anne-arundel-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Anne Arundel County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request Anne Arundel County septic or well records online")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Anne Arundel County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("drawing file, perc file, and property-improvement branch are all clear")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("wet-season")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("property-improvement")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void frederickCountyRecordsPageShowsResearchRequestWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/frederick-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Frederick County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Frederick County information research request form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Frederick County's well-and-septic program owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic layout and permit file plus any building-permit conflict note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("buildable footprint")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void dorchesterCountyRecordsPageShowsPropertyStatusWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/dorchester-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dorchester County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Dorchester County septic status and record-search path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dorchester County Environmental Health keeps the practical file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("property-status search, perc path, and sanitary-construction lane all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("property-status")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary construction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void cayugaCountyRecordsPageShowsTransferAndParcelWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/cayuga-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cayuga County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Cayuga County septic records by parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pumping")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void senecaCountyRecordsPageShowsMandatoryTransferWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/seneca-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Seneca County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Seneca County septic inspection and transfer rules")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("watershed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void alleganyCountyRecordsPageShowsSanitarySurveyWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/allegany-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Allegany County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Allegany County sanitary survey application for property transfers")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary survey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("potability")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void madisonCountyRecordsPageShowsAlternativeSystemWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/madison-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Madison County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Madison County septic replacement fund workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("specific-waiver")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("alternative-system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void cortlandCountyRecordsPageShowsCompletionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/cortland-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cortland County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cortland County subsurface sewage permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("certificate of completion")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("measurements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void tiogaCountyRecordsPageShowsPermitPacketWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/tioga-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tioga County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tioga County real property data viewer route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real-property")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void pimaCountyRecordsPageShowsTransferInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/pima-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pima County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Pima County septic records guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pima County keeps the practical wastewater file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("record portal, the transfer-inspection file, and any construction or discharge authorization all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("activity number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void yavapaiCountyRecordsPageShowsExistingSystemWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/yavapai-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Yavapai County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request Yavapai County wastewater permits and site investigation results")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Yavapai County keeps the practical wastewater file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit copies, site-investigation work, and any existing-system approval all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("existing conventional septic system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("footprint")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void maricopaCountyRecordsPageShowsTransferAndResearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/maricopa-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maricopa County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maricopa County online septic research")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maricopa County Environmental Services owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Report of Inspection plus the seller document handoff and Notice of Transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("paid county records-search fallback")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void coconinoCountyRecordsPageShowsDelegatedAuthorityWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/coconino-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Coconino County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Coconino County online wastewater file search and application portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Coconino County keeps the practical wastewater file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("file search, the transfer-inspection lane, and any remodel or redesign branch all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failed file searches are not refunded")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("notice of transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void pinalCountyRecordsPageShowsTransferAndLocationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/pinal-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pinal County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pinal County septic applications and transfer forms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pinal County keeps the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN-linked file, the transfer paperwork, and the sewer-availability or replacement-area story all support the same parcel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewer-availability")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void santaCruzCountyRecordsPageShowsParcelRequestWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/santa-cruz-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Santa Cruz County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Santa Cruz County public records request for septic files")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("request-for-discharge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void davisCountyRecordsPageShowsSewerProximityWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/davis-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Davis County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Davis County onsite wastewater application and property search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("300 feet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("GIS assessment")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void utahCountyRecordsPageShowsLookupAndPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/utah-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah County septic system lookup application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("loan-clearance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("building permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void washtenawCountyRecordsPageShowsTimeOfSaleWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/washtenaw-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washtenaw County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Washtenaw well and septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washtenaw County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-sale inspection result plus the permit and site-plan trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("time-of-sale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void livingstonCountyMichiganRecordsPageShowsSiteReviewWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/livingston-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Livingston County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Livingston County realtor resources and record search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("point-of-sale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void ottawaCountyRecordsPageShowsTransferEvaluationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/ottawa-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ottawa County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ottawa County real estate transfer evaluation program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("vacant-land")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void olmstedCountyRecordsPageShowsTransferComplianceWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/minnesota/olmsted-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Olmsted County Minnesota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Olmsted County land-transfer compliance form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("jurisdiction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Minnesota records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Minnesota guide")));
	}

	@Test
	void stLouisCountyRecordsPageShowsEscrowAndPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/minnesota/st-louis-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("St. Louis County Minnesota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("View St. Louis County septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("escrow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Minnesota records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Minnesota guide")));
	}

	@Test
	void chisagoCountyRecordsPageShowsWinterEscrowWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/minnesota/chisago-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Chisago County Minnesota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Chisago County point-of-sale septic requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("winter escrow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Noncompliance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Minnesota records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Minnesota guide")));
	}

	@Test
	void blueEarthCountyRecordsPageShowsWinterTransferWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/minnesota/blue-earth-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Blue Earth County Minnesota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ask Blue Earth County for septic records by address or PID")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("winter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Certificate of Compliance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Minnesota records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Minnesota guide")));
	}

	@Test
	void kenoshaCountyRecordsPageShowsMaintenancePortalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wisconsin/kenosha-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kenosha County Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search the Kenosha County sanitary maintenance portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("POWTS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wisconsin records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wisconsin guide")));
	}

	@Test
	void mchenryCountyRecordsPageShowsElectronicRecordsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/illinois/mchenry-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("McHenry County Illinois Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search McHenry County electronic permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("onsite wastewater")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("layout")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Illinois records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Illinois guide")));
	}

	@Test
	void lakeCountyIllinoisRecordsPageShowsHistoricPlanWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/illinois/lake-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lake County Illinois Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Lake County well and septic evaluations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("historic septic plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Illinois records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Illinois guide")));
	}

	@Test
	void johnsonCountyRecordsPageShowsResaleInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/kansas/johnson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Johnson County Kansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Johnson County resale inspection steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("historical file review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("decommissioning")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kansas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Kansas guide")));
	}

	@Test
	void sedgwickCountyRecordsPageShowsWastewaterPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/kansas/sedgwick-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sedgwick County Kansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Sedgwick County wastewater permit guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lagoon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("cost-share")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kansas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Kansas guide")));
	}

	@Test
	void pottawatomieCountyRecordsPageShowsFacilityInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/kansas/pottawatomie-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pottawatomie County Kansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Pottawatomie County environmental health forms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("functional sale or refinance inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sanitation Codes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kansas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Kansas guide")));
	}

	@Test
	void ellisCountyRecordsPageShowsTransferEvaluationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/kansas/ellis-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ellis County Kansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ellis County property transfer evaluation workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septage hauler")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("photos")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kansas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Kansas guide")));
	}

	@Test
	void dakotaCountyRecordsPageShowsMunicipalAndAsBuiltWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/minnesota/dakota-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dakota County Minnesota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Dakota County septic contacts and compliance-inspection routing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("municipal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("reserve-area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Minnesota records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Minnesota guide")));
	}

	@Test
	void clarkeCountyRecordsPageShowsOnlineRmeAndFoiaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/clarke-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clarke County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Clarke County septic and well records in Online RME")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("unknown")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FOIA")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void calumetCountyRecordsPageShowsAscentAndPermitViewerWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wisconsin/calumet-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Calumet County Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Calumet County sanitary maintenance and permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ascent")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online permit viewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("property-transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wisconsin records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wisconsin guide")));
	}

	@Test
	void kingmanCountyRecordsPageShowsPermitAndSoilWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/kansas/kingman-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kingman County Kansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kingman County wastewater permit application and information")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("unincorporated")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil information")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("planning or zoning")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kansas records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Kansas guide")));
	}

	@Test
	void snohomishCountyRecordsPageShowsAsBuiltAndOnlineRmeWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/snohomish-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Snohomish County Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Snohomish County as-built records and OnlineRME path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Snohomish County Health Department owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built, OnlineRME history, and current-use story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OnlineRME")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washington records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Washington guide")));
	}

	@Test
	void clatsopCountyRecordsPageShowsWebmapsAndAuthorizationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/oregon/clatsop-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clatsop County Oregon Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Clatsop County Webmaps septic-record guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Webmaps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("authorization notice")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Oregon records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Oregon guide")));
	}

	@Test
	void kaneCountyRecordsPageShowsOwnerVsFoiaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/illinois/kane-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kane County Illinois Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request a copy of Kane County septic design")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FOIA")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("current homeowner")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Illinois records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Illinois guide")));
	}

	@Test
	void elkhartCountyRecordsPageShowsLookupAndReuseWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/indiana/elkhart-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Elkhart County Indiana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Submit Elkhart County septic lookup request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("reuse")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("scaled drawing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Indiana records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Indiana guide")));
	}

	@Test
	void sussexCountyRecordsPageShowsPlanSearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/sussex-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sussex County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search online for Sussex County septic plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sussex County's septic systems office owns the practical plan file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sussex plan search, county copy route, and wastewater-management context all point to the same system story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("wastewater-management")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void burlingtonCountyRecordsPageShowsComplianceWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/burlington-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Burlington County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Burlington County public-records route for septic files")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Burlington County Health Department owns the practical wastewater file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file, repair-or-replace history, and compliance-certificate story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("compliance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair-or-replace")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void booneCountyRecordsPageShowsMinorRepairWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/boone-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Boone County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request Boone County permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("minor repair")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lagoon")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void washingtonCountyWisconsinRecordsPageShowsPowtsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wisconsin/washington-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington County Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Washington County POWTS designs and layouts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("POWTS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance reporting")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wisconsin records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wisconsin guide")));
	}

	@Test
	void waukeshaCountyRecordsPageShowsMaintenanceAndTransferWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wisconsin/waukesha-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Waukesha County Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use Waukesha County septic sale, permit, and maintenance workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance notices")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real estate transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wisconsin records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wisconsin guide")));
	}

	@Test
	void stCroixCountyRecordsPageShowsAscentAndMaintenanceWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wisconsin/st-croix-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("St. Croix County Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search St. Croix County sanitary maintenance and permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ascent")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("existing-tank certification")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wisconsin records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wisconsin guide")));
	}

	@Test
	void daneCountyRecordsPageShowsLookupAndAbandonmentWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wisconsin/dane-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dane County Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("View Dane County septic system records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("12-digit parcel number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("abandonment")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wisconsin records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wisconsin guide")));
	}

	@Test
	void buncombeCountyRecordsPageShowsLookupAndExistingSystemWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/buncombe-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buncombe County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County search action board")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buncombe County records and permit guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("buncombe county septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Build exact request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buncombe septic permit search guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buncombe County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic lookup plus any Existing System Inspection and approved-system record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("existing-system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("case number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void chathamCountyRecordsPageShowsScannedPermitAndEsaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/chatham-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Chatham County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to search scanned Chatham septic permits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Chatham County Environmental Health keeps the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("scanned permit archive, the live Existing System Approval path, and any repair lane all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing System Approval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("scanned")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void orangeCountyRecordsPageShowsPre2010AndAuthorizationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/orange-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Orange County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Orange County pre-2010 septic records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Orange County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("old record packet plus the Existing System Authorization and bedroom-count approval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing System Authorization")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pre-2010")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void loudounCountyRecordsPageShowsOnlineRmeWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/loudoun-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Loudoun County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Loudoun County Health Department owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Online RME, scanned records, and any Safe, Adequate, and Proper review all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("scanned records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Online RME")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Safe, Adequate, and Proper")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void fairfaxCountyRecordsPageShowsPlusAndSewerWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/fairfax-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fairfax County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Fairfax environmental health records in PLUS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fairfax County Health Department owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("PLUS file, pump-out history, and sewer branch all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("five-year pump-out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public sewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void princeWilliamCountyRecordsPageShowsDatabaseAndPumpOutWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/prince-william-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Prince William County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Prince William health department septic documents")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Prince William County keeps the practical septic story in the health-district database and land records together")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("health-district file plus the pump-out history and as-built or zoning trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("GPIN")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pump-out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void hamiltonTennesseeCountyRecordsPageShowsPermitRetrievalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/tennessee/hamilton-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Hamilton County TN Septic Inspection Records | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hamilton County septic inspection records and permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hamilton County TN Septic Inspection Records and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hamilton County Septic Information and Forms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("certificate of completion")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("existing septic use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tennessee records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Tennessee guide")));
	}

	@Test
	void hallCountyRecordsPageShowsEvaluationAndPermitGateWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/georgia/hall-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hall County Georgia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hall County septic records and existing-system evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("performance evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("building permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Georgia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Georgia guide")));
	}

	@Test
	void brunswickCountyRecordsPageShowsPermitReportWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/brunswick-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Brunswick County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Brunswick County permit reports and permit search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing System Authorization")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit reports")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void cabarrusCountyRecordsPageShowsExistingSystemApprovalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/cabarrus-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cabarrus County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cabarrus Health Alliance onsite wastewater records and applications")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cabarrus Health Alliance keeps the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("record return, the Existing System Approval path, and any repair or expansion lane all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing System Approval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("swimming pools")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void unionCountyRecordsPageShowsExistingSystemInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/union-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Union County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Union County existing septic and well permit request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Union County Environmental Health keeps the practical file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit record, the compliance-inspection lane, and any O and M or repair trail all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("compliance inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("irrigation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void mecklenburgCountyRecordsPageShowsUseExistingSystemWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/mecklenburg-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mecklenburg County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mecklenburg septic fee schedule and existing-system permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mecklenburg County is a permit-ladder county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("use-existing-system, repair, and plot-plan branches are separated")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use Existing System")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("plot plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void wakeCountyRecordsPageShowsImapsAndAbandonmentWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/wake-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wake County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wake County iMAPS septic permit search guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wake County owns the practical file, but the real first move is the county's iMAPS and Permit Portal record trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wake's iMAPS file, permit branch, and abandonment story all agree")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("iMAPS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Abandonment")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void forsythCountyNorthCarolinaRecordsPageShowsReleaseAndRepairsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/forsyth-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Forsyth County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Forsyth septic owner's guide and permit-copy path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Health Department Release")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair authorization")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void penderCountyRecordsPageShowsRepairAreaAndPortalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/pender-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pender County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request Pender County septic permit information")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("PORT")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void jamesCityCountyRecordsPageShowsMaintenanceWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/james-city-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("James City County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("James City County septic pump-out grant program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("James City County owns the practical septic maintenance file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance record plus the latest pump-out proof and any permit-dependent file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pump-out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void spotsylvaniaCountyRecordsPageShowsPumpOutComplianceWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/spotsylvania-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Spotsylvania County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Spotsylvania County health department septic authority")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("settlement sheet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pump-out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void forsythCountyRecordsPageShowsPoolAndEvaluationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/georgia/forsyth-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Forsyth County Georgia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Forsyth County sewage disposal permits and evaluations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("performance evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pool")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Georgia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Georgia guide")));
	}

	@Test
	void jacksonCountyRecordsPageShowsEvaluationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/georgia/jackson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jackson County Georgia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jackson County existing system evaluation request form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("refinance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("location plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Georgia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Georgia guide")));
	}

	@Test
	void williamsonCountyTennesseeRecordsPageShowsPlanReviewWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/tennessee/williamson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Williamson County Tennessee Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Williamson County electronic plan review information")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("electronic plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("location map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tennessee records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Tennessee guide")));
	}

	@Test
	void blountCountyRecordsPageShowsInspectionLetterWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/tennessee/blount-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Blount County TN Septic Records &amp; SSDS Request | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Blount County TN septic records and SSDS request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use Blount County Environmental Health's SSDS request to pull the septic file.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County search action board")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Blount County records and permit guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("blount county septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Build exact request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Blount County SSDS request form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("loan closings")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tennessee records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Tennessee guide")));
	}

	@Test
	void flatheadCountyRecordsPageShowsResearchFirstWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/montana/flathead-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Flathead County Montana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start the Flathead County land research request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("land research")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Montana records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Montana guide")));
	}

	@Test
	void missoulaCountyRecordsPageShowsFastFactsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/montana/missoula-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Missoula County Montana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missoula County Property Fast Facts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast Facts")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tax ID")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Montana records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Montana guide")));
	}

	@Test
	void gallatinCountyRecordsPageShowsPermitSearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/montana/gallatin-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Gallatin County Montana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Gallatin County wastewater permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No Images")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("COSA")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Montana records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Montana guide")));
	}

	@Test
	void washoeCountyRecordsPageShowsApnAndSewerGateWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/washoe-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washoe County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washoe septic and well records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic-to-sewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void tetonCountyRecordsPageShowsPriorityAreaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/teton-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Teton County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Teton County small wastewater facility permit packet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("priority areas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Professional Engineer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void suffolkCountyRecordsPageShowsRecordsGate() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/suffolk-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Suffolk County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Suffolk septic records instructions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("1973 or later")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("631-852-5700")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void westchesterCountyRecordsPageShowsApprovedFileWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/westchester-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Westchester County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request approved septic system and well records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved bedroom count")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Design Data Sheet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void dutchessCountyRecordsPageShowsApprovalCopyWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/dutchess-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dutchess County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Project review and permit status")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tax map number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pre-construction conference")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void rocklandCountyRecordsPageShowsPortalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/rockland-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rockland County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Rockland self-service permit and records portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("schedule inspections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved permits or plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void albanyCountyRecordsPageShowsFormsAndGrantWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/albany-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Albany County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Albany County septic replacement program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic-system modification form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("likely to fail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void monroeCountyRecordsPageShowsRepairPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/monroe-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Monroe County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Monroe County septic replacement fund")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lake Ontario")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void livingstonCountyRecordsPageShowsPermitFormsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/livingston-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Livingston County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Livingston wastewater permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit to operate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tax-map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void chautauquaCountyRecordsPageShowsTransferGrantWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/chautauqua-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Chautauqua County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Chautauqua OWTS permit packet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("water and sewage survey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Findley Lake")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void wyomingCountyRecordsPageShowsTransferFormsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/wyoming-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming County environmental health forms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-to-operate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void putnamCountyRecordsPageShowsBedroomAndWatershedWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/putnam-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Putnam County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Putnam County single-family septic guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("house-addition")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("watershed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void erieCountyRecordsPageShowsTransferCertificationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/erie-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Erie County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Erie County septic and onsite wastewater guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Property Transfer Certification")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("variance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void tompkinsCountyRecordsPageShowsCertificateWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/tompkins-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tompkins County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tompkins County OWTS permit procedure")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Certificate of Completion")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("seasonal and second homes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void broomeCountyRecordsPageShowsRecordStatusWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/broome-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Broome County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Broome County sewage record search form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("under-designed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("ETU")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void geneseeCountyRecordsPageShowsTransferAndPercWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/genesee-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Genesee County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Genesee County septic construction permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("property-transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement-fund")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void onondagaCountyRecordsPageShowsWatershedRepairWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-york/onondaga-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Onondaga County New York Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Onondaga repair application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tax map number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement-funding")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New York records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New York guide")));
	}

	@Test
	void hamiltonCountyRecordsPageShowsBuyerInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/hamilton-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hamilton County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("View Hamilton County inspection results and permit status")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hamilton County Public Health owns the practical sewage-treatment file once the parcel is really in county jurisdiction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("jurisdiction check plus the buyer-inspection record and inspection cadence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cincinnati, Norwood, or Springdale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("buyer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void clermontCountyRecordsPageShowsTrendReviewWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/clermont-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clermont County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Access Clermont septic inspection history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clermont County Public Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection-history trend plus the operation-permit cadence and any repair-file requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("recurring problems")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel identification number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void summitCountyRecordsPageShowsPointOfSaleWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/summit-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Summit County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Summit County septic records online")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Summit County Public Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online file plus the point-of-sale inspection record and any transfer timing note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("point-of-sale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("off-lot discharge")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void lucasCountyRecordsPageShowsInspectionAndFormsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/lucas-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lucas County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Lucas County HSTS inspection and permit forms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lucas County Public Health owns the practical household-sewage file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("inspection report plus the mortgage or transfer document and design or site-review trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("419-213-4100")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("mortgage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void franklinCountyRecordsPageShowsRealEstateInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/franklin-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Franklin County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Franklin County public-records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Franklin County Public Health owns the practical household-sewage file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real-estate inspection, site-review forms, and public-records return all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real-estate inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site-review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void geaugaCountyRecordsPageShowsForSaleWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/geauga-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Geauga County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Geauga for-sale septic workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Geauga County Public Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("for-sale evaluation plus any pending-requirements or record-update note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("for-sale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("update records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void delawareCountyRecordsPageShowsTransferWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/delaware-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Delaware County public-records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Delaware County Public Health owns the practical household-sewage file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("visible sewage file plus the adjacent-property or permit-transfer paperwork")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Adjacent-property transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void lorainCountyRecordsPageShowsHomeSaleWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/lorain-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lorain County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Lorain home sewage treatment records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("home-sale evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit conditions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void lakeCountyRecordsPageShowsDigitalTransferWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/lake-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lake County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Lake County environmental health digital record portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("point-of-sale evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void hockingCountyRecordsPageShowsPermitSearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/hocking-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hocking County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Hocking County permit history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("map-based permit search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void tuscarawasCountyRecordsPageShowsTransferAndOmWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/tuscarawas-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tuscarawas County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tuscarawas point-of-sale septic application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer with the property")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void portageCountyRecordsPageShowsHistoricalFileWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/portage-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Portage County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Portage point-of-sale inspection workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("historical files")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("voluntary")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void mahoningCountyRecordsPageShowsSaleTestingWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/mahoning-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mahoning County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Mahoning County public-records request path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tested prior to the sale of a home")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("info@mahoninghealth.org")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void clarkCountyRecordsPageShowsTransferAndReplacementAreaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/clark-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clark County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Clark County records request path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clark County Combined Health District owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer inspection, pumping-report trail, and replacement-area story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("being sold or refinanced")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void starkCountyRecordsPageShowsTransferDrawingWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/stark-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Stark County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Stark County transfer inspection guidelines")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Stark County's local health workflow owns the meaningful transfer file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer inspection, waiver status, and sewer-availability branch are all resolved")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("vacant 30 days")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit and system drawings")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void medinaCountyRecordsPageShowsSewerAndOmWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/medina-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Medina County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Medina real-estate sewage evaluation checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Medina County's local health workflow owns the practical file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewer-availability gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("same path, because the septic lane can disappear")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("municipal or sanitary sewer is available")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation and maintenance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void carrollCountyRecordsPageShowsOlderFileResearchWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/carroll-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Carroll County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Submit Carroll County records research request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Carroll County Health Department owns the practical sewage-disposal file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("records-research return, permit-plan story, and any perc or wet-weather testing branch all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("owner name at installation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("wet-weather testing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void worcesterCountyRecordsPageShowsPermitAndDesignWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/worcester-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Worcester County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Worcester County EP permits contact routing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("qualified private-sector designers")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-and-design")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void cuyahogaCountyRecordsPageShowsPointOfSaleAndOmWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/ohio/cuyahoga-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cuyahoga County Ohio Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cuyahoga County public records and sewage downloads")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cuyahoga County Board of Health owns the practical household-sewage file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewer jurisdiction all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("point-of-sale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("O&amp;M")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ohio records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Ohio guide")));
	}

	@Test
	void yumaCountyRecordsPageShowsEtrakitAndPlotPlanWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/yuma-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Yuma County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Yuma County permit information in eTRAKiT")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Yuma County Development Services owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("eTRAKiT permit stack, septic plot plan, and public-records return all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic plot plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public-records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void pitkinCountyRecordsPageShowsParcelNumberAndUsePermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/pitkin-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pitkin County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Pitkin County OWTS records search path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("use-permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void atlanticCountyRecordsPageShowsPermitAndRequestWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/atlantic-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Atlantic County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Atlantic County septic records request path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Atlantic County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file, county notes, and any repair-or-alteration history all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("environmental-health")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void cecilCountyRecordsPageShowsRoutingAndUpgradeWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/cecil-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cecil County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cecil County well and septic permits/forms hub")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cecil County Environmental Health owns the practical permit lane")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file, Water and Sewer Planning route, and any upgrade-program context all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Water and Sewer Planning")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bay Restoration Fund")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void capeMayCountyRecordsPageShowsRecordsRetrievalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/cape-may-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cape May County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cape May County government records request path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cape May County keeps the practical septic story split")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic review file, records-retrieval path, and any outside-agency context all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("records retrieval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pinelands")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void johnstonCountyRecordsPageShowsPermitStatusAndImageWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/johnston-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Johnston County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Check Johnston County septic permit status")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Johnston County Environmental Health owns the practical file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit image, repair path, and pump-inspection trail all support the same system story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit image")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("effluent pump")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void mohaveCountyRecordsPageShowsPlotPlanAndTransferWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/mohave-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mohave County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Mohave County septic plot-plan path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void mesaCountyRecordsPageShowsLocationMapAndHistoricalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/mesa-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mesa County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Mesa County septic resources and location map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("location map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("historical permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void yorkCountyRecordsPageShowsSewerVersusSepticWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/york-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("York County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open York County building permit septic approval requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("100 percent reserve drainfield")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewer or a state health septic path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void harfordCountyRecordsPageShowsAdditionAndReserveAreaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/harford-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Harford County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Harford County well and septic build guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Harford County Health Department owns the practical sewage-disposal file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary permit and reserve-area file plus any certification or upgrade requirement")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("reserve area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary construction permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void cochiseCountyRecordsPageShowsEvaluationAndReserveFieldWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/cochise-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cochise County Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cochise County septic systems permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil and site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("reserve disposal field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Arizona records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Arizona guide")));
	}

	@Test
	void weldCountyRecordsPageShowsLoanApprovalWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/weld-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Weld County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Weld County residential septic path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("loan approval inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("change of use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void douglasCountyRecordsPageShowsPermitMapAndMaintenanceWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/douglas-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Douglas County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Douglas County OWTS permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("four-year")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void kentCountyRecordsPageShowsRealEstateAndChangeOfUseWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/kent-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kent County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kent County septic and well permits path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kent County Health Department owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation, real-estate evaluation, and change-of-use review all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real-estate evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("change-of-use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void geneseeCountyRecordsPageShowsPublicRecordsAndReplacementWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/genesee-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Genesee County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request Genesee County well and septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewer is available")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void oceanCountyRecordsPageShowsOpraAndWastewaterWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/ocean-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ocean County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ocean County OPRA and health records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ocean County's practical septic file is split between health-department routing and the county OPRA lane")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("health file, OPRA return, and wastewater-management context all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("wastewater management")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void monmouthCountyRecordsPageShowsDesignReviewWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/monmouth-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Monmouth County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Monmouth County health department routing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Monmouth County's practical septic file starts with county public health routing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("routed office, design review, and inspection trail all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("design review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("member-town health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void oaklandCountyRecordsPageShowsOnlinePermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/oakland-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oakland County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Oakland County septic permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Oakland County Health Division owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online permit path, any denial appeal branch, and the county help trail all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("perc denial appeal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void macombCountyRecordsPageShowsSoilEvaluationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/macomb-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Macomb County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Macomb County septic permit procedures")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Macomb County Health Department owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil evaluation, portal application trail, and any failing-system complaint all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failing-system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void inghamCountyRecordsPageShowsPointOfSaleWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/ingham-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ingham County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Ingham County permit and inspector path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("point-of-sale")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("viewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void kalamazooCountyRecordsPageShowsChangeOfUseWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/michigan/kalamazoo-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kalamazoo County Michigan Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kalamazoo County sewage applications and evaluations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("change-of-use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sanitary-code")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Michigan records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Michigan guide")));
	}

	@Test
	void adamsCountyRecordsPageShowsUsePermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/adams-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Adams County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Adams County OWTS use permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("use permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void routtCountyRecordsPageShowsPermitClassWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/colorado/routt-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Routt County Colorado Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Routt County OWTS permit procedures")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("major repair")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("City View")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Colorado records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Colorado guide")));
	}

	@Test
	void chesterfieldCountyRecordsPageShowsCertificationLetterWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/chesterfield-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Chesterfield County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Chesterfield Health District FOIA records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("certification letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("FOIA")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void hanoverCountyRecordsPageShowsPumpOutAndMissingFileWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/hanover-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hanover County Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Hanover County well septic and drainfield request form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pre-1986")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pump-out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Virginia records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Virginia guide")));
	}

	@Test
	void gloucesterCountyRecordsPageShowsRealEstateInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/gloucester-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Gloucester County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Gloucester County forms and OPRA septic path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Gloucester County keeps the practical septic story split")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real-estate inspection record plus the OPRA-backed county file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real-estate inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lot block")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void salemCountyRecordsPageShowsTransferInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/salem-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Salem County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Salem County septic records and inspection forms")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Salem County keeps the practical septic story split")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-recognized transfer inspection plus the records-request return")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("state-recognized")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("plan-review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void baltimoreCountyRecordsPageShowsReserveAreaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/baltimore-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Baltimore County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request Baltimore County well and septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Baltimore County Environmental Health keeps the practical file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county record return, the reserve-area trail, and any transfer-side testing all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("reserve-area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tax ID")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void montgomeryCountyRecordsPageShowsRepairAndAbandonmentWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/montgomery-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montgomery County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Montgomery County septic permit and repair process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montgomery County DPS owns the practical well-and-septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montgomery's permit, repair, and abandonment branches all point to the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("abandonment")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public sewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void princeGeorgesCountyRecordsPageShowsMomentumAndAppealWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/prince-georges-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Prince George's County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Prince George's County Momentum and permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Prince George's County DPIE owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("information request plus the Momentum review trail and any appeal-sensitive note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Momentum")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("appeal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void charlesCountyRecordsPageShowsPumpOutAndPermitGuideWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/charles-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Charles County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Charles County septic reimbursement and permit guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Charles County keeps the practical septic story split between planning, permit-guide routing, and health-department system details")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit-guide branch, the reimbursement or Bay Restoration context, and the county health detail all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pump-out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bay Restoration")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void weberCountyRecordsPageShowsFeasibilityWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/weber-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Weber County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Weber-Morgan wastewater permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("feasibility")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("groundwater")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void washingtonUtahCountyRecordsPageShowsMapWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/washington-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Washington County septic inspections map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic-inspections map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void douglasNevadaCountyRecordsPageShowsReplacementFieldWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/douglas-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Douglas County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Douglas County septic information and online permitting path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("replacement leach field")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("public sewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void laramieCountyRecordsPageShowsTransferInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/laramie-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Laramie County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Laramie County small wastewater permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("conveyance-of-property-ownership")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("approved-tank")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void stCharlesCountyRecordsPageShowsTransferEscrowWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/st-charles-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("St. Charles County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open St. Charles County OWTS permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("200 feet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void greeneCountyRecordsPageShowsSoilFactorsWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/greene-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greene County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Greene County onsite wastewater permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil-factors")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-compliant")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void tooeleCountyRecordsPageShowsRecertificationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/tooele-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tooele County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tooele County well and septic re-certification form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("mortgage lenders")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("last five years")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void lyonCountyRecordsPageShowsApnWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/lyon-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lyon County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Lyon County parcel and records search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("APN")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("state-health approval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void johnsonCountyRecordsPageShowsDelegatedPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/johnson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Johnson County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Johnson County onsite wastewater treatment path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("delegated")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("engineer-designed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void uintaCountyRecordsPageShowsPermitPacketWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/uinta-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Uinta County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Uinta County wastewater and septic systems path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("percolation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("before backfill")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void subletteCountyRecordsPageShowsPreConstructionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/sublette-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sublette County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Sublette County septic systems path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pre-construction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("design approval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void jacksonCountyRecordsPageShowsSewerDistrictWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/jackson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jackson County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Jackson County sewer districts and septic brochure path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("demo permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("engineer-sealed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void cacheCountyRecordsPageShowsSensitiveAreaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/cache-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cache County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cache County parcel viewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sensitive areas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic permit or sewer-connection letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void clarkCountyRecordsPageShowsCertificationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/clark-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clark County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Clark County septic certification path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("sewer-unavailability")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("abandonment")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void parkCountyRecordsPageShowsBackfillInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/park-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Park County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Park County small wastewater permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("delegated")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("backfill inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void clayCountyRecordsPageShowsComplaintAndPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/clay-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clay County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Clay County septic application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("work cannot begin")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("complaint investigations")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void richCountyRecordsPageShowsBuildGateWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/rich-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rich County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Rich County building permit instructions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bear River Health")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("survey narrative")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void churchillCountyRecordsPageShowsPercWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/churchill-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Churchill County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Churchill County septic installation requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("two complete percolation tests")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("plot plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void natronaCountyRecordsPageShowsArchiveGapWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/natrona-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Natrona County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Natrona County septic system application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("archived permits are not currently mapped")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("CNCHD Sewer Map")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void franklinCountyRecordsPageShowsComplaintScaleWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/franklin-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Franklin County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Franklin County sewer permit path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Franklin County owns the practical onsite sewage story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file, sewer-district route, and complaint history all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("14500 to 15000 private onsite septic systems")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("complaint")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void boxElderCountyRecordsPageShowsFeasibilityWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/box-elder-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Box Elder County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Box Elder County subdivision and septic rules")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("feasibility letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("300 feet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void storeyCountyRecordsPageShowsPlotWalkWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/storey-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Storey County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Storey County clerk document search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("plot walk")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State of Nevada septic permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void sheridanCountyRecordsPageShowsEnhancedSystemWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/sheridan-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sheridan County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Sheridan County small wastewater permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("enhanced septic systems")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void coleCountyRecordsPageShowsSoilMorphologyWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/cole-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cole County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cole County onsite wastewater permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil morphology")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("registered installer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void ironCountyRecordsPageShowsSewerReceiptWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/iron-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Iron County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Iron County EagleWeb records search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Paid Sewer Hook-up Receipt")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Approved Septic System Permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void carsonCityRecordsPageShowsMandatorySewerWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/carson-city/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Carson City Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Carson City sewer-connection and engineering guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("test trench")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mandatory Sewer Connection Program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void albanyCountyRecordsPageShowsApozWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/albany-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Albany County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Albany County wastewater permit application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Aquifer Protection Overlay Zone")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Authorization to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void christianCountyRecordsPageShowsCertifiedInstallerWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/christian-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Christian County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Christian County Sunshine Law records guidance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-certified")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("six months")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void sanpeteCountyRecordsPageShowsDistrictPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/sanpete-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Sanpete County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Sanpete County recorder records search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Central Utah Public Health")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permitted septic system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void elkoCountyRecordsPageShowsStatePermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/elko-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Elko County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Elko County recorded document search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada State Health")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("incomplete applications are not accepted")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void campbellCountyRecordsPageShowsDelegatedAuthorityWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/campbell-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Campbell County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Campbell County recording and records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("delegated authority")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("all properties")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void taneyCountyRecordsPageShowsAcreageWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/taney-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Taney County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Taney County OWTS forms and permit literature")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("less than 3 acres")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Onsite WW Inspector and Permit Authority")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void sanJuanCountyRecordsPageShowsSewerDistanceWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/san-juan-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Juan County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open San Juan County recorder and parcel records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("1320 feet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("San Juan County Building Department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void humboldtCountyRecordsPageShowsDrilledWellWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/humboldt-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Humboldt County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Humboldt County recorder and assessor records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("well must be drilled prior to obtaining septic system permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("reserve absorption area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void converseCountyRecordsPageShowsHearingWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/converse-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Converse County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Converse County public records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("request a hearing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Public Records Request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void butlerCountyRecordsPageShowsAcreageAndPermitTimingWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/missouri/butler-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Butler County Missouri Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Butler County onsite sewer FAQs")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("less than 3 acres")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("one calendar year")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Missouri records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Missouri guide")));
	}

	@Test
	void wasatchCountyRecordsPageShowsGroundwaterAndSewerWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/wasatch-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wasatch County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wasatch County parcel and tax lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("four (4) feet of the ground surface")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("connect to the sewer system")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void lincolnCountyRecordsPageShowsApnAndSepticDrawingWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/lincoln-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lincoln County Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Lincoln County recorder records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Assessor Parcel Number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Drawing of Septic System if applicable")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Nevada records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Nevada guide")));
	}

	@Test
	void talbotCountyRecordsPageShowsMissingPermitRecordWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/talbot-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Talbot County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Talbot County sanitary construction permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("no County permit record")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("THIS APPLICATION SHALL EXPIRE ONE YEAR FROM THE DATE OF APPROVAL")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void camdenCountyRecordsPageShowsComplianceAndTankInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/camden-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Camden County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Camden County tank inspection requests")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Camden County's Septic and Wells Unit owns the practical wastewater file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("compliance certificate, tank inspection, and county application trail all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("plan review installation repair and compliance certificates")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tank inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void newHanoverCountyRecordsPageShowsInspectionAndAuthorizationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/new-hanover-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hanover County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Hanover County septic permit checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Hanover County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation, permit ladder, and Existing System Inspection all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing System Inspection (Reuse Purpose/Addition)")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Construction Authorization")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void harnettCountyRecordsPageShowsExistingSystemAndAuthorizationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/harnett-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Harnett County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Harnett County residential land use application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing Septic Tank Instructions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Improvement Permit and/or Authorization to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void summitCountyUtahRecordsPageShowsAccessWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/summit-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Summit County Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Summit County GIS and parcel records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("water, sewer/septic and access requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Recorder Surveyor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Utah records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Utah guide")));
	}

	@Test
	void goshenCountyRecordsPageShowsArchiveCutoffWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/goshen-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Goshen County Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Goshen County property search and parcel viewer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("December 31, 2015")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("historical archive")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wyoming records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Wyoming guide")));
	}

	@Test
	void queenAnnesCountyRecordsPageShowsPermitExpiryWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/queen-annes-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Queen Anne")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit portal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit expires 2 years after the date of issue")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mandatory Pump-Out")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void middlesexCountyRecordsPageShowsApprovalAndViolationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/middlesex-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Middlesex County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Middlesex County environmental health division")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Middlesex County Environmental Health keeps the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county approval trail, the inspection file, and any violation history all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county approval")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Notice of Violation and/or a Penalty Assessment")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void onslowCountyRecordsPageShowsExistingAuthorizationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/onslow-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Onslow County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Onslow County septic repair workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Existing System Authorization")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Improvement Permit and Construction Authorization")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void cumberlandCountyRecordsPageShowsExistingTankInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/cumberland-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cumberland County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Cumberland existing septic requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tanks must be checked")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Inspection of Existing Septic Tank for Reuse or Change of Use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void wicomicoCountyRecordsPageShowsTransferAndBrfWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/wicomico-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wicomico County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Wicomico septic applications and checklists")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wicomico County Environmental Health owns the practical sewage-disposal file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("transfer inspection, scaled site plan, and BRF priority story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("real estate transfer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("failing OSDS and holding tanks in the Critical Areas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void pittCountyRecordsPageShowsAuthorizationAndRepairAreaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/pitt-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pitt County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Pitt County onsite sewage disposal workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pitt County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Authorization to Construct, layout record, and repair-area story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Authorization to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("primary drain field and a repair area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void mooreCountyRecordsPageShowsLocatorAndRecertificationWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/moore-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Moore County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Moore County sewage disposal recertification form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Moore County owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("record locator, existing-system approval lane, and recertification window all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("GIS maps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("six months from date of issuance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void alamanceCountyRecordsPageShowsRepairPermitAndInspectionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/alamance-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Alamance County NC Septic Permit Lookup &amp; Records | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alamance County NC septic permit lookup and records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start with Alamance County Environmental Health and pull the latest improvement permit or existing-system inspection.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Alamance County septic application packet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alamance County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permit lane, existing-system inspection, and any repair history all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair permits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("existing septic system inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void carteretCountyRecordsPageShowsRepairAndOperationPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/carteret-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Carteret County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request Carteret septic permit information")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("repair permit questionnaire")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Certificate of Occupancy")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void dareCountyRecordsPageShowsParcelSearchAndJurisdictionSplitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/dare-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dare County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Dare wastewater permits by parcel number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("parcel number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("municipal building permits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void somersetMarylandCountyRecordsPageShowsSeasonalPercAndInterimPermitWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/somerset-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Somerset County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Somerset sewage and water permit form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Somerset County Environmental Health keeps the practical file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit life, the seasonal perc trail, and any BRF or reserve-area path all support the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("highest water table")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("interim permit expires in 24 months")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void somersetNewJerseyCountyRecordsPageShowsLocalHealthDepartmentAndCehaWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/somerset-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Somerset County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Somerset Countywide septic management plan")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Somerset County is split on purpose: the local health department may own the septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("municipal file, CEHA complaint lane, and countywide management context all point to the same story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic system malfunctions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
	}

	@Test
	void cravenCountyRecordsPageShowsGisAndDocumentRequestWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/craven-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Craven County North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Craven GIS septic permits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Craven County Environmental Health owns the practical septic file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("GIS permit trail, document-request fallback, and existing-system branch all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("operation permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Request for Document")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the North Carolina guide")));
	}

	@Test
	void carolineCountyRecordsPageShowsPercStatusAndBrfWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/caroline-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Caroline County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Caroline online records search request form")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Caroline County Health Department owns the practical sewage-disposal file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Property Status Report, Completion Certificate, and BRF priority story all support the same path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("previously perced parcels are not guaranteed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Completion Certificate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void kentCountyRecordsPageShowsLandEvaluationAndBrfWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/kent-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kent County Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Kent on-site sewage system application")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("within 100 feet")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Critical Area")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Maryland records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the Maryland guide")));
	}

	@Test
	void hunterdonCountyRecordsPageShowsEngineerChecklistAndCompletionWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/hunterdon-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hunterdon County New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Transfer or buyer artifact")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Special program or local exception")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Malfunction or repair trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hunterdon County owns the engineered septic review, but the file can still branch through township witness requirements")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hunterdon can look straightforward until one missing approval breaks the occupancy story")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Hunterdon septic permit checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("township witness")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Certificate of Completion")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open New Jersey records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the New Jersey guide")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Perc Test Cost and TDEC Septic Permit Path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Tennessee")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee search path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee county file before the estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate before the permit-file pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open local authority source")));
	}

	@Test
	void southCarolinaStateGuideShowsPermitCopyAndOfficeRoutingContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/south-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina septic permit cost, permit copy, and D-1740 guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Permit Cost, Permit Copy, and D-1740 Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site approvals and permits for all septic systems")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit must be issued before the county can issue a building permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("D-1740")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Permit Lookup &amp; SCDES Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Record proof path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-search-by-address/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina search path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina county file before the estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run the estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void hendersonCountyRecordsPageSeparatesLegacyAndCurrentPermitSearches() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/henderson-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Henderson County North Carolina Septic Records and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Henderson County 2004-present septic permits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("1968 through early 2004")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("1979-1983 destroyed-record gap")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")));
	}

	@Test
	void franklinCountyRecordsPageShowsDatabaseAndOlderFileEscalation() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/north-carolina/franklin-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Franklin County North Carolina Septic Records and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Search Franklin County septic and well permits")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("entries begin in 2004")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("online older-record request path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open North Carolina records lookup")));
	}

	@Test
	void southCarolinaRecordsChecklistUsesActionablePermitCopyLanguage() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/south-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Permit Lookup &amp; SCDES Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Records proof ladder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic permit records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-as-built-records/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SCDES records path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The permit copy already on file for the parcel.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County record pages behind this state workflow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/south-carolina/greenville-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greenville County South Carolina Septic Records and Permit Lookup")));
	}

	@Test
	void greenvilleCountySouthCarolinaRecordsPageShowsScdesRoutingWorkflow() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/south-carolina/greenville-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greenville County South Carolina Septic Records and Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County intent matrix")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greenville County South Carolina septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greenville County South Carolina septic records request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greenville County South Carolina septic permit search by address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greenville County South Carolina septic as-built records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Greenville County South Carolina septic inspection letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a house with a septic system in Greenville County South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"county_intent_matrix_primary\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Capture the parcel anchor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Greenville County real property search for TMS and parcel identity")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use Greenville County real property search to capture the map number")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Action Playbook")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Copy-ready request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Please search for the septic permit copy, D-1740 application or site-review history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buyer move")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Seller move")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Contractor move")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Stop if you see this")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SCDES county or regional septic contact for Greenville County")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Greenville County septic permit copy and records lookup path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("D-1740")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit to Construct")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County workflow structure")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("the Greenville County parcel or TMS anchor")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Real Property Search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("statewide septic routing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open South Carolina records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records request guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records by county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the South Carolina guide")));
	}

	@Test
	void alabamaStateGuideShowsCountyHealthPermitContext() throws Exception {
		mockMvc.perform(get("/septic-system-cost-calculator/alabama/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How much is a perc test in Alabama? Septic permit cost and county records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How Much Is a Perc Test in Alabama? | SepticPath")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How much is a perc test in Alabama? Build a county-usable quote scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama perc-test planning range: $300 to $3,000")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Narrow my Alabama quote scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-alabama-perc-scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health departments")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit to Install")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Approval for Use")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How much is a perc test in Alabama?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in Alabama")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Permit Lookup &amp; County Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Record proof path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-records-request/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official state estimate file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Resolve the file path before a cheap number.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Estimate gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama search path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/perc-test-cost/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/alabama/madison-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Alabama county health file before the estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Check Alabama county records before the estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Alabama records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")));
	}

	@Test
	void montgomeryAlabamaCountyRecordsPageAvoidsRepeatedCountyLabel() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/alabama/montgomery-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Montgomery County Environmental Health office")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Montgomery County county"))));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Permit Cost, Sewage Permit File, and Local Health Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regulatory interpretation and technical assistance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System in West Virginia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("West Virginia Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run the estimate")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Permit Cost, DEM File Search, and Suitability Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("1968 forward")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("historic permit searches")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Inspection Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Rhode Island Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run the estimate")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath Editorial Team")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath Source Review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last reviewed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-03-09\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Georgia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county environmental health office and pull the latest permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Planning cost snapshot")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Replacement midpoint")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cost scope router")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What actually widens Georgia replacement pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Low-end breaker")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County widener")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Stop trusting midpoint when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What keeps widening Georgia replacement scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What to line up before you price replacement scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("50 percent larger")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The first practical check is usually the office, file path, or reviewer identified in this state workflow:")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("That is why this page pairs a planning estimate with official sources, records links, and a local checklist before you move into quote mode.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Open county replacement pages<")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("quote-request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Get matched with local septic pros")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official-source context")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_primary_county_pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_secondary_calculator\""))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_featured_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"state_money_related_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county replacement files usually break down in Georgia")))
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
	void officialSourceClickEventIsStoredWithoutQueryParameters() throws Exception {
		mockMvc.perform(post("/events/nav-click")
						.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
						.content("""
								{
								  "sourcePage": "/septic-records-by-county/",
								  "sourceContext": "county_direct_official_search",
								  "targetPath": "https://tdec.tn.gov/filenetsearch",
								  "targetType": "official_source",
								  "targetLabel": "Open official search"
								}
								"""))
				.andExpect(status().isNoContent());

		try (Stream<Path> eventFiles = Files.walk(Path.of("build/test-storage/events"))) {
			Path eventFile = eventFiles.filter(path -> path.toString().endsWith(".ndjson"))
					.findFirst().orElseThrow(() -> new AssertionError("Expected an event file"));
			String eventContent = Files.readString(eventFile);
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"eventType\":\"official_source_click\""));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"targetPath\":\"https://tdec.tn.gov/filenetsearch\""));
		}
	}

	@Test
	void artifactActionEventIsStoredWithoutFormInputs() throws Exception {
		mockMvc.perform(post("/events/artifact-action")
						.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
						.content("""
								{
								  "sourcePage": "/septic-records-request-builder/",
								  "sourceContext": "records_request_builder",
								  "action": "downloaded",
								  "artifactType": "records_request_packet"
								}
								"""))
				.andExpect(status().isNoContent());

		try (Stream<Path> eventFiles = Files.walk(Path.of("build/test-storage/events"))) {
			Path eventFile = eventFiles.filter(path -> path.toString().endsWith(".ndjson"))
					.findFirst().orElseThrow(() -> new AssertionError("Expected an event file"));
			String eventContent = Files.readString(eventFile);
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"eventType\":\"artifact_action\""));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"artifactType\":\"records_request_packet\""));
			org.junit.jupiter.api.Assertions.assertFalse(eventContent.contains("Property address"));
		}
	}

	@Test
	void offerPrepArtifactActionPreservesSourceKeyWithoutAddressOrRequestText() throws Exception {
		mockMvc.perform(post("/events/artifact-action")
					.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
					.content("""
							{
							  "sourcePage": "/offer-prep-septic-file-check/?src=tn-rural-buyer-guide&utm_medium=resource",
							  "sourceContext": "offer_prep_file_check",
							  "action": "generated",
							  "artifactType": "offer_prep_tool"
							}
							"""))
				.andExpect(status().isNoContent());

		try (Stream<Path> eventFiles = Files.walk(Path.of("build/test-storage/events"))) {
			Path eventFile = eventFiles.filter(path -> path.toString().endsWith(".ndjson"))
					.findFirst().orElseThrow(() -> new AssertionError("Expected an event file"));
			String eventContent = Files.readString(eventFile);
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("src=tn-rural-buyer-guide"));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"artifactType\":\"offer_prep_tool\""));
			org.junit.jupiter.api.Assertions.assertFalse(eventContent.contains("123 Main"));
			org.junit.jupiter.api.Assertions.assertFalse(eventContent.contains("Septic file request before offer"));
		}
	}

	@Test
	void authenticatedEventReportAggregatesActionSignals() throws Exception {
		mockMvc.perform(post("/events/nav-click")
						.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
						.content("""
								{
								  "sourcePage": "/septic-records-by-county/",
								  "sourceContext": "county_direct_official_search",
								  "targetPath": "https://tdec.tn.gov/filenetsearch",
								  "targetType": "official_source",
								  "targetLabel": "Open official search"
								}
								"""))
				.andExpect(status().isNoContent());
		mockMvc.perform(post("/events/artifact-action")
						.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
						.content("""
								{
								  "sourcePage": "/septic-records-request-builder/",
								  "sourceContext": "records_request_builder",
								  "action": "downloaded",
								  "artifactType": "records_request_packet"
								}
								"""))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/ops/event-report/"))
				.andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", org.hamcrest.Matchers.containsString("Basic")));

		mockMvc.perform(get("/ops/event-report/")
						.header("Authorization", opsReportAuthorization()))
				.andExpect(status().isOk())
				.andExpect(header().string("X-Robots-Tag", "noindex, nofollow, noarchive"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Behavior signals, not vanity totals.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official source clicks")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Artifact actions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open official search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("downloaded - records request packet")));
	}

	@Test
	void eventReportSkipsUnreadableHistoricalFiles() throws Exception {
		Path malformedEventFile = TEST_STORAGE_ROOT.resolve("events").resolve("2026").resolve("07").resolve("10.ndjson");
		Files.createDirectories(malformedEventFile.getParent());
		Files.write(malformedEventFile, new byte[] {(byte) 0xC3, (byte) 0x28});

		mockMvc.perform(get("/ops/event-report/")
						.header("Authorization", opsReportAuthorization()))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Some event files could not be read.")));
	}

	private String opsReportAuthorization() {
		OpsReportCredentialsService.OpsReportCredentials credentials = opsReportCredentialsService.credentials();
		String value = credentials.username() + ":" + credentials.password();
		return "Basic " + Base64.getEncoder().encodeToString(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}

	@Test
	void webVitalEventIsStored() throws Exception {
		mockMvc.perform(post("/events/web-vital")
						.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
						.content("""
								{
								  "metricName": "LCP",
								  "value": 2810,
								  "rating": "needs-improvement",
								  "sourcePage": "/septic-records-checklist/tennessee/",
								  "navigationType": "navigate"
								}
								""")
						.header("User-Agent", "MockBrowser/1.0")
						.header("Referer", "https://example.test/septic-records-checklist/tennessee/"))
				.andExpect(status().isNoContent());

		try (Stream<Path> eventFiles = Files.walk(Path.of("build/test-storage/events"))) {
			Path eventFile = eventFiles
					.filter(path -> path.toString().endsWith(".ndjson"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one event NDJSON file"));
			String eventContent = Files.readString(eventFile);
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"eventType\":\"web_vital\""));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"metricName\":\"LCP\""));
			org.junit.jupiter.api.Assertions.assertTrue(eventContent.contains("\"sourcePage\":\"/septic-records-checklist/tennessee/\""));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county diligence pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county due diligence usually breaks down in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common buyer or transfer artifact")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common special program or exception")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county buyer artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not treat this as a routine deal yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/sussex-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Any service contract, maintenance agreement, or board-of-health notice tied to advanced treatment or special-area oversight.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("FAQPage"))))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://healthweb-back.health.ny.gov/environmental/docs/cehdir.pdf")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/new-york/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/montana/flathead-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/montana/missoula-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/montana/gallatin-county/")))
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
	void nevadaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/nevada/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nevada Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Southern Nevada Health District")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file, inspection note, and as-built plans")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/washoe-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/lyon-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/douglas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/clark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/churchill-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/storey-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/carson-city/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/elko-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/humboldt-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/nevada/lincoln-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NV&projectType=buying_home")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Tennessee Septic Records by County | Permit Files | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/septic-records-checklist/\">Septic Records Lookup</a>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"Septic Records Lookup\",\"item\":\"https://example.test/septic-records-checklist/\",\"position\":2")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Records by County")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Find Tennessee septic records by choosing the county")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tennessee official records source")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("state_money_primary_tennessee_records_source")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("TDEC SSDS record search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("TDEC septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tdec septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State of TN septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official records route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee records lookup guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tennessee county septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("tdec septic permit records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Blount County septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hamilton County septic inspection records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not make county-known searches restart.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("state_records_response_county_handoff")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit file and inspection letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("regional-contact and repair-permit friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"gsc_intent_patch\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Records proof ladder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open address search guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county records guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official state file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Owner, artifact, request, fallback")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Tennessee official records source")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No-record fallback")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Address clue")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Blount County records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open Hamilton County records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/hamilton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/williamson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TN&projectType=buying_home")));
	}

	@Test
	void vermontInspectionCostPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-cost/vermont/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/septic-system-cost-calculator/vermont/\">Vermont Septic Guide</a>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"name\":\"Vermont Septic Guide\",\"item\":\"https://example.test/septic-system-cost-calculator/vermont/\",\"position\":2")))
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
	void wyomingRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wyoming/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wyoming Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permit, inspection, and perc file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("engineer-design friction")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/teton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/johnson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/uinta-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/sublette-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/laramie-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/park-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/natrona-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/sheridan-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/albany-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/campbell-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/converse-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wyoming/goshen-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WY&projectType=buying_home")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county replacement pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Replacement Summary")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county replacement files usually break down in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county replacement artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not price replacement scope yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router for North Carolina replacement pricing")))
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
		mockMvc.perform(get("/drain-field-replacement-cost/colorado/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("index,follow")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("state_money_next_best_quote"))));
	}

	@Test
	void statePumpingMoneyPageRenders() throws Exception {
		mockMvc.perform(get("/septic-pumping-cost/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Pumping Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county maintenance pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Maintenance Summary")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county maintenance files usually break down in Washington")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county maintenance artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not price maintenance scope yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router for Washington pumping and maintenance pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What actually widens Washington pumping and maintenance pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What keeps widening Washington maintenance scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What to line up before you price maintenance scope")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run an inspection-scope estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What records should you gather before a septic inspection?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=inspection")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the full state guide directory")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Septic Records Lookup by State<")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(">Buying a House With a Septic System<")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("This page exists to support high-intent search"))));
	}

	@Test
	void permitProcessContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Permit Process by State")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to use this page before you ask for quotes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jump between sections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open state permit pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a permit-path estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#state-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=new_install")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_state_specific\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most homeowners get stuck because permit sounds like one step")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What the live state pages already resolve")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("source-backed state workflow pages across")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County-backed coverage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-first follow-up before pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the full state guide directory")));
	}

	@Test
	void percTestCostContentPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Perc Test Cost: $300-$3,000 Range and State Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("$300 to $3,000 is the starting range")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ask what the quote includes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<dd>2026-07-20</dd>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How much does a perc test cost?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Is a perc test the same as a percolation test or a perk test?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open state perc pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a site-risk estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#state-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=perc_test")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/perc-test-cost/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/perc-test-cost/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/perc-test-cost/alabama/"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/failed-perc-test-septic/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("quoteMode=true#quote-request"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/"))));
	}

	@Test
	void permitLookupContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-lookup/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Permit Lookup by State")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Permit Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open state permit lookup pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a lookup-aware estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#state-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County lookup launchpad")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Start with the county file when the search is already specific.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/how-to-find-septic-records-online/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee Septic Records by County")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/davidson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/south-carolina/greenville-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/durham-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/iredell-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/comal-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/indiana/grant-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/montgomery-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/fort-bend-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/alabama/shelby-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/indiana/st-joseph-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county-first follow-up before pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_state_specific\"")));
	}

	@Test
	void howToFindSepticRecordsOnlineContentPageRenders() throws Exception {
		mockMvc.perform(get("/how-to-find-septic-records-online/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How to Find Septic Records Online")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>How to Find Septic Records Online")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records lookup pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Finding septic records online is usually a routing problem.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Next-click accelerator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/official-septic-lookup-tools/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-request-builder/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/dhec-septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What if the online septic search finds nothing?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/durham-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/comal-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-lookup/")));
	}

	@Test
	void septicRecordsByCountyContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-by-county/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Records by County")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Records by County")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county records pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic records availability index")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Direct official searches")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open official search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://tdec.tn.gov/filenetsearch")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://public.cdpehs.com/NCENVPBLo/OSW_PROPERTY/ShowOSW_PROPERTYTablePage.aspx?ESTTST_CTY=C35")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Next-click accelerator")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official content file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("File owner")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Blount County septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buncombe County septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Comal County septic permit search")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("High-confidence routes loaded")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First pull:")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County lookup launchpad")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/san-bernardino-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/cape-may-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-search-by-address/")));
	}

	@Test
	void septicPermitSearchByAddressContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-search-by-address/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Permit Search by Address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Permit Search by Address")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Find my county records route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Compare bedrooms to the septic permit")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A septic permit search by address is strongest")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-address-record-finder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Address-to-record relay")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official content file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lookup clue")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("If the address search stalls, move to parcel, official tools, or a copy-ready request.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/official-septic-lookup-tools/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-request-builder/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-by-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/comal-county/")));
	}

	@Test
	void septicPermitRecordsRequestContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-records-request/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Permit Records Request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Permit Records Request")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open records request routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A septic permit records request should be specific")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official content file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No-record fallback")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Turn the records request into a precise file pull before the office answers vaguely.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-request-builder/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-as-built-records/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/durham-county/")));
	}

	@Test
	void septicAsBuiltRecordsContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-as-built-records/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic As-Built Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic As-Built Records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open as-built record routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic as-built records matter because they show where the system was actually installed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Official content file path")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("installed layout")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("If the layout is missing, request the exact as-built, final approval, or no-record response.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/dhec-septic-permit-lookup/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-replacement-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/comal-county/")));
	}

	@Test
	void septicInspectionLetterContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-inspection-letter/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Inspection Letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Inspection Letter")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open inspection-letter routes")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A septic inspection letter is not the same thing as a generic records lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/blount-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/")));
	}

	@Test
	void recordsChecklistContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Records Lookup by State")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Records Lookup by State")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What this page is really helping you decide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Records change the estimate because they change what you can safely assume.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("one missing as-built or permit can matter more than several contractor opinions")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Representative state examples behind this national page")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What this national page can answer before you touch a quote")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("When this page stops being enough")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What usually kills the low end")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Fast next steps")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Jump between sections")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open state records lookup pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a records-aware estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#state-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=buying_home")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 6 source-backed state-specific pages, the county workflow network underneath them, and the source policy.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County-backed coverage")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath Editorial Team")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath Source Review")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Last reviewed")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"dateModified\":\"2026-07-09\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("\"editor\":{\"@type\":\"Organization\",\"name\":\"SepticPath Source Review\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this page is sourced")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("State-specific pages carry the official sources behind this national overview.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reviewed against 3 official sources tied to the Connecticut workflow.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Trust: high")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_link\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-track-source-context=\"content_page_featured_state_specific\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-inspection-cost/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the full state guide directory")));
	}

	@Test
	void transferComplianceContentPageRenders() throws Exception {
		mockMvc.perform(get("/septic-transfer-compliance/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Transfer Compliance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Septic Transfer Compliance")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Transfer compliance guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open state transfer pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a transfer-risk estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/alabama/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/indiana/floyd-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/dekalb-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/alabama/madison-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=buying_home")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Georgia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("DeKalb County Georgia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Madison County Alabama Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the full state guide directory")));
	}

	@Test
	void buyerContentPageRenders() throws Exception {
		mockMvc.perform(get("/buying-a-house-with-a-septic-system/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Buying a House With a Septic System")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A septic home purchase usually turns on the file story before it turns on the repair number.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The real buyer question is not just whether the house has septic. It is whether the septic story survives diligence once you test the permit file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What septic records should a buyer ask for?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a buyer due-diligence estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=buying_home")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county permit paths usually break down in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county permit artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not schedule permit pricing yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/hunterdon-county/")))
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
	void marylandRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/maryland/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Maryland Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Maryland")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county files usually break down in Maryland")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common file owner pattern")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common permit closeout signal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common buyer or transfer artifact")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common special program or exception")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common malfunction or repair trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common quote gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Drop to a county page when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not quote yet when")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Live triage")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Use the file trail before you trust the story.")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Record owner")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Evidence to pull")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Pricing gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router for Maryland records work")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Resolve first")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Escalate to county when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hold pricing when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Coverage:")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("PTI-backed transfer report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/howard-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/garrett-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/st-marys-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/anne-arundel-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/frederick-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/dorchester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/carroll-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/worcester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/cecil-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/harford-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/baltimore-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/montgomery-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/prince-georges-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/charles-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/talbot-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/queen-annes-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/wicomico-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/somerset-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/caroline-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/maryland/kent-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("FAQPage"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MD&projectType=buying_home")));
	}

	@Test
	void washingtonRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/washington/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Washington Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built drawing or approved design")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("as-built drawing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/washington/king-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/washington/whatcom-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/washington/clark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/washington/thurston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/washington/snohomish-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/hall-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/forsyth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/georgia/jackson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=GA&projectType=buying_home")));
	}

	@Test
	void virginiaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/loudoun-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/james-city-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/spotsylvania-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/fairfax-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/clarke-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/prince-william-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/york-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/chesterfield-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/virginia/hanover-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=VA&projectType=buying_home")));
	}

	@Test
	void newJerseyRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/new-jersey/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New Jersey Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county files usually break down in New Jersey")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common file owner pattern")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common quote gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Drop to a county page when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not quote yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Coverage:")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("service agreement")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/sussex-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/burlington-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/atlantic-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/cape-may-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/ocean-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/monmouth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/gloucester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/salem-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/camden-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/middlesex-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/somerset-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-jersey/hunterdon-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("FAQPage"))))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/placer-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/el-dorado-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/trinity-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/sonoma-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/napa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/ventura-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/marin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/santa-cruz-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/san-luis-obispo-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/tuolumne-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/riverside-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/san-bernardino-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/san-diego-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/santa-clara-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/california/monterey-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://www.waterboards.ca.gov/water_issues/programs/owts/lamp_contact.html")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/larimer-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/jefferson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/boulder-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/el-paso-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/pitkin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/mesa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/weld-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/douglas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/adams-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/colorado/routt-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/travis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/bexar-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/williamson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/harris-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/collin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/tarrant-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/denton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/hays-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://www.tceq.texas.gov/permitting/ossf/on-site-activity-reporting-system")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/washtenaw-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/ottawa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/livingston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/kent-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/genesee-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/oakland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/macomb-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/ingham-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/michigan/kalamazoo-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/illinois/mchenry-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/illinois/lake-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/illinois/kane-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/cayuga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/seneca-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/allegany-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/suffolk-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/westchester-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/dutchess-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/rockland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/albany-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/monroe-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/livingston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/chautauqua-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/wyoming-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/putnam-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/erie-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/tompkins-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/broome-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/genesee-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/onondaga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/madison-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/cortland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/tioga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("NYSDOH Field Offices and Local Health Departments")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://healthweb-back.health.ny.gov/environmental/docs/cehdir.pdf")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/hamilton-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/clermont-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/summit-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/lucas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/franklin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/geauga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/delaware-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/lorain-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/lake-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/hocking-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/tuscarawas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/portage-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/mahoning-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/clark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/stark-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/medina-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/cuyahoga-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://ohioepa.custhelp.com/app/answers/detail/a_id/367/~/information-about-household-sewage-treatment-systems")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county permit pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county permit paths usually break down in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common permit closeout signal")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common malfunction or repair trail")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Most common quote gate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county permit artifacts to pull")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not schedule permit pricing yet when")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Live triage")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Find the permit desk before pricing the work.")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Permit authority")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Evidence to pull")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Pricing gate")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router for Texas permit work")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Resolve first")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Escalate to county when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hold pricing when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/texas/travis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("OARS")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("licensed site evaluator or professional engineer")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("FAQPage"))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=TX&projectType=new_install")));
	}

	@Test
	void virginiaPermitProcessPageRenders() throws Exception {
		mockMvc.perform(get("/septic-permit-process/virginia/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Virginia Septic Permit Process")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Virginia Septic Permit Process")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county diligence pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county due diligence usually breaks down in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county buyer artifacts to pull")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not treat this as a routine deal yet when")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Live triage")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Resolve the buyer file before negotiating price.")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Buyer file")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Evidence to pull")))
						.andExpect(content().string(org.hamcrest.Matchers.containsString("Pricing gate")))
					.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router for Ohio buyer diligence")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Resolve first")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Escalate to county when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Hold pricing when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/ohio/hamilton-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county inspection pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Inspection Summary")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county inspection files usually break down in Ohio")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county inspection artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not price inspection scope yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router for Ohio inspection pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What actually widens Ohio inspection pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What keeps widening Ohio inspection scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What to line up before you price inspection scope")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Permit Requirements, D-1740, and Permit Copy Guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in South Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("permit copy")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("D-1740")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("site evaluation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SCDES")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SCDES county or regional contact")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The permit copy already on file for the parcel.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Tanks - Who to Call")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("https://des.sc.gov/permits-regulations/septic-tanks/septic-tanks-who-call")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/south-carolina/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/boone-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/jackson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/st-charles-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/greene-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/clay-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/franklin-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/cole-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/christian-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/taney-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/missouri/butler-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MO&projectType=buying_home")));
	}

	@Test
	void minnesotaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/minnesota/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Minnesota Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local SSTS program")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("prior compliance-inspection report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/minnesota/olmsted-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/minnesota/st-louis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/minnesota/chisago-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/minnesota/blue-earth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/minnesota/dakota-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=MN&projectType=buying_home")));
	}

	@Test
	void wisconsinRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/wisconsin/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Wisconsin Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("maintenance-tracking history")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("POWTS inspection report")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wisconsin/kenosha-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wisconsin/washington-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wisconsin/waukesha-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wisconsin/st-croix-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wisconsin/calumet-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/wisconsin/dane-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=WI&projectType=buying_home")));
	}

	@Test
	void kansasRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/kansas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Kansas Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil-profile and sanitary-code file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local sanitary-code variation")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/kansas/johnson-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/kansas/sedgwick-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/kansas/pottawatomie-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/kansas/ellis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/kansas/kingman-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=KS&projectType=buying_home")));
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>North Carolina Septic Permit Lookup by County | SepticPath</title>")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Septic Permit Lookup by County")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Bring this into the next")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county files usually break down in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Drop to a county page when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not quote yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Coverage:")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county health department")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("The county health department file reference for the property.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina records lookup guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("alamance county septic records")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("buncombe county septic permit lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("state_records_response_county_handoff")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/buncombe-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/chatham-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/orange-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/brunswick-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/cabarrus-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/union-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/mecklenburg-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/wake-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/forsyth-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/pender-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/johnston-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/new-hanover-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/harnett-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/onslow-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/cumberland-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/pitt-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/moore-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/alamance-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/carteret-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/dare-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/craven-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=NC&projectType=buying_home")));
	}

	@Test
	void texasPercPageRenders() throws Exception {
		mockMvc.perform(get("/perc-test-cost/texas/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Texas Perc Test Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county site-review pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("County Site-Review Summary")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county site-review files usually break down in Texas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county site-review artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not price site-review scope yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Decision router for Texas perc and site-review pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What actually widens Texas site-review pricing")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What keeps widening Texas site-review scope")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("What to line up before you price site-review scope")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county permit pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county permit paths usually break down in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county permit artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not schedule permit pricing yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/wake-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county diligence pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How this workflow usually unfolds in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How county due diligence usually breaks down in North Carolina")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("First county buyer artifacts to pull")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Do not treat this as a routine deal yet when")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/carteret-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Indiana Septic Records Lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county or local health office")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("county permit and site file")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Records proof ladder")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana records lookup guide")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("how to find septic tank records online free")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("septic system lookup")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How-to-find records and system-lookup searches")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-records-request/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#county-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/indiana/elkhart-county/")))
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
	void arizonaRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/arizona/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Arizona Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/pima-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/yavapai-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/maricopa-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/coconino-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/pinal-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/santa-cruz-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/yuma-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/mohave-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/arizona/cochise-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?state=AZ&projectType=buying_home")));
	}

	@Test
	void utahRecordsChecklistPageRenders() throws Exception {
		mockMvc.perform(get("/septic-records-checklist/utah/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Utah Septic Records Checklist")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local health department or district engineer")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("soil log and percolation test results")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open county record lookup paths")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/davis-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/utah-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/tooele-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/cache-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/rich-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/box-elder-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/iron-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/sanpete-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/san-juan-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/wasatch-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/summit-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/weber-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/utah/washington-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("local-LHJ control")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/oregon/clackamas-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/oregon/deschutes-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/oregon/washington-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/oregon/lane-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/oregon/clatsop-county/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Drain Field Replacement Cost")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("A field problem stops being local trench work the moment the replacement footprint is uncertain or the contractor is no longer sure the tank, distribution box, or dosing setup can stay.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open state drain field pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a drain field replacement estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#state-pages\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("How much does drain field replacement cost?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Is a leach field the same as a drain field?")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open state failed-perc pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Run a failed-perc estimate")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"#state-pages\"")))
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
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-system-cost-calculator/?projectType=drainfield_replacement&amp;sourcePageHint=/septic-replacement-area/&amp;quoteMode=true#quote-request")));
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
				{"/septic-records-checklist/alabama/", "/drain-field-replacement-cost/alabama/"},
				{"/septic-inspection-cost/alabama/", "/drain-field-replacement-cost/alabama/"},
				{"/buying-a-house-with-a-septic-system/alabama/", "/drain-field-replacement-cost/alabama/"},
				{"/septic-records-checklist/georgia/", "/drain-field-replacement-cost/georgia/"},
				{"/septic-records-checklist/georgia/", "/failed-perc-test-septic/georgia/"},
				{"/septic-inspection-cost/georgia/", "/wet-yard-over-septic-drain-field/georgia/"},
				{"/buying-a-house-with-a-septic-system/georgia/", "/septic-replacement-area/georgia/"},
				{"/septic-records-checklist/indiana/", "/drain-field-replacement-cost/indiana/"},
				{"/septic-inspection-cost/indiana/", "/drain-field-replacement-cost/indiana/"},
				{"/buying-a-house-with-a-septic-system/indiana/", "/drain-field-replacement-cost/indiana/"},
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
				{"/buying-a-house-with-a-septic-system/florida/", "/septic-replacement-area/florida/"},
				{"/septic-records-checklist/south-carolina/", "/drain-field-replacement-cost/south-carolina/"},
				{"/septic-inspection-cost/south-carolina/", "/drain-field-replacement-cost/south-carolina/"},
				{"/buying-a-house-with-a-septic-system/south-carolina/", "/drain-field-replacement-cost/south-carolina/"},
				{"/septic-records-checklist/rhode-island/", "/drain-field-replacement-cost/rhode-island/"},
				{"/septic-inspection-cost/rhode-island/", "/drain-field-replacement-cost/rhode-island/"},
				{"/buying-a-house-with-a-septic-system/rhode-island/", "/drain-field-replacement-cost/rhode-island/"}
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
	void drainfieldStatePagesLinkBackIntoWorkflowCluster() throws Exception {
		String[][] expectations = {
				{"/drain-field-replacement-cost/georgia/", "/septic-records-checklist/georgia/"},
				{"/drain-field-replacement-cost/georgia/", "/septic-permit-process/georgia/"},
				{"/drain-field-replacement-cost/georgia/", "/septic-inspection-cost/georgia/"},
				{"/wet-yard-over-septic-drain-field/florida/", "/septic-records-checklist/florida/"},
				{"/wet-yard-over-septic-drain-field/florida/", "/septic-permit-process/florida/"},
				{"/wet-yard-over-septic-drain-field/florida/", "/septic-inspection-cost/florida/"},
				{"/septic-replacement-area/connecticut/", "/septic-records-checklist/connecticut/"},
				{"/septic-replacement-area/connecticut/", "/septic-permit-process/connecticut/"},
				{"/septic-replacement-area/connecticut/", "/septic-inspection-cost/connecticut/"}
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
						.param("sourcePageHint", "/septic-replacement-cost/")
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

		Path leadsRoot = TEST_STORAGE_ROOT.resolve("leads");
		Path eventsRoot = TEST_STORAGE_ROOT.resolve("events");
		Path pendingExportsRoot = TEST_STORAGE_ROOT.resolve("exports").resolve("pending");
		Path dailyExportsRoot = TEST_STORAGE_ROOT.resolve("exports").resolve("daily");

		try (Stream<Path> leadFiles = Files.walk(TEST_STORAGE_ROOT)) {
			Path leadJson = leadFiles
					.filter(path -> path.startsWith(leadsRoot))
					.filter(path -> path.toString().endsWith(".json"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one stored lead file"));
			String leadContent = Files.readString(leadJson);
			org.junit.jupiter.api.Assertions.assertTrue(leadContent.contains("\"sourcePage\" : \"/septic-replacement-cost/\""));
			org.junit.jupiter.api.Assertions.assertTrue(leadContent.contains("\"sourcePageHint\" : \"/septic-replacement-cost/\""));
		}

		try (Stream<Path> eventFiles = Files.walk(TEST_STORAGE_ROOT)) {
			org.junit.jupiter.api.Assertions.assertTrue(
					eventFiles
							.filter(path -> path.startsWith(eventsRoot))
							.anyMatch(path -> path.toString().endsWith(".ndjson")),
					"Expected at least one stored event file"
			);
		}

		Path exportJson;
		try (Stream<Path> exportFiles = Files.walk(TEST_STORAGE_ROOT)) {
			exportJson = exportFiles
					.filter(path -> path.startsWith(pendingExportsRoot))
					.filter(path -> path.toString().endsWith(".json"))
					.findFirst()
					.orElseThrow(() -> new AssertionError("Expected at least one export JSON file"));
		}
		String exportContent = Files.readString(exportJson);
		org.junit.jupiter.api.Assertions.assertTrue(exportContent.contains("\"exportStatus\" : \"pending_routing\""));
		org.junit.jupiter.api.Assertions.assertTrue(exportContent.contains("\"consentLanguageVersion\" : \"2026-03-09-v1\""));
		org.junit.jupiter.api.Assertions.assertTrue(exportContent.contains("\"userAgent\" : \"MockBrowser/1.0\""));
		org.junit.jupiter.api.Assertions.assertTrue(exportContent.contains("\"sourcePage\" : \"/septic-replacement-cost/\""));

		Path exportCsv;
		try (Stream<Path> exportCsvFiles = Files.walk(TEST_STORAGE_ROOT)) {
			exportCsv = exportCsvFiles
					.filter(path -> path.startsWith(dailyExportsRoot))
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
	void quoteSubmissionValidationShowsErrorForMissingRequiredFields() throws Exception {
		mockMvc.perform(post("/quote-request/")
						.param("stateCode", "GA")
						.param("projectType", "replacement")
						.param("bedrooms", "4")
						.param("soilPercStatus", "poor_drainage")
						.param("accessDifficulty", "hard")
						.param("timeline", "this_month")
						.param("zipCode", "30301"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Finish the required fields")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Name, email, phone, ZIP, and consent are required before this lead can be stored.")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Request received"))));
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
	void genericNotFoundPageIsBrandedHtmlAndNoindex() throws Exception {
		mockMvc.perform(get("/not-a-real-route-for-audit/"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(org.springframework.http.MediaType.TEXT_HTML))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("SepticPath")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,nofollow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Closest next pages")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/")));
	}

	@Test
	void drainfieldLikeNotFoundPathShowsIntentAwareRecoveryLinks() throws Exception {
		mockMvc.perform(get("/wet-yard-over-septic-drain-field/not-a-real-state/"))
				.andExpect(status().isNotFound())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/wet-yard-over-septic-drain-field/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-replacement-area/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/drain-field-estimator/")));
	}

	@Test
	void indianaRecordsPacketIsNoindexAndPinsCountyChain() throws Exception {
		mockMvc.perform(get("/for-professionals/records-packet/indiana/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Indiana septic records packet for buyer agents and coordinators")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,follow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/indiana/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/indiana/howard-county/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Share-ready note")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Copy or download this handoff note.")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("data-packet-note-download")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Open the pinned workflow page")));
	}

	@Test
	void newYorkBuyerPacketIsNoindexAndPinsBuyerToRecordsChain() throws Exception {
		mockMvc.perform(get("/for-professionals/buyer-diligence-packet/new-york/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("New York septic buyer diligence packet for agents and coordinators")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,follow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/buying-a-house-with-a-septic-system/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/new-york/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Appendix 75-A")));
	}

	@Test
	void southCarolinaPermitPacketIsNoindexAndPinsPermitChain() throws Exception {
		mockMvc.perform(get("/for-professionals/permit-prep-packet/south-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina septic permit prep packet for installers and coordinators")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,follow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-permit-process/south-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("D-1740")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("South Carolina Septic Guide")));
	}

	@Test
	void tennesseeInspectionLetterPacketIsNoindexAndPinsTransactionFileChain() throws Exception {
		mockMvc.perform(get("/for-professionals/inspection-letter-packet/tennessee/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Tennessee septic inspection-letter packet for buyer agents and lenders")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,follow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/tennessee/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("contract-county route")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Septic Inspection Letter")));
	}

	@Test
	void northCarolinaListingPermitPacketIsNoindexAndPinsBedroomCheckToCountyFile() throws Exception {
		mockMvc.perform(get("/for-professionals/listing-permit-packet/north-carolina/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina septic listing-permit packet for brokers and coordinators")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("noindex,follow")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-bedroom-permit-checker/?state=NC")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("/septic-records-checklist/north-carolina/")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("North Carolina Real Estate Commission")));
	}

	@Test
	void workflowPacketsDoNotAppearInSitemap() throws Exception {
		mockMvc.perform(get("/sitemap.xml"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("for-professionals/records-packet/indiana"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("for-professionals/buyer-diligence-packet/new-york"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("for-professionals/inspection-letter-packet/tennessee"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("for-professionals/listing-permit-packet/north-carolina"))))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("for-professionals/permit-prep-packet/south-carolina"))));
	}

	private void assertStateMoneyPageRenders(String path, String title, String anchorText, String calculatorPath) throws Exception {
		mockMvc.perform(get(path))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString(title)))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Who this page is for")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(anchorText)))
				.andExpect(content().string(org.hamcrest.Matchers.containsString(calculatorPath)));
	}

	private boolean indexable(String contentSlug, String stateSlug) {
		StateMoneyPage page = researchDataService.findPublicStateMoneyPage(contentSlug, stateSlug).orElseThrow();
		StateProfile state = researchDataService.findStateByCode(page.stateCode()).orElseThrow();
		return publishingPolicyService.isIndexableStateMoneyPage(page, state);
	}

}

