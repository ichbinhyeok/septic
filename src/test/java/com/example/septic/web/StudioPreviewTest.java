package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"app.studio-preview.enabled=true", "app.storage.root=./build/test-storage"})
@AutoConfigureMockMvc
class StudioPreviewTest {
    @Autowired MockMvc mvc;
    @Autowired com.example.septic.service.ResearchDataService research;

    @Test void recoveryPreviewsStayNoindexAndOfferUsefulRoutes() throws Exception {
        for (String kind : java.util.List.of("not-found", "server")) {
            mvc.perform(get("/design-preview/studio/recovery/" + kind + "/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("studio/recovery"))
                    .andExpect(content().string(containsString("noindex,nofollow")))
                    .andExpect(content().string(containsString("Open the records workspace")))
                    .andExpect(content().string(containsString("Ask us to research it")));
        }
        mvc.perform(get("/design-preview/studio/recovery/unknown/"))
                .andExpect(status().isNotFound());
    }

    @Test void privateTransactionPreviewsAreNoindexAndDoNotCreatePaymentControls() throws Exception {
        mvc.perform(get("/design-preview/studio/checkout/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/checkout"))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("Your reviewed property record is ready.")))
                .andExpect(content().string(not(containsString("paypal.com"))));
        mvc.perform(get("/design-preview/studio/delivery/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/delivery"))
                .andExpect(content().string(containsString("Your record package is ready.")))
                .andExpect(content().string(containsString("3 of 3")));
    }

    @Test void policiesPreserveEveryParagraphAndPublicationAnchor() throws Exception {
        for (var slug : java.util.List.of("privacy-policy", "terms-of-use")) {
            var original = mvc.perform(get("/" + slug + "/")).andReturn().getModelAndView().getModel();
            var response = mvc.perform(get("/design-preview/studio/policies/" + slug + "/"))
                    .andExpect(status().isOk()).andExpect(view().name("studio/policy")).andReturn();
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(response.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
            org.assertj.core.api.Assertions.assertThat(html).contains("noindex,nofollow", (String) original.get("heading"), (String) original.get("intro"), (String) original.get("calloutBody"));
            for (var section : (java.util.List<SitePageSection>) original.get("sections")) {
                org.assertj.core.api.Assertions.assertThat(html).contains(section.title(), section.body());
                for (var bullet : section.bullets()) org.assertj.core.api.Assertions.assertThat(html).contains(bullet);
            }
            if (slug.equals("privacy-policy")) org.assertj.core.api.Assertions.assertThat(html).contains("id=\"research-publication\"");
        }
        mvc.perform(get("/design-preview/studio/policies/unknown/")).andExpect(status().isNotFound());
    }

    @Test void methodologyRetainsSourceStandardsAndCoverage() throws Exception {
        var original = mvc.perform(get("/methodology/")).andReturn().getModelAndView().getModel();
        var source = (TrustOperationsPageView) original.get("operationsPage");
        String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get("/design-preview/studio/methodology/"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        org.assertj.core.api.Assertions.assertThat(html).contains(source.heading(), source.intro(), source.ctaBody(), "noindex,nofollow");
        for (var lane : source.lanes()) org.assertj.core.api.Assertions.assertThat(html).contains(lane.title(), lane.body());
        for (var row : source.coverageRows()) org.assertj.core.api.Assertions.assertThat(html).contains(row.stateName(), row.sourceCountLabel(), row.lastVerifiedAt());
    }

    @Test void topicGuidesRetainEditorialAndSourceDataWithoutLegacyPresentation() throws Exception {
        for (String slug : java.util.List.of("septic-permit-process", "septic-inspection-cost", "septic-as-built-records",
                "septic-permit-search-by-address", "septic-permit-records-request", "septic-inspection-letter",
                "how-to-find-septic-records-online", "septic-tank-location-records", "septic-replacement-cost",
                "perc-test-cost", "drain-field-replacement-cost", "failed-perc-test-septic",
                "septic-replacement-area", "wet-yard-over-septic-drain-field")) {
            var source = research.findPublicContentPage(slug).orElseThrow();
            var original = mvc.perform(get("/" + slug + "/")).andExpect(status().isOk())
                    .andReturn().getModelAndView().getModel();
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get(
                    "/design-preview/studio/topics/" + slug + "/"))
                    .andExpect(status().isOk()).andExpect(view().name("studio/topic-guide"))
                    .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
            org.assertj.core.api.Assertions.assertThat(html).contains(source.title(), source.introCopy(), source.targetReader(),
                    "noindex,nofollow", "See our investigations", "href=\"/design-preview/studio/work/\"");
            for (var group : java.util.List.of(source.deepDiveParagraphs(), source.fitBullets(), source.decisionSteps(),
                    source.lowEndBreakers(), source.quotePrepChecklist(), source.driverBullets())) {
                for (var item : group) org.assertj.core.api.Assertions.assertThat(html).contains(item);
            }
            for (var faq : source.faqBlocks()) org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
            for (var item : (java.util.List<?>) original.get("internalLinks")) {
                var link = (PageLink) item;
                org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(link.path()), link.title());
            }
            for (var item : (java.util.List<?>) original.get("stateMoneyPageLinks")) {
                var link = (StateMoneyPageLink) item;
                org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(link.path()), link.title());
            }
            for (var item : (java.util.List<?>) original.get("contentEvidenceLanes")) {
                var lane = (ContentEvidenceLaneView) item;
                for (var evidence : lane.sources()) org.assertj.core.api.Assertions.assertThat(html).contains(evidence.title(), evidence.url());
            }
            org.assertj.core.api.Assertions.assertThat(html.split("<h1", -1)).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("/pages.css", "hero-command-board", "detail-card");
        }
        mvc.perform(get("/design-preview/studio/topics/not-a-topic/")).andExpect(status().isNotFound());
        mvc.perform(get("/design-preview/studio/topics/septic-records-request-builder/")).andExpect(status().isNotFound());
        mvc.perform(get("/design-preview/studio/guides/"))
                .andExpect(content().string(containsString("/design-preview/studio/topics/septic-permit-process/")))
                .andExpect(content().string(containsString("/design-preview/studio/topics/septic-inspection-cost/")));
    }

    @Test void allStateBuyerPreviewsRetainTheirDistinctEditorialData() throws Exception {
        int checked = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            var source = research.findPublicStateMoneyPage("buying-a-house-with-a-septic-system", state.slug());
            if (source.isEmpty()) continue;
            var page = source.get();
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get(
                    "/design-preview/studio/buying-guide/" + state.slug() + "/"))
                    .andExpect(status().isOk()).andExpect(view().name("studio/state-buyer"))
                    .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
            org.assertj.core.api.Assertions.assertThat(html).contains(page.title(), page.introCopy(), page.uniqueAngle(),
                    page.targetReader(), "noindex,nofollow", "See our investigations");
            for (var group : java.util.List.of(page.fitBullets(), page.decisionSteps(), page.lowEndBreakers(),
                    page.quotePrepChecklist(), page.driverBullets())) {
                for (var value : group) org.assertj.core.api.Assertions.assertThat(html).contains(value);
            }
            for (var target : page.internalLinkTargets()) {
                org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(target));
            }
            for (var faq : page.faqBlocks()) org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
            for (var sourceRecord : research.getSources(page.officialSourceIds()))
                org.assertj.core.api.Assertions.assertThat(html).contains(sourceRecord.url(), sourceRecord.title());
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("/pages.css", "workflow-console");
            checked++;
        }
        org.assertj.core.api.Assertions.assertThat(checked).isEqualTo(50);
        mvc.perform(get("/design-preview/studio/buying-guide/not-a-state/")).andExpect(status().isNotFound());
        mvc.perform(get("/buying-a-house-with-a-septic-system/alabama/"))
                .andExpect(view().name("pages/state-money-page"));
    }

    @Test void allStateRecordsPreviewsRetainTheirDistinctEditorialAndSourceData() throws Exception {
        int checked = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            var source = research.findPublicStateMoneyPage("septic-records-checklist", state.slug());
            if (source.isEmpty()) continue;
            var page = source.get();
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get(
                    "/design-preview/studio/records/" + state.slug() + "/"))
                    .andExpect(status().isOk()).andExpect(view().name("studio/state-records"))
                    .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
            org.assertj.core.api.Assertions.assertThat(html).contains(page.introCopy(), page.uniqueAngle(),
                    page.targetReader(), "noindex,nofollow", "See how we investigate real properties",
                    "href=\"/design-preview/studio/work/\"");
            for (var group : java.util.List.of(page.fitBullets(), page.decisionSteps(), page.lowEndBreakers(),
                    page.quotePrepChecklist(), page.driverBullets())) {
                for (var value : group) org.assertj.core.api.Assertions.assertThat(html).contains(value);
            }
            for (var target : page.internalLinkTargets())
                org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(target));
            for (var faq : page.faqBlocks())
                org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
            for (var sourceRecord : research.getSources(page.officialSourceIds()))
                org.assertj.core.api.Assertions.assertThat(html).contains(sourceRecord.url(), sourceRecord.title());
            org.assertj.core.api.Assertions.assertThat(html.split("<h1", -1)).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("/pages.css", "state-money-page", "workflow-console");
            checked++;
        }
        org.assertj.core.api.Assertions.assertThat(checked).isEqualTo(50);
        mvc.perform(get("/design-preview/studio/records/not-a-state/")).andExpect(status().isNotFound());
        mvc.perform(get("/design-preview/studio/checklist/"))
                .andExpect(content().string(containsString("/design-preview/studio/records/alabama/")))
                .andExpect(content().string(containsString("/design-preview/studio/records/wyoming/")));
    }

    @Test void allStatePermitProcessPreviewsRetainTheirDistinctWorkflowAndSources() throws Exception {
        int checked = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            var source = research.findPublicStateMoneyPage("septic-permit-process", state.slug());
            if (source.isEmpty()) continue;
            var page = source.get();
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get(
                    "/design-preview/studio/permit-process/" + state.slug() + "/"))
                    .andExpect(status().isOk()).andExpect(view().name("studio/state-permit-process"))
                    .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
            org.assertj.core.api.Assertions.assertThat(html).contains(page.introCopy(), page.uniqueAngle(),
                    page.targetReader(), page.actionChecklistHeading(), "noindex,nofollow",
                    "See how we investigated", "From the site.");
            for (var group : java.util.List.of(page.fitBullets(), page.decisionSteps(),
                    page.lowEndBreakers(), page.quotePrepChecklist(), page.driverBullets())) {
                for (var value : group) org.assertj.core.api.Assertions.assertThat(html).contains(value);
            }
            for (var target : page.internalLinkTargets())
                org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(target));
            for (var faq : page.faqBlocks())
                org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
            for (var sourceRecord : research.getSources(page.officialSourceIds()))
                org.assertj.core.api.Assertions.assertThat(html).contains(sourceRecord.url(), sourceRecord.title());
            org.assertj.core.api.Assertions.assertThat(html.split("<h1", -1)).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("/pages.css", "state-money-page", "workflow-console");
            checked++;
        }
        org.assertj.core.api.Assertions.assertThat(checked).isEqualTo(50);
        mvc.perform(get("/design-preview/studio/permit-process/not-a-state/")).andExpect(status().isNotFound());
    }

    @Test void allStateInspectionCostPreviewsRetainTheirDistinctScopeAndSources() throws Exception {
        int checked = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            var source = research.findPublicStateMoneyPage("septic-inspection-cost", state.slug());
            if (source.isEmpty()) continue;
            var page = source.get();
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get(
                    "/design-preview/studio/inspection-cost/" + state.slug() + "/"))
                    .andExpect(status().isOk()).andExpect(view().name("studio/state-inspection-cost"))
                    .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
            org.assertj.core.api.Assertions.assertThat(html).contains(page.introCopy(), page.uniqueAngle(),
                    page.targetReader(), page.actionChecklistHeading(), "noindex,nofollow",
                    "See how we investigated", "Price the scope.");
            for (var group : java.util.List.of(page.fitBullets(), page.decisionSteps(), page.lowEndBreakers(),
                    page.quotePrepChecklist(), page.driverBullets())) {
                for (var value : group) org.assertj.core.api.Assertions.assertThat(html).contains(value);
            }
            for (var target : page.internalLinkTargets())
                org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(target));
            for (var faq : page.faqBlocks())
                org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
            for (var sourceRecord : research.getSources(page.officialSourceIds()))
                org.assertj.core.api.Assertions.assertThat(html).contains(sourceRecord.url(), sourceRecord.title());
            org.assertj.core.api.Assertions.assertThat(html.split("<h1", -1)).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("/pages.css", "state-money-page", "workflow-console");
            checked++;
        }
        org.assertj.core.api.Assertions.assertThat(checked).isEqualTo(50);
        mvc.perform(get("/design-preview/studio/inspection-cost/not-a-state/")).andExpect(status().isNotFound());
    }

    @Test void allPublishedStateReplacementCostPreviewsRetainTheirDistinctScopeAndSources() throws Exception {
        int rendered = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            if (research.findPublicStateMoneyPage("septic-replacement-cost", state.slug()).isEmpty()) continue;
            String html = mvc.perform(get("/design-preview/studio/replacement-cost/" + state.slug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-state-replacement.css?v=")))
                    .andExpect(content().string(containsString(state.stateName() + " / Replacement planning")))
                    .andExpect(content().string(containsString("Replace the failure.")))
                    .andExpect(content().string(containsString("Official sources")))
                    .andExpect(content().string(containsString("/design-preview/studio/calculator/?state=" + state.stateCode())))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains(state.stateName(), state.stateCode());
            rendered++;
        }
        org.assertj.core.api.Assertions.assertThat(rendered).isEqualTo(50);
    }

    @Test void allPublishedStatePercTestCostPreviewsRetainTheirDistinctSiteAndSourceContext() throws Exception {
        int rendered = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            if (research.findPublicStateMoneyPage("perc-test-cost", state.slug()).isEmpty()) continue;
            String html = mvc.perform(get("/design-preview/studio/perc-test-cost/" + state.slug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-state-perc.css?v=")))
                    .andExpect(content().string(containsString(state.stateName() + " / Soil &amp; site planning")))
                    .andExpect(content().string(containsString("Read the ground.")))
                    .andExpect(content().string(containsString("Official sources")))
                    .andExpect(content().string(containsString("/design-preview/studio/calculator/?state=" + state.stateCode())))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains(state.stateName(), state.stateCode());
            rendered++;
        }
        org.assertj.core.api.Assertions.assertThat(rendered).isEqualTo(50);
    }

    @Test void allPublishedStateDrainFieldCostPreviewsRetainTheirDistinctFieldAndSourceContext() throws Exception {
        int rendered = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            if (research.findPublicStateMoneyPage("drain-field-replacement-cost", state.slug()).isEmpty()) continue;
            String html = mvc.perform(get("/design-preview/studio/drain-field-cost/" + state.slug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-state-field.css?v=")))
                    .andExpect(content().string(containsString(state.stateName() + " / Drain-field planning")))
                    .andExpect(content().string(containsString("Recover the field.")))
                    .andExpect(content().string(containsString("Official sources")))
                    .andExpect(content().string(containsString("/design-preview/studio/calculator/?state=" + state.stateCode())))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains(state.stateName(), state.stateCode());
            rendered++;
        }
        org.assertj.core.api.Assertions.assertThat(rendered).isEqualTo(14);
    }

    @Test void allPublishedFailedPercPreviewsRetainTheirDistinctFailureAndSourceContext() throws Exception {
        int rendered = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            if (research.findPublicStateMoneyPage("failed-perc-test-septic", state.slug()).isEmpty()) continue;
            String html = mvc.perform(get("/design-preview/studio/failed-perc/" + state.slug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-state-problem.css?v=")))
                    .andExpect(content().string(containsString(state.stateName() + " / Failed site review")))
                    .andExpect(content().string(containsString("The result says no.")))
                    .andExpect(content().string(containsString("Official sources")))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains(state.stateName(), state.stateCode());
            rendered++;
        }
        org.assertj.core.api.Assertions.assertThat(rendered).isEqualTo(10);
    }

    @Test void allPublishedReplacementAreaPreviewsRetainTheirDistinctParcelAndSourceContext() throws Exception {
        int rendered = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            if (research.findPublicStateMoneyPage("septic-replacement-area", state.slug()).isEmpty()) continue;
            String html = mvc.perform(get("/design-preview/studio/replacement-area/" + state.slug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-state-problem.css?v=")))
                    .andExpect(content().string(containsString(state.stateName() + " / Replacement-area planning")))
                    .andExpect(content().string(containsString("The open yard")))
                    .andExpect(content().string(containsString("Official sources")))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains(state.stateName(), state.stateCode());
            rendered++;
        }
        org.assertj.core.api.Assertions.assertThat(rendered).isEqualTo(10);
    }

    @Test void allPublishedWetYardPreviewsRetainTheirDistinctSymptomAndSourceContext() throws Exception {
        int rendered = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            if (research.findPublicStateMoneyPage("wet-yard-over-septic-drain-field", state.slug()).isEmpty()) continue;
            String html = mvc.perform(get("/design-preview/studio/wet-yard/" + state.slug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-state-problem.css?v=")))
                    .andExpect(content().string(containsString(state.stateName() + " / Visible field symptoms")))
                    .andExpect(content().string(containsString("The wet patch")))
                    .andExpect(content().string(containsString("Official sources")))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains(state.stateName(), state.stateCode());
            rendered++;
        }
        org.assertj.core.api.Assertions.assertThat(rendered).isEqualTo(10);
    }

    @Test void commonJourneysStayInStudioWithoutRemovingAdvancedTools() throws Exception {
        for (String route : java.util.List.of("alabama/", "service/", "work/", "buying-guide/")) {
            String html = mvc.perform(get("/design-preview/studio/" + route))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("href=\"/\"", "href=\"/septic-record-finder/\"",
                    "href=\"/septic-records-request-builder/\"", "href=\"/septic-record-brief-example/");
            org.assertj.core.api.Assertions.assertThat(html).contains("href=\"/design-preview/studio/service/\"");
        }
        mvc.perform(get("/design-preview/studio/tools/"))
                .andExpect(content().string(containsString("href=\"/design-preview/studio/request-builder/\"")));
        mvc.perform(get("/design-preview/studio/request-builder/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Request the right record.")))
                .andExpect(content().string(containsString("data-records-request-builder")))
                .andExpect(content().string(containsString("/studio-request-builder.css?v=")))
                .andExpect(content().string(containsString("/app.js?v=")));
        mvc.perform(get("/design-preview/studio/bedroom-check/"))
                .andExpect(content().string(containsString("href=\"/design-preview/studio/request-builder/\"")))
                .andExpect(content().string(not(containsString("data-bedroom-embed-code"))));
        mvc.perform(get("/design-preview/studio/work/"))
                .andExpect(content().string(containsString("id=\"case-boundaries\"")));
    }

    @Test void regionalProofIsVisibleEarlyAndKeepsItsActualGeography() throws Exception {
        for (String route : java.util.List.of("alabama/", "alabama/autauga-county/", "georgia/")) {
            String html = mvc.perform(get("/design-preview/studio/" + route))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains("id=\"our-research\"", "See our investigations", "research-proof-title",
                    "not record availability for your property", "id=\"official-route\"");
            String primary = route.contains("-county/") ? "/design-preview/studio/work/morgan/" : "/design-preview/studio/work/";
            org.assertj.core.api.Assertions.assertThat(html).contains("class=\"studio-button research-proof-cta\" href=\"" + primary + "\"");
            if (route.contains("-county/")) {
                org.assertj.core.api.Assertions.assertThat(html).contains(StudioProofSelection.question("morgan"), "See how we investigated", "Morgan County, Alabama");
            }
            org.assertj.core.api.Assertions.assertThat(html.indexOf("id=\"our-research\""))
                    .isLessThan(html.indexOf("id=\"official-route\""));
            org.assertj.core.api.Assertions.assertThat(html).contains(route.startsWith("alabama")
                    ? "/design-preview/studio/work/morgan/" : "/design-preview/studio/work/haverhill/");
        }
        org.assertj.core.api.Assertions.assertThat(StudioProofSelection.select("tennessee", "overton-county").slug()).isEqualTo("overton");
        org.assertj.core.api.Assertions.assertThat(StudioProofSelection.select("south-carolina", "anderson-county").slug()).isEqualTo("anderson");
        org.assertj.core.api.Assertions.assertThat(StudioProofSelection.select("alabama", "autauga-county").place()).isEqualTo("Morgan County, Alabama");
        org.assertj.core.api.Assertions.assertThat(StudioProofSelection.select("georgia", "unknown").place()).isEqualTo("Haverhill, Massachusetts");
        for (var study : com.example.septic.web.StudioCaseStudies.ALL) {
            org.assertj.core.api.Assertions.assertThat(StudioProofSelection.question(study.slug())).endsWith("?");
        }
    }

    @Test void publicRequestBuilderHasOnePageHeading() throws Exception {
        String html = mvc.perform(get("/septic-records-request-builder/"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(java.util.regex.Pattern.compile("<h1(?:\\s[^>]*)?>").matcher(html).results().count()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(html).contains("<h2 data-records-task-heading>");
    }

    @Test void countyDirectoryContainsEveryPublishedCountyWithoutLegacyPresentation() throws Exception {
        String html = mvc.perform(get("/design-preview/studio/counties/"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(html).contains("county-directory-lane-v1.webp", "noindex,nofollow", "county-search", "/studio-directory.js?v=")
                .doesNotContain("/app.css");
        for (var county : research.getPublicCountyRecordsPages()) {
            var state = research.findStateByCode(county.stateCode()).orElseThrow();
            org.assertj.core.api.Assertions.assertThat(html).contains("/design-preview/studio/" + state.slug() + "/" + county.countySlug() + "/");
        }
        org.assertj.core.api.Assertions.assertThat(StudioGuideRoutes.preview("/septic-records-by-county/"))
                .isEqualTo("/design-preview/studio/counties/");
    }

    @Test void recordsChecklistIsEditorialUsefulAndConnectedToProof() throws Exception {
        String html = mvc.perform(get("/design-preview/studio/checklist/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/checklist"))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        org.assertj.core.api.Assertions.assertThat(html).contains("noindex,nofollow", "/studio-checklist.css?v=",
                "checklist-desk-v1.webp", "checklist-plan-v1.webp", "checklist-case-house-v1.webp",
                "checklist-closing-house-v1.webp", "Illustrative setting", "Three things to prepare.", "Property identity", "Search history",
                "Ask for the record", "Written no-record response", "id=\"our-research\"",
                "See how we investigated", "href=\"/design-preview/studio/work/overton/\"",
                "href=\"/design-preview/studio/intake/\"", "href=\"/design-preview/studio/counties/\"")
                .doesNotContain("/app.css", "type=\"checkbox\"");
        org.assertj.core.api.Assertions.assertThat(java.util.regex.Pattern.compile("<h1(?:\\s[^>]*)?>").matcher(html).results().count()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(html.indexOf("id=\"request\""))
                .isLessThan(html.indexOf("id=\"our-research\""));
    }

    @Test void permitLookupHubPreservesPublishedGuidanceAndRegionalLinks() throws Exception {
        var source = research.findPublicContentPage("septic-permit-lookup").orElseThrow();
        var original = mvc.perform(get("/septic-permit-lookup/"))
                .andExpect(status().isOk()).andReturn().getModelAndView().getModel();
        String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get("/design-preview/studio/permit-lookup/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/permit-lookup"))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        org.assertj.core.api.Assertions.assertThat(html).contains("noindex,nofollow", "/studio-permit-lookup.css?v=",
                "/studio-permit-lookup.js?v=", "permit-lookup-desk-v1.webp", "permit-file-spread-v1.webp",
                source.title(), source.introCopy(), source.targetReader(), "Find the office.", "A permit mention",
                "id=\"our-research\"", "href=\"/design-preview/studio/checklist/\"")
                .doesNotContain("/app.css", "hero-command-board");
        for (var group : java.util.List.of(source.deepDiveParagraphs(), source.fitBullets(), source.decisionSteps(),
                source.lowEndBreakers(), source.quotePrepChecklist(), source.driverBullets())) {
            for (var item : group) org.assertj.core.api.Assertions.assertThat(html).contains(item);
        }
        for (var faq : source.faqBlocks()) org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
        for (var item : (java.util.List<?>) original.get("internalLinks")) {
            var link = (PageLink) item;
            org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(link.path()), link.compactTitle());
        }
        org.assertj.core.api.Assertions.assertThat(html.split("<option value=", -1)).hasSize(52);
        org.assertj.core.api.Assertions.assertThat(java.util.regex.Pattern.compile("<h1(?:\\s[^>]*)?>").matcher(html).results().count()).isEqualTo(1);
        mvc.perform(get("/design-preview/studio/guides/"))
                .andExpect(content().string(containsString("/design-preview/studio/official-lookup-tools/")));
    }

    @Test void tdecHubPreservesPublishedGuidanceAndEveryCountyRoute() throws Exception {
        var source = research.findPublicContentPage("tdec-septic-records").orElseThrow();
        var original = mvc.perform(get("/tdec-septic-records/"))
                .andExpect(status().isOk()).andReturn().getModelAndView().getModel();
        String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get("/design-preview/studio/tdec-records/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/tdec-records"))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        org.assertj.core.api.Assertions.assertThat(html).contains("noindex,nofollow", "/studio-tdec.css?v=",
                "/studio-tdec.js?v=", "tdec-desk-v1.webp", "tdec-identity-v1.webp", "tdec-file-v1.webp",
                source.title(), source.introCopy(), source.targetReader(), "The viewer stopped.",
                "A route is not a property record.", "id=\"our-research\"", "href=\"/design-preview/studio/work/overton/\"")
                .doesNotContain("/app.css", "hero-command-board");
        for (var group : java.util.List.of(source.deepDiveParagraphs(), source.fitBullets(), source.decisionSteps(),
                source.lowEndBreakers(), source.quotePrepChecklist(), source.driverBullets())) {
            for (var item : group) org.assertj.core.api.Assertions.assertThat(html).contains(item);
        }
        for (var faq : source.faqBlocks()) org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
        for (var item : (java.util.List<?>) original.get("internalLinks")) {
            var link = (PageLink) item;
            org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(link.path()), link.compactTitle());
        }
        var counties = (java.util.List<TennesseeCountyRouteView>) original.get("tennesseeCountyRoutes");
        org.assertj.core.api.Assertions.assertThat(counties).hasSize(95);
        for (var county : counties) {
            org.assertj.core.api.Assertions.assertThat(html).contains(county.countyName(), county.recordsUrl());
        }
        org.assertj.core.api.Assertions.assertThat(html.split("<option value=", -1)).hasSize(97);
        org.assertj.core.api.Assertions.assertThat(java.util.regex.Pattern.compile("<h1(?:\\s[^>]*)?>").matcher(html).results().count()).isEqualTo(1);
    }

    @Test void officialLookupHubPreservesSourceGuidanceAndNationalRouteChoices() throws Exception {
        var source = research.findPublicContentPage("official-septic-lookup-tools").orElseThrow();
        var original = mvc.perform(get("/official-septic-lookup-tools/"))
                .andExpect(status().isOk()).andReturn().getModelAndView().getModel();
        String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get("/design-preview/studio/official-lookup-tools/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/official-lookup-tools"))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        org.assertj.core.api.Assertions.assertThat(html).contains("noindex,nofollow", "/studio-official-lookup.css?v=",
                "/studio-official-lookup.js?v=", "official-lookup-hero-v1.webp", "official-lookup-map-v1.webp",
                "official-lookup-files-v1.webp", source.title(), source.introCopy(), source.targetReader(),
                "Find the official source.", "State portal", "County office", "Targeted request",
                "https://www.tceq.texas.gov/permitting/ossf", "https://des.sc.gov/permits-regulations/septic-tanks",
                "id=\"our-research\"", "href=\"/design-preview/studio/work/overton/\"")
                .doesNotContain("/app.css", "official-tool-console", "hero-command-board");
        for (var group : java.util.List.of(source.deepDiveParagraphs(), source.fitBullets(), source.decisionSteps(),
                source.lowEndBreakers(), source.quotePrepChecklist(), source.driverBullets())) {
            for (var item : group) org.assertj.core.api.Assertions.assertThat(html).contains(item);
        }
        for (var faq : source.faqBlocks()) org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
        for (var item : (java.util.List<?>) original.get("internalLinks")) {
            var link = (PageLink) item;
            org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(link.path()), link.compactTitle());
        }
        for (var row : (java.util.List<CountyWorkflowFieldView>) original.get("contentOfficialFilePathRows")) {
            org.assertj.core.api.Assertions.assertThat(html).contains(row.label(), row.value());
        }
        org.assertj.core.api.Assertions.assertThat(html.split("<option value=", -1)).hasSize(52);
        org.assertj.core.api.Assertions.assertThat(java.util.regex.Pattern.compile("<h1(?:\\s[^>]*)?>").matcher(html).results().count()).isEqualTo(1);
    }

    @Test void northCarolinaHubPreservesCountyOwnedWorkflowAndPublishedRoutes() throws Exception {
        var source = research.findPublicContentPage("north-carolina-septic-permit-lookup").orElseThrow();
        var original = mvc.perform(get("/north-carolina-septic-permit-lookup/"))
                .andExpect(status().isOk()).andReturn().getModelAndView().getModel();
        String html = org.springframework.web.util.HtmlUtils.htmlUnescape(mvc.perform(get("/design-preview/studio/north-carolina-records/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/north-carolina-records"))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        org.assertj.core.api.Assertions.assertThat(html).contains("noindex,nofollow", "/studio-nc-records.css?v=",
                "/studio-nc-records.js?v=", "property-hero-v1.webp", "official-lookup-map-v1.webp", "file-dividers-v1.webp",
                source.title(), source.introCopy(), source.targetReader(), "The county holds", "County-held files",
                "NC On-Site Wastewater Program", "id=\"our-research\"")
                .doesNotContain("/app.css", "tdec-workspace", "nc-priority-routes__links");
        for (var group : java.util.List.of(source.deepDiveParagraphs(), source.fitBullets(), source.decisionSteps(),
                source.lowEndBreakers(), source.quotePrepChecklist(), source.driverBullets())) {
            for (var item : group) org.assertj.core.api.Assertions.assertThat(html).contains(item);
        }
        for (var faq : source.faqBlocks()) org.assertj.core.api.Assertions.assertThat(html).contains(faq.question(), faq.answer());
        for (var item : (java.util.List<?>) original.get("internalLinks")) {
            var link = (PageLink) item;
            org.assertj.core.api.Assertions.assertThat(html).contains(StudioGuideRoutes.preview(link.path()), link.compactTitle());
        }
        for (var row : (java.util.List<CountyWorkflowFieldView>) original.get("contentOfficialFilePathRows")) {
            org.assertj.core.api.Assertions.assertThat(html).contains(row.label(), row.value());
        }
        var counties = (java.util.List<com.example.septic.data.model.CountyRecordsPage>) original.get("northCarolinaCountyRoutes");
        org.assertj.core.api.Assertions.assertThat(counties).isNotEmpty();
        for (var county : counties) {
            org.assertj.core.api.Assertions.assertThat(html).contains(county.countyName(), county.recordsUrl(),
                    county.path("north-carolina").replace("/septic-records-checklist/north-carolina/", "/design-preview/studio/north-carolina/"));
        }
        org.assertj.core.api.Assertions.assertThat(html.split("<option value=", -1)).hasSize(counties.size() + 2);
        org.assertj.core.api.Assertions.assertThat(java.util.regex.Pattern.compile("<h1(?:\\s[^>]*)?>").matcher(html).results().count()).isEqualTo(1);
    }

    @Test void stateBuyerPlanningLinkStaysInStudioAndKeepsInputs() throws Exception {
        String html = mvc.perform(get("/design-preview/studio/buying-guide/alabama/"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(html).contains("href=\"/design-preview/studio/calculator/?")
                .doesNotContain("href=\"/septic-system-cost-calculator/?");
    }

    @Test void buyerArticlePreservesCuratedCopyFaqAndLinks() throws Exception {
        var source = research.findPublicContentPage("buying-a-house-with-a-septic-system").orElseThrow();
        String html = mvc.perform(get("/design-preview/studio/buying-guide/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/buying-guide"))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("/studio-article.css?v=")))
                .andExpect(content().string(not(containsString("/pages.css"))))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        String decoded = org.springframework.web.util.HtmlUtils.htmlUnescape(html);
        org.assertj.core.api.Assertions.assertThat(decoded).contains(source.title(), source.introCopy(), source.targetReader());
        for (var group : java.util.List.of(source.deepDiveParagraphs(), source.fitBullets(), source.decisionSteps(), source.lowEndBreakers(), source.quotePrepChecklist(), source.driverBullets())) {
            for (String item : group) org.assertj.core.api.Assertions.assertThat(decoded).contains(item);
        }
        for (String target : source.internalLinkTargets())
            org.assertj.core.api.Assertions.assertThat(decoded).contains(StudioGuideRoutes.preview(target));
        for (var faq : source.faqBlocks()) org.assertj.core.api.Assertions.assertThat(decoded).contains(faq.question(), faq.answer());
        for (String id : java.util.List.of("buyer-records", "seller-claim-records", "buyer-steps", "buyer-checklist", "buyer-questions", "buyer-local")) {
            org.assertj.core.api.Assertions.assertThat(html).contains("id=\"" + id + "\"", "href=\"#" + id + "\"");
        }
        org.assertj.core.api.Assertions.assertThat(html.split("<header", -1)).hasSize(2);
        mvc.perform(get("/buying-a-house-with-a-septic-system/"))
                .andExpect(status().isOk()).andExpect(view().name("pages/content-page"));
    }

    @Test void bedroomComparisonIsIsolatedAndDoesNotClaimPermitVerification() throws Exception {
        mvc.perform(get("/design-preview/studio/bedroom-check/"))
                .andExpect(status().isOk()).andExpect(view().name("studio/bedroom-check"))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("/studio-bedroom.js?v=")))
                .andExpect(content().string(containsString("does not retrieve or verify a permit")))
                .andExpect(content().string(containsString("data-bedroom-permit-form")))
                .andExpect(content().string(not(containsString("/app.js"))));
        mvc.perform(get("/design-preview/studio/guides/"))
                .andExpect(content().string(containsString("/design-preview/studio/bedroom-check/")));
        mvc.perform(get("/septic-bedroom-permit-checker/")).andExpect(status().isOk());
    }

    @Test void specializedRegionalRecordRoutesUseTheSharedStudioWorkspace() throws Exception {
        for (String route : java.util.List.of("texas-ossf-records/", "south-carolina-records/", "florida-ostds-records/")) {
            mvc.perform(get("/design-preview/studio/" + route))
                    .andExpect(status().isOk())
                    .andExpect(view().name("studio/regional-records"))
                    .andExpect(content().string(containsString("data-regional-records")))
                    .andExpect(content().string(containsString("/studio-regional-records.css?v=")))
                    .andExpect(content().string(containsString("/studio-regional-records.js?v=")))
                    .andExpect(content().string(containsString("/design-preview/studio/request-builder/?mode=task")))
                    .andExpect(content().string(not(containsString("/app.css"))));
        }
        String hub = mvc.perform(get("/design-preview/studio/official-lookup-tools/"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(hub).contains(
                "/design-preview/studio/north-carolina-records/",
                "/design-preview/studio/texas-ossf-records/",
                "/design-preview/studio/south-carolina-records/",
                "/design-preview/studio/florida-ostds-records/");
    }

    @Test void workspaceUsesSharedShellAndRealApiWithoutLegacyPresentation() throws Exception {
        String html = mvc.perform(get("/design-preview/studio/tools/"))
                .andExpect(status().isOk())
                .andExpect(view().name("studio/tools"))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("/studio-tools.js?v=")))
                .andExpect(content().string(containsString("A route is not a property record.")))
                .andExpect(content().string(containsString("Nothing is sent automatically")))
                .andExpect(content().string(not(containsString("/app.js"))))
                .andReturn().getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(html.split("<option value=", -1)).hasSize(52);
        mvc.perform(get("/design-preview/studio/guides/")).andExpect(content().string(containsString("/design-preview/studio/tools/")));
    }

    @Test void guideHubListsEveryStateUsingActualPublicationStatus() throws Exception {
        var directory = new com.example.septic.service.UsStateDirectoryService();
        var result = mvc.perform(get("/design-preview/studio/guides/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/studio-guides.css?v=")))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(not(containsString("/pages.css"))))
                .andReturn();
        String html = result.getResponse().getContentAsString();
        org.assertj.core.api.Assertions.assertThat(html.split("data-guide-state=", -1)).hasSize(51);
        for (var state : directory.allStates()) {
            org.assertj.core.api.Assertions.assertThat(html).contains("data-state-code=\"" + state.stateCode() + "\"");
            if (research.findPublicStateBySlug(state.slug()).isPresent()) {
                org.assertj.core.api.Assertions.assertThat(html).contains("href=\"/design-preview/studio/" + state.slug() + "/\"");
                mvc.perform(get("/design-preview/studio/" + state.slug() + "/")).andExpect(status().isOk());
            } else {
                org.assertj.core.api.Assertions.assertThat(html).doesNotContain("href=\"/design-preview/studio/" + state.slug() + "/\"");
            }
        }
        mvc.perform(get("/design-preview/studio/states/")).andExpect(status().isOk()).andExpect(view().name("studio/guides"));
    }

    @Test void guideTopicDestinationsRemainAvailable() throws Exception {
        for (String path : java.util.List.of("/septic-records-checklist/", "/septic-bedroom-permit-checker/", "/buying-a-house-with-a-septic-system/", "/septic-permit-process/", "/septic-system-cost-calculator/", "/septic-inspection-cost/")) {
            mvc.perform(get(path)).andExpect(status().isOk());
        }
    }

    @Test void allPreviewFamiliesShareOneNavigationAndShell() throws Exception {
        for (String route : java.util.List.of("alabama/", "alabama/autauga-county/", "service/", "work/", "intake/", "guides/", "tools/")) {
            String html = mvc.perform(get("/design-preview/studio/" + route))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-navigation.js?v=")))
                    .andExpect(content().string(containsString("id=\"studio-mobile-navigation\"")))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html.split("<header", -1)).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(html.split("<footer", -1)).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(html.split("/studio.css\\?v=", -1)).hasSize(2);
            for (String label : java.util.List.of("Our service", "Our work", "Guides", "Ask SepticPath")) {
                org.assertj.core.api.Assertions.assertThat(html).contains(label);
            }
            if (java.util.List.of("service/", "work/", "intake/").contains(route)) {
                org.assertj.core.api.Assertions.assertThat(html.split("aria-current=\"page\"", -1)).hasSize(4);
            }
        }
    }

    @Test void rendersRealStateDataWithAnIndependentPresentation() throws Exception {
        mvc.perform(get("/design-preview/studio/alabama/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("Alabama Department of Public Health")))
                .andExpect(content().string(containsString("/design-preview/studio/alabama/autauga-county/")))
                .andExpect(content().string(containsString("/studio.css?v=")))
                .andExpect(content().string(not(containsString("/pages.css"))))
                .andExpect(content().string(not(containsString("/app.css"))))
                .andExpect(content().string(not(containsString("premium-families"))))
                .andExpect(content().string(not(containsString("workflow-console"))))
                .andExpect(content().string(containsString("/design-preview/studio/intake/")));
    }

    @Test void stateDataIsNotHardcodedToTheReference() throws Exception {
        mvc.perform(get("/design-preview/studio/georgia/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Georgia Department of Public Health")))
                .andExpect(content().string(containsString("Georgia / Cost &amp; project planning")));
    }

    @Test void everyPublishedStateCostGuideKeepsItsPlanningAndSourceContext() throws Exception {
        int rendered = 0;
        for (var state : new com.example.septic.service.UsStateDirectoryService().allStates()) {
            if (research.findPublicStateBySlug(state.slug()).isEmpty()) continue;
            String html = mvc.perform(get("/design-preview/studio/" + state.slug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("/studio-state-cost.css?v=")))
                    .andExpect(content().string(containsString(state.stateName() + " / Cost &amp; project planning")))
                    .andExpect(content().string(containsString("Price the scope.")))
                    .andExpect(content().string(containsString("Official sources")))
                    .andExpect(content().string(containsString("/design-preview/studio/calculator/?state=" + state.stateCode())))
                    .andReturn().getResponse().getContentAsString();
            org.assertj.core.api.Assertions.assertThat(html).contains(state.stateName(), state.stateCode());
            rendered++;
        }
        org.assertj.core.api.Assertions.assertThat(rendered).isEqualTo(50);
    }

    @Test void unknownStateIsNotTurnedIntoAFakeLocalLandingPage() throws Exception {
        mvc.perform(get("/design-preview/studio/not-a-state/")).andExpect(status().isNotFound());
    }

    @Test void countyPreviewUsesLocalFactsAndNoLegacyPresentation() throws Exception {
        mvc.perform(get("/design-preview/studio/alabama/autauga-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Autauga County.")))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(containsString("/studio-county.css?v=")))
                .andExpect(content().string(containsString("county-property-v1.webp")))
                .andExpect(content().string(containsString("/design-preview/studio/intake/")))
                .andExpect(content().string(not(containsString("/app.css"))))
                .andExpect(content().string(not(containsString("/pages.css"))))
                .andExpect(content().string(not(containsString("premium-county"))))
                .andExpect(content().string(not(containsString("workflow-console"))));
    }

    @Test void countyAccessOverridesRetainTheirActualActions() throws Exception {
        mvc.perform(get("/design-preview/studio/new-york/suffolk-county/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"tel:")))
                .andExpect(content().string(containsString("Tax Map")))
                .andExpect(content().string(containsString("Starts a phone call")));
    }

    @Test void unknownCountyIsNotFabricated() throws Exception {
        mvc.perform(get("/design-preview/studio/alabama/not-a-county/")).andExpect(status().isNotFound());
    }

    @Test void everyPublishedCountyRendersWithItsOwnData() throws Exception {
        for (var county : research.getPublicCountyRecordsPages()) {
            var state = research.findStateByCode(county.stateCode()).orElseThrow();
            mvc.perform(get("/design-preview/studio/" + state.slug() + "/" + county.countySlug() + "/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString(county.countyName() + ".")));
        }
    }

    @Test void servicePreservesPricingAndExistingIntakeHandoffs() throws Exception {
        mvc.perform(get("/design-preview/studio/service/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Optional US $29")))
                .andExpect(content().string(containsString("At cost, with approval")))
                .andExpect(content().string(containsString("No upfront payment or automatic charge")))
                .andExpect(content().string(containsString("/design-preview/studio/intake/?mode=review")))
                .andExpect(content().string(containsString("/design-preview/studio/intake/")))
                .andExpect(content().string(containsString("/studio-editorial.css?v=")))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(not(containsString("<form"))))
                .andExpect(content().string(not(containsString("/app.css"))));
    }

    @Test void workUsesPublishedEvidenceAndPreservesCaseLimitations() throws Exception {
        mvc.perform(get("/design-preview/studio/work/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-site-plan-detail-public.png")))
                .andExpect(content().string(containsString("/images/case-study/redacted/shelby-brief-findings-public.png")))
                .andExpect(content().string(containsString("not a completed sale or a passed inspection")))
                .andExpect(content().string(containsString("property match unconfirmed")))
                .andExpect(content().string(containsString("/design-preview/studio/work/overton/")))
                .andExpect(content().string(containsString("noindex,nofollow")))
                .andExpect(content().string(not(containsString("/pages.css"))));
    }

    @Test void individualCasesRetainSourcesFindingsAndLimits() throws Exception {
        for (var study : StudioCaseStudies.ALL) {
            var result = mvc.perform(get("/design-preview/studio/work/" + study.slug() + "/"))
                    .andExpect(status().isOk()).andExpect(view().name("studio/case-study"))
                    .andExpect(content().string(containsString("noindex,nofollow")))
                    .andExpect(content().string(containsString(study.original())))
                    .andReturn();
            String html = org.springframework.web.util.HtmlUtils.htmlUnescape(result.getResponse().getContentAsString());
            org.assertj.core.api.Assertions.assertThat(html).contains(study.title(), study.limit(), study.caption());
            org.assertj.core.api.Assertions.assertThat(html).contains(study.angle(), study.stepTitle(0), study.stepTitle(1), study.bridge());
            org.assertj.core.api.Assertions.assertThat(html.indexOf("class=\"case-story")).isLessThan(html.indexOf("id=\"case-evidence\""));
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("morgan-interpretation", "case-intro");
            org.assertj.core.api.Assertions.assertThat(html).doesNotContain("storage/operations", "mail.google.com", "mailto:", "href=\"\"");
            if (study.original().isEmpty()) {
                org.assertj.core.api.Assertions.assertThat(html).contains("Source documents and correspondence are retained privately.")
                        .doesNotContain("Read the original case account");
            }
            for (var fact : study.facts()) org.assertj.core.api.Assertions.assertThat(html).contains(fact.source(), fact.value());
            for (var paragraph : study.story()) org.assertj.core.api.Assertions.assertThat(html).contains(paragraph);
            if (!study.image().isEmpty()) org.assertj.core.api.Assertions.assertThat(html).contains(study.image());
            else org.assertj.core.api.Assertions.assertThat(html).doesNotContain("/images/case-study/");
        }
        mvc.perform(get("/design-preview/studio/work/unknown/")) .andExpect(status().isNotFound());
    }
}
