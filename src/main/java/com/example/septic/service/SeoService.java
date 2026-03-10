package com.example.septic.service;

import com.example.septic.config.AppSiteProperties;
import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.FaqBlock;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import com.example.septic.web.EditorialProfile;
import com.example.septic.web.PageMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SeoService {
    private final AppSiteProperties siteProperties;
    private final ObjectMapper objectMapper;

    public SeoService(AppSiteProperties siteProperties) {
        this.siteProperties = siteProperties;
        this.objectMapper = JsonMapper.builder().findAndAddModules().build();
    }

    public PageMeta homePage() {
        String canonicalUrl = absoluteUrl("/");
        return pageMeta(
                "SepticPath | Septic System Cost & Size Estimator",
                "State-aware septic planning estimates for tank size, system type, and rough cost.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(editorialOrganization()),
                        toJson(webSite(canonicalUrl, "SepticPath",
                                "State-aware septic planning estimates for tank size, system type, and rough cost.")),
                        toJson(webPage(canonicalUrl, "SepticPath",
                                "State-aware septic planning estimates for tank size, system type, and rough cost.", "CollectionPage"))
                )
        );
    }

    public PageMeta calculatorPage() {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/");
        return pageMeta(
                "Septic System Cost Calculator",
                "Estimate likely tank size, system class, and septic project cost range by state.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(webPage(canonicalUrl, "Septic System Cost Calculator",
                                "Estimate likely tank size, system class, and septic project cost range by state.", "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic System Cost Calculator", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta stateCoveragePage() {
        String canonicalUrl = absoluteUrl("/states/");
        return pageMeta(
                "State Coverage | SepticPath",
                "Track live septic state guides, current deep-page coverage, and the research queue for the next rollout wave.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(webPage(canonicalUrl,
                                "State Coverage",
                                "Track live septic state guides, current deep-page coverage, and the research queue for the next rollout wave.",
                                "CollectionPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("State Coverage", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta tankSizeEstimatorPage() {
        String canonicalUrl = absoluteUrl("/septic-tank-size-estimator/");
        List<FaqBlock> faqBlocks = tankSizeEstimatorFaqs();
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(webPage(canonicalUrl, "Septic Tank Size Estimator",
                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.", "WebPage")));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb("Septic Tank Size Estimator", canonicalUrl)
        ))));
        jsonLdBlocks.add(toJson(faqPage(
                canonicalUrl,
                "Septic Tank Size Estimator",
                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.",
                faqBlocks
        )));
        return pageMeta(
                "Septic Tank Size Estimator",
                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.",
                canonicalUrl,
                "index,follow",
                jsonLdBlocks
        );
    }

    public List<FaqBlock> tankSizeEstimatorFaqs() {
        return List.of(
                new FaqBlock(
                        "How should a homeowner use this septic tank size estimator?",
                        "Use it as a planning tool to estimate a likely minimum tank size and a conservative range before you collect quotes or rely on old paperwork."
                ),
                new FaqBlock(
                        "Do bedrooms matter more than current occupancy for septic tank size?",
                        "In many states, bedrooms or design flow are the main public sizing signal. Occupancy still helps widen the planning range when usage is clearly higher than the bedroom count suggests."
                ),
                new FaqBlock(
                        "Does a garbage disposal change the likely tank size?",
                        "Sometimes yes. Some states call this out directly, and even where the rule is less explicit, disposal use is a reasonable homeowner-facing signal that the conservative range may need to move up."
                ),
                new FaqBlock(
                        "Can seasonal use justify a much smaller septic tank?",
                        "Usually not. Seasonal use may soften the pumping cadence estimate, but it should not aggressively shrink a homeowner-facing size recommendation."
                )
        );
    }

    public PageMeta pumpScheduleEstimatorPage() {
        String canonicalUrl = absoluteUrl("/septic-pump-schedule-estimator/");
        return pageMeta(
                "Septic Pump Schedule Estimator",
                "Estimate a rough pumping cadence, inspection cadence, and maintenance reminder from tank size and use.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(webPage(canonicalUrl, "Septic Pump Schedule Estimator",
                                "Estimate a rough pumping cadence, inspection cadence, and maintenance reminder from tank size and use.", "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic Pump Schedule Estimator", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta stateGuide(StateProfile state, String lastReviewedAt, EditorialProfile preparedBy, EditorialProfile reviewedBy) {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/");
        String title = stateGuideTitle(state);
        String description = stateGuideDescription(state);
        List<FaqBlock> faqBlocks = stateGuideFaqs(state);
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withEditorialMeta(webPage(canonicalUrl,
                title,
                description,
                "Article"), lastReviewedAt, preparedBy, reviewedBy)));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb("Septic System Cost Calculator", absoluteUrl("/septic-system-cost-calculator/")),
                crumb(state.stateName() + " Guide", canonicalUrl)
        ))));
        if (!faqBlocks.isEmpty()) {
            jsonLdBlocks.add(toJson(faqPage(
                    canonicalUrl,
                    title,
                    description,
                    faqBlocks
            )));
        }
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                jsonLdBlocks
        );
    }

    public PageMeta queuedStateGuide(String stateName, String stateSlug) {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/" + stateSlug + "/");
        String title = stateName + " Septic Guide | Research Queue";
        String description = "Planning starter for " + stateName + " homeowners while the official-source state guide is still in the research queue.";
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "noindex,follow",
                List.of(
                        toJson(webPage(
                                canonicalUrl,
                                title,
                                description,
                                "WebPage"
                        )),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("State Coverage", absoluteUrl("/states/")),
                                crumb(stateName + " Research Queue", canonicalUrl)
                        )))
                )
        );
    }

    public String stateGuideHeading(StateProfile state) {
        return switch (state.stateCode()) {
            case "AL" -> "Alabama septic cost guide and county permit path";
            case "AR" -> "Arkansas septic cost guide and county permit path";
            case "MS" -> "Mississippi septic cost guide and public records path";
            case "IN" -> "Indiana septic cost guide and county permit path";
            case "OK" -> "Oklahoma septic cost guide and soil-test path";
            case "KY" -> "Kentucky septic cost guide and local file path";
            case "MN" -> "Minnesota septic cost guide and property transfer risk";
            case "IL" -> "Illinois septic cost guide and local health file path";
            case "MD" -> "Maryland septic cost guide and property transfer risk";
            case "WI" -> "Wisconsin septic cost guide and POWTS inspection path";
            case "LA" -> "Louisiana septic cost guide and parish site-risk path";
            case "AZ" -> "Arizona septic cost guide and site approval path";
            case "CO" -> "Colorado septic cost guide and local OWTS permit path";
            case "VA" -> "Virginia septic cost guide and inspection obligations";
            case "TN" -> "Tennessee septic cost guide and permit file path";
            case "SC" -> "South Carolina septic cost guide and permit path";
            case "CA" -> "California septic cost guide and county permit path";
            case "TX" -> "Texas septic cost guide and local OSSF permit path";
            case "NY" -> "New York septic cost guide and Appendix 75-A rules";
            case "OH" -> "Ohio septic cost guide and local health permit path";
            case "MI" -> "Michigan septic cost guide and local health records path";
            case "GA" -> "Georgia septic cost guide and tank size estimate";
            case "PA" -> "Pennsylvania septic cost guide and SEO permit path";
            case "CT" -> "Connecticut septic cost guide and design flow rules";
            case "OR" -> "Oregon septic cost guide and site evaluation path";
            case "MA" -> "Massachusetts septic cost guide and Title 5 overview";
            case "FL" -> "Florida septic cost guide and DEP vs county path";
            case "WA" -> "Washington septic cost guide and inspection rules";
            case "NJ" -> "New Jersey septic cost guide and management rules";
            case "NC" -> "North Carolina septic cost guide and permit steps";
            case "MO" -> "Missouri septic cost guide and local permit path";
            default -> state.stateName() + " septic cost guide";
        };
    }

    public List<FaqBlock> stateGuideFaqs(StateProfile state) {
        List<FaqBlock> faqBlocks = new ArrayList<>();

        if (hasText(state.whoToCallFirst())) {
            faqBlocks.add(new FaqBlock(
                    "Who should a homeowner call first about septic work in " + state.stateName() + "?",
                    state.whoToCallFirst() + " Use that first call to confirm the local process before you rely on a national rule of thumb."
            ));
        }

        if (state.recordsToRequest() != null && !state.recordsToRequest().isEmpty()) {
            faqBlocks.add(new FaqBlock(
                    "What septic records should you request first in " + state.stateName() + "?",
                    String.join(" ", state.recordsToRequest()) + " Those records help confirm whether the low end of a quote is still realistic."
            ));
        }

        if (state.lowEndRiskChecks() != null && !state.lowEndRiskChecks().isEmpty()) {
            faqBlocks.add(new FaqBlock(
                    "What usually pushes a " + state.stateName() + " septic quote above the low end?",
                    String.join(" ", state.lowEndRiskChecks()) + " " + state.localOverrideNote()
            ));
        }

        String specialContext = firstNonBlank(
                state.specialAreaNote(),
                state.pageAngle(),
                state.permitTimelineNote(),
                state.siteEvalSummary()
        );
        if (specialContext != null) {
            faqBlocks.add(new FaqBlock(
                    "What makes " + state.stateName() + " different from a generic septic cost estimate?",
                    specialContext + " Final design, permit timing, and approval still need local verification."
            ));
        }

        if (faqBlocks.size() < 4) {
            faqBlocks.add(new FaqBlock(
                    "How much should you trust an online septic estimate in " + state.stateName() + "?",
                    "Treat it as a planning range only. " + state.localOverrideNote()
                            + " Final design, permit path, and approval still depend on local review."
            ));
        }

        return faqBlocks;
    }

    public PageMeta contentPage(ContentPage contentPage, String lastReviewedAt, EditorialProfile preparedBy, EditorialProfile reviewedBy) {
        String canonicalUrl = absoluteUrl("/" + contentPage.slug() + "/");
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withEditorialMeta(
                webPage(canonicalUrl, contentPage.title(), contentPage.metaDescription(), "CollectionPage"),
                lastReviewedAt,
                preparedBy,
                reviewedBy
        )));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb(contentPage.title(), canonicalUrl)
        ))));
        if (contentPage.faqBlocks() != null && !contentPage.faqBlocks().isEmpty()) {
            jsonLdBlocks.add(toJson(faqPage(canonicalUrl, contentPage.title(), contentPage.metaDescription(), contentPage.faqBlocks())));
        }
        return pageMeta(
                contentPage.title(),
                contentPage.metaDescription(),
                canonicalUrl,
                "index,follow",
                jsonLdBlocks
        );
    }

    public PageMeta stateMoneyPage(StateMoneyPage stateMoneyPage, StateProfile state, String lastReviewedAt, EditorialProfile preparedBy, EditorialProfile reviewedBy) {
        String canonicalUrl = absoluteUrl(stateMoneyPage.path(state.slug()));
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withEditorialMeta(
                webPage(canonicalUrl, stateMoneyPage.title(), stateMoneyPage.metaDescription(), "Article"),
                lastReviewedAt,
                preparedBy,
                reviewedBy
        )));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb(stateMoneyPage.title(), canonicalUrl)
        ))));
        if (stateMoneyPage.faqBlocks() != null && !stateMoneyPage.faqBlocks().isEmpty()) {
            jsonLdBlocks.add(toJson(faqPage(canonicalUrl, stateMoneyPage.title(), stateMoneyPage.metaDescription(), stateMoneyPage.faqBlocks())));
        }
        return pageMeta(
                stateMoneyPage.title(),
                stateMoneyPage.metaDescription(),
                canonicalUrl,
                "index,follow",
                jsonLdBlocks
        );
    }

    public PageMeta basicPage(String title, String description, String path) {
        String canonicalUrl = absoluteUrl(path);
        return pageMeta(
                title,
                description,
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(webPage(canonicalUrl, title, description, "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb(title, canonicalUrl)
                        )))
                )
        );
    }

    public List<String> staticPagePaths() {
        return Arrays.asList(
                "/states/",
                "/about/",
                "/privacy-policy/",
                "/terms-of-use/",
                "/contact/"
        );
    }

    public PageMeta notFound(String message) {
        return pageMeta(
                "State Not Found",
                message,
                absoluteUrl("/404"),
                "noindex,nofollow",
                List.of()
        );
    }

    public String absoluteUrl(String path) {
        String baseUrl = siteProperties.baseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        if ("/".equals(normalizedPath)) {
            return baseUrl + "/";
        }
        return baseUrl + normalizedPath;
    }

    private PageMeta pageMeta(String title, String description, String canonicalUrl, String robots, List<String> jsonLdBlocks) {
        return new PageMeta(
                title,
                description,
                canonicalUrl,
                robots,
                absoluteUrl("/social-card.svg"),
                jsonLdBlocks
        );
    }

    private Map<String, Object> webSite(String url, String name, String description) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "WebSite");
        payload.put("name", name);
        payload.put("url", url);
        payload.put("description", description);
        payload.put("inLanguage", "en-US");
        return payload;
    }

    private Map<String, Object> webPage(String url, String name, String description, String type) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", type);
        payload.put("name", name);
        payload.put("url", url);
        payload.put("description", description);
        payload.put("inLanguage", "en-US");
        payload.put("isPartOf", Map.of("@type", "WebSite", "url", absoluteUrl("/")));
        return payload;
    }

    private Map<String, Object> withEditorialMeta(
            Map<String, Object> payload,
            String lastReviewedAt,
            EditorialProfile preparedBy,
            EditorialProfile reviewedBy
    ) {
        payload.put("author", editorialContributorReference(preparedBy));
        payload.put("editor", editorialContributorReference(reviewedBy));
        payload.put("publisher", editorialOrganizationReference());
        if (lastReviewedAt != null && !lastReviewedAt.isBlank()) {
            payload.put("dateModified", lastReviewedAt);
        }
        return payload;
    }

    private Map<String, Object> editorialOrganization() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "Organization");
        payload.put("name", "SepticPath");
        payload.put("url", absoluteUrl("/"));
        return payload;
    }

    private Map<String, Object> editorialOrganizationReference() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@type", "Organization");
        payload.put("name", "SepticPath");
        payload.put("url", absoluteUrl("/"));
        return payload;
    }

    private Map<String, Object> editorialContributorReference(EditorialProfile profile) {
        if (profile == null) {
            return editorialOrganizationReference();
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@type", "Organization");
        payload.put("name", profile.displayName());
        payload.put("description", profile.roleTitle() + ". " + profile.focusSummary());
        payload.put("url", absoluteUrl("/about/"));
        return payload;
    }

    private Map<String, Object> faqPage(String url, String name, String description, List<FaqBlock> faqBlocks) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "FAQPage");
        payload.put("name", name);
        payload.put("url", url);
        payload.put("description", description);
        payload.put("mainEntity", faqBlocks.stream().map(this::faqQuestion).toList());
        return payload;
    }

    private Map<String, Object> faqQuestion(FaqBlock faqBlock) {
        Map<String, Object> question = new LinkedHashMap<>();
        question.put("@type", "Question");
        question.put("name", faqBlock.question());
        question.put("acceptedAnswer", Map.of(
                "@type", "Answer",
                "text", faqBlock.answer()
        ));
        return question;
    }

    private Map<String, Object> breadcrumb(List<Map<String, Object>> items) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@context", "https://schema.org");
        payload.put("@type", "BreadcrumbList");
        payload.put("itemListElement", items);
        return payload;
    }

    private Map<String, Object> crumb(String name, String url) {
        Map<String, Object> crumb = new LinkedHashMap<>();
        crumb.put("@type", "ListItem");
        crumb.put("name", name);
        crumb.put("item", url);
        return crumb;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String stateGuideTitle(StateProfile state) {
        return switch (state.stateCode()) {
            case "IA" -> "Iowa Septic Cost Guide and County Records Path";
            case "KS" -> "Kansas Septic Cost Guide and Soil-Profile Path";
            case "NE" -> "Nebraska Septic Cost Guide and Permit Path";
            case "NM" -> "New Mexico Septic Cost Guide and Buyer File Path";
            case "UT" -> "Utah Septic Cost Guide and Local Health Permit Path";
            case "WV" -> "West Virginia Septic Cost Guide and Local File Path";
            case "SD" -> "South Dakota Septic Cost Guide and Permit Path";
            case "AL" -> "Alabama Septic Cost Guide and County Permit Path";
            case "AR" -> "Arkansas Septic Cost Guide and County Permit Path";
            case "MS" -> "Mississippi Septic Cost Guide and Public Records Path";
            case "IN" -> "Indiana Septic Cost Guide and County Permit Path";
            case "OK" -> "Oklahoma Septic Cost Guide and Soil-Test Path";
            case "KY" -> "Kentucky Septic Cost Guide and Local File Path";
            case "MN" -> "Minnesota Septic Cost Guide and Property Transfer Risk";
            case "IL" -> "Illinois Septic Cost Guide and Local Health File Path";
            case "MD" -> "Maryland Septic Cost Guide and Property Transfer Risk";
            case "WI" -> "Wisconsin Septic Cost Guide and POWTS Inspection Path";
            case "LA" -> "Louisiana Septic Cost Guide and Parish Site-Risk Path";
            case "AZ" -> "Arizona Septic Cost Guide and Site Approval Path";
            case "CO" -> "Colorado Septic Cost Guide and Local OWTS Permit Path";
            case "VA" -> "Virginia Septic Cost Guide and Inspection Obligations";
            case "TN" -> "Tennessee Septic Cost Guide and Permit File Path";
            case "SC" -> "South Carolina Septic Cost Guide and Permit Path";
            case "CA" -> "California Septic Cost Guide and County Permit Path";
            case "TX" -> "Texas Septic Cost Guide and Local OSSF Permit Path";
            case "NY" -> "New York Septic Cost Guide and Appendix 75-A Rules";
            case "OH" -> "Ohio Septic Cost Guide and Local Health Permit Path";
            case "MI" -> "Michigan Septic Cost Guide and Local Health Records Path";
            case "GA" -> "Georgia Septic Cost Guide, Tank Size, and Permit Notes";
            case "PA" -> "Pennsylvania Septic Cost Guide and SEO Permit Path";
            case "CT" -> "Connecticut Septic Cost Guide and Design Flow Rules";
            case "OR" -> "Oregon Septic Cost Guide and Site Evaluation Path";
            case "MA" -> "Massachusetts Septic Cost Guide and Title 5 Rules";
            case "FL" -> "Florida Septic Cost Guide, DEP Counties, and Permit Path";
            case "WA" -> "Washington Septic Cost Guide and Inspection Rules";
            case "NJ" -> "New Jersey Septic Cost Guide and Management Rules";
            case "NC" -> "North Carolina Septic Cost Guide and Permit Steps";
            case "MO" -> "Missouri Septic Cost Guide and Local Permit Path";
            default -> state.stateName() + " Septic Cost Guide";
        };
    }

    private String stateGuideDescription(StateProfile state) {
        return switch (state.stateCode()) {
            case "IA" -> "Iowa septic planning estimates with county-sanitarian routing, time-of-transfer context, and official-source links.";
            case "KS" -> "Kansas septic planning estimates with soil-profile requirements, local sanitary-code variation, and official-source links.";
            case "NE" -> "Nebraska septic planning estimates with DHHS permit filing, registered-system context, and official-source links.";
            case "NM" -> "New Mexico septic planning estimates with buyer-file checks, permit-search context, and official-source links.";
            case "UT" -> "Utah septic planning estimates with local health routing, R317-4 permit workflow, and official-source links.";
            case "WV" -> "West Virginia septic planning estimates with local health routing, sewage-permit file context, and official-source links.";
            case "SD" -> "South Dakota septic planning estimates with permit-certificate context, inspection sequencing, and official-source links.";
            case "AL" -> "Alabama septic planning estimates with county health routing, Permit to Install timing, and official ADPH source links.";
            case "AR" -> "Arkansas septic planning estimates with county health routing, permit-copy context, and official-source links.";
            case "MS" -> "Mississippi septic planning estimates with county health routing, public-record context, and official-source links.";
            case "IN" -> "Indiana septic planning estimates with county permit routing, sewer-availability context, and official-source links.";
            case "OK" -> "Oklahoma septic planning estimates with local DEQ routing, soil-test context, and official-source links.";
            case "KY" -> "Kentucky septic planning estimates with local health routing, site-evaluation files, and official-source links.";
            case "MN" -> "Minnesota septic planning estimates with local SSTS transfer rules, seller-disclosure context, and official-source links.";
            case "IL" -> "Illinois septic planning estimates with local health file routing, evaluation-form context, and official-source links.";
            case "MD" -> "Maryland septic planning estimates with local approving authority routing, PTI transfer context, and official-source links.";
            case "WI" -> "Wisconsin septic planning estimates with county POWTS routing, maintenance-tracking context, and official-source links.";
            case "LA" -> "Louisiana septic planning estimates with parish health routing, sewer-availability context, and official-source links.";
            case "AZ" -> "Arizona septic planning estimates with county delegation, site-investigation paperwork, and official ADEQ source links.";
            case "CO" -> "Colorado septic planning estimates with local public health routing, site-and-soil paperwork, and official-source links.";
            case "VA" -> "Virginia septic planning estimates with local health district routing, operation-permit context, and inspection obligations.";
            case "TN" -> "Tennessee septic planning estimates with permit-file retrieval, repair-permit context, and official-source links.";
            case "SC" -> "South Carolina septic planning estimates with permit-copy retrieval, local office routing, and official-source links.";
            case "CA" -> "California septic planning estimates with local agency routing, OWTS policy context, and county permit-file questions.";
            case "TX" -> "Texas septic planning estimates with local permitting authority routing, site-evaluation context, and official OSSF sources.";
            case "NY" -> "New York septic planning estimates with Appendix 75-A rules, county health workflow, and official-source links.";
            case "OH" -> "Ohio septic planning estimates with local health department routing, Chapter 3701-29 permit context, and official-source links.";
            case "MI" -> "Michigan septic planning estimates with local health department routing, file-retrieval context, and official-source links.";
            case "GA" -> "Georgia septic planning estimates with bedroom sizing, disposal upsizing, county permit context, and official-source links.";
            case "PA" -> "Pennsylvania septic planning estimates with Sewage Enforcement Officer workflow, local permit context, and official-source links.";
            case "CT" -> "Connecticut septic planning estimates with design flow, potential-bedroom risk, local health review, and official-source links.";
            case "OR" -> "Oregon septic planning estimates with site evaluation, permit sequencing, and official-source links.";
            case "MA" -> "Massachusetts septic planning estimates with Title 5 timing, buyer risk, and local Board of Health context.";
            case "FL" -> "Florida septic planning estimates with DEP-versus-county routing, inspection context, and official-source links.";
            case "WA" -> "Washington septic planning estimates with local health jurisdiction rules, inspection cadence, and official-source links.";
            case "NJ" -> "New Jersey septic planning estimates with management-program risk, Pinelands context, and official-source links.";
            case "NC" -> "North Carolina septic planning estimates with permit-ladder context, county health workflow, and official-source links.";
            case "MO" -> "Missouri septic planning estimates with local authority routing, permit path, and official-source links.";
            default -> "Planning estimates, permit context, and official sources for " + state.stateName() + " homeowners.";
        };
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String toJson(Map<String, Object> payload) {
        if ("BreadcrumbList".equals(payload.get("@type"))) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) payload.get("itemListElement");
            for (int index = 0; index < itemList.size(); index++) {
                itemList.get(index).put("position", index + 1);
            }
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize structured data", exception);
        }
    }
}
