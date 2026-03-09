package com.example.septic.service;

import com.example.septic.config.AppSiteProperties;
import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.FaqBlock;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
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
                "Septic System Cost & Size Estimator",
                "State-aware septic planning estimates for tank size, system type, and rough cost.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(editorialOrganization()),
                        toJson(webSite(canonicalUrl, "Septic System Cost & Size Estimator",
                                "State-aware septic planning estimates for tank size, system type, and rough cost.")),
                        toJson(webPage(canonicalUrl, "Septic System Cost & Size Estimator",
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

    public PageMeta stateGuide(StateProfile state, String lastReviewedAt) {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/");
        String title = stateGuideTitle(state);
        String description = stateGuideDescription(state);
        List<FaqBlock> faqBlocks = stateGuideFaqs(state);
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withEditorialMeta(webPage(canonicalUrl,
                title,
                description,
                "Article"), lastReviewedAt)));
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

    public String stateGuideHeading(StateProfile state) {
        return switch (state.stateCode()) {
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

    public PageMeta contentPage(ContentPage contentPage, String lastReviewedAt) {
        String canonicalUrl = absoluteUrl("/" + contentPage.slug() + "/");
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withEditorialMeta(
                webPage(canonicalUrl, contentPage.title(), contentPage.metaDescription(), "CollectionPage"),
                lastReviewedAt
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

    public PageMeta stateMoneyPage(StateMoneyPage stateMoneyPage, StateProfile state, String lastReviewedAt) {
        String canonicalUrl = absoluteUrl(stateMoneyPage.path(state.slug()));
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(withEditorialMeta(
                webPage(canonicalUrl, stateMoneyPage.title(), stateMoneyPage.metaDescription(), "Article"),
                lastReviewedAt
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

    private Map<String, Object> withEditorialMeta(Map<String, Object> payload, String lastReviewedAt) {
        payload.put("author", editorialOrganizationReference());
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
        payload.put("name", "Septic System Cost & Size Estimator");
        payload.put("url", absoluteUrl("/"));
        return payload;
    }

    private Map<String, Object> editorialOrganizationReference() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("@type", "Organization");
        payload.put("name", "Septic System Cost & Size Estimator");
        payload.put("url", absoluteUrl("/"));
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
