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
        return new PageMeta(
                "Septic System Cost & Size Estimator",
                "State-aware septic planning estimates for tank size, system type, and rough cost.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(webSite(canonicalUrl, "Septic System Cost & Size Estimator",
                                "State-aware septic planning estimates for tank size, system type, and rough cost.")),
                        toJson(webPage(canonicalUrl, "Septic System Cost & Size Estimator",
                                "State-aware septic planning estimates for tank size, system type, and rough cost.", "CollectionPage"))
                )
        );
    }

    public PageMeta calculatorPage() {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/");
        return new PageMeta(
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
        return new PageMeta(
                "Septic Tank Size Estimator",
                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(webPage(canonicalUrl, "Septic Tank Size Estimator",
                                "Estimate likely minimum septic tank size, a conservative range, and a rough pumping cadence by state.", "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic Tank Size Estimator", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta pumpScheduleEstimatorPage() {
        String canonicalUrl = absoluteUrl("/septic-pump-schedule-estimator/");
        return new PageMeta(
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

    public PageMeta stateGuide(StateProfile state) {
        String canonicalUrl = absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/");
        return new PageMeta(
                state.stateName() + " Septic Cost & Size Guide",
                "Planning estimates, permit context, and official sources for " + state.stateName() + " homeowners.",
                canonicalUrl,
                "index,follow",
                List.of(
                        toJson(webPage(canonicalUrl,
                                state.stateName() + " Septic Cost & Size Guide",
                                "Planning estimates, permit context, and official sources for " + state.stateName() + " homeowners.",
                                "WebPage")),
                        toJson(breadcrumb(List.of(
                                crumb("Home", absoluteUrl("/")),
                                crumb("Septic System Cost Calculator", absoluteUrl("/septic-system-cost-calculator/")),
                                crumb(state.stateName() + " Guide", canonicalUrl)
                        )))
                )
        );
    }

    public PageMeta contentPage(ContentPage contentPage) {
        String canonicalUrl = absoluteUrl("/" + contentPage.slug() + "/");
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(webPage(canonicalUrl, contentPage.title(), contentPage.metaDescription(), "WebPage")));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb(contentPage.title(), canonicalUrl)
        ))));
        if (contentPage.faqBlocks() != null && !contentPage.faqBlocks().isEmpty()) {
            jsonLdBlocks.add(toJson(faqPage(canonicalUrl, contentPage.title(), contentPage.metaDescription(), contentPage.faqBlocks())));
        }
        return new PageMeta(
                contentPage.title(),
                contentPage.metaDescription(),
                canonicalUrl,
                "index,follow",
                jsonLdBlocks
        );
    }

    public PageMeta stateMoneyPage(StateMoneyPage stateMoneyPage, StateProfile state) {
        String canonicalUrl = absoluteUrl(stateMoneyPage.path(state.slug()));
        List<String> jsonLdBlocks = new ArrayList<>();
        jsonLdBlocks.add(toJson(webPage(canonicalUrl, stateMoneyPage.title(), stateMoneyPage.metaDescription(), "WebPage")));
        jsonLdBlocks.add(toJson(breadcrumb(List.of(
                crumb("Home", absoluteUrl("/")),
                crumb(stateMoneyPage.title(), canonicalUrl)
        ))));
        if (stateMoneyPage.faqBlocks() != null && !stateMoneyPage.faqBlocks().isEmpty()) {
            jsonLdBlocks.add(toJson(faqPage(canonicalUrl, stateMoneyPage.title(), stateMoneyPage.metaDescription(), stateMoneyPage.faqBlocks())));
        }
        return new PageMeta(
                stateMoneyPage.title(),
                stateMoneyPage.metaDescription(),
                canonicalUrl,
                "index,follow",
                jsonLdBlocks
        );
    }

    public PageMeta basicPage(String title, String description, String path) {
        String canonicalUrl = absoluteUrl(path);
        return new PageMeta(
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
        return new PageMeta(
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
        payload.put("isPartOf", Map.of("@type", "WebSite", "url", absoluteUrl("/")));
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
