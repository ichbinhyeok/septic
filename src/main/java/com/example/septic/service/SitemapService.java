package com.example.septic.service;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.SourceRecord;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class SitemapService {
    /*
     * The shared content, state, and county workflow surfaces were materially
     * rebuilt through this date. Keep this as a manual editorial revision
     * marker: advance it only when a shared template changes page meaning or
     * user-visible workflow content, never for cosmetic-only releases.
     */
    private static final String SHARED_WORKFLOW_REVISION_DATE = "2026-09-01";
    private static final String RECORDS_CONTENT_REVISION_DATE = "2026-09-01";
    private static final String STATE_RECORDS_REVISION_DATE = "2026-09-01";
    private static final String COUNTY_RECORDS_REVISION_DATE = "2026-09-01";
    /*
     * Page-specific material revisions keep lastmod accurate without making a
     * cosmetic or narrowly scoped release look like a sitewide content update.
     */
    private static final Map<String, String> MATERIAL_PAGE_REVISIONS = Map.ofEntries(
            Map.entry("/", "2026-09-11"),
            Map.entry("/septic-record-finder/", "2026-09-12"),
            Map.entry("/septic-records-access-index/", "2026-09-11"),
            Map.entry("/septic-record-brief-example/", "2026-09-12"),
            Map.entry("/offer-prep-septic-file-check/", "2026-09-12"),
            Map.entry("/official-septic-lookup-tools/", "2026-09-12"),
            Map.entry("/tdec-septic-records/", "2026-09-12"),
            Map.entry("/north-carolina-septic-permit-lookup/", "2026-09-12"),
            Map.entry("/texas-ossf-records-search/", "2026-09-12"),
            Map.entry("/florida-ostds-permit-lookup/", "2026-09-12"),
            Map.entry("/dhec-septic-permit-lookup/", "2026-09-12"),
            Map.entry("/septic-as-built-records/", "2026-09-11"),
            Map.entry("/septic-tank-location-records/", "2026-09-11")
    );

    private final ResearchDataService researchDataService;
    private final PublishingPolicyService publishingPolicyService;
    private final SeoService seoService;
    private final CountyContentQualityService countyContentQualityService;

    public SitemapService(
            ResearchDataService researchDataService,
            PublishingPolicyService publishingPolicyService,
            SeoService seoService,
            CountyContentQualityService countyContentQualityService
    ) {
        this.researchDataService = researchDataService;
        this.publishingPolicyService = publishingPolicyService;
        this.seoService = seoService;
        this.countyContentQualityService = countyContentQualityService;
    }

    public String robotsTxt() {
        return String.join("\n",
                "User-agent: *",
                "Allow: /",
                "Disallow: /quote-request/",
                "Sitemap: " + seoService.absoluteUrl("/sitemap.xml"),
                "Sitemap: " + seoService.absoluteUrl("/sitemap-county.xml"),
                ""
        );
    }

    public String sitemapXml() {
        List<SitemapEntry> entries = new ArrayList<>();
        entries.add(entry(seoService.absoluteUrl("/"), materialRevision("/")));
        entries.add(entry(seoService.absoluteUrl("/septic-system-cost-calculator/"), ""));
        entries.add(entry(seoService.absoluteUrl("/septic-tank-size-estimator/"), ""));
        entries.add(entry(seoService.absoluteUrl("/septic-pump-schedule-estimator/"), ""));
        entries.add(entry(seoService.absoluteUrl("/drain-field-estimator/"), ""));
        seoService.staticPagePaths().stream()
                .map(path -> entry(seoService.absoluteUrl(path), materialRevision(path)))
                .forEach(entries::add);

        for (ContentPage contentPage : researchDataService.getPublicContentPages()) {
            if (!"septic-system-cost-calculator".equals(contentPage.slug())) {
                entries.add(entry(
                        seoService.absoluteUrl("/" + contentPage.slug() + "/"),
                        latestValidDate(Stream.of(
                                contentPage.updatedAt(),
                                materialRevision("/" + contentPage.slug() + "/"),
                                SHARED_WORKFLOW_REVISION_DATE,
                                isRecordsWorkflowContentPage(contentPage.slug()) ? RECORDS_CONTENT_REVISION_DATE : ""
                        ))
                ));
            }
        }

        for (StateProfile state : researchDataService.getPublicStateProfiles()) {
            if (publishingPolicyService.isIndexableStateGuide(state)) {
                entries.add(entry(
                        seoService.absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/"),
                        statePageLastMod(state)
                ));
            }
        }

        for (StateMoneyPage stateMoneyPage : researchDataService.getPublicStateMoneyPages()) {
            researchDataService.findStateByCode(stateMoneyPage.stateCode())
                    .filter(state -> !stateMoneyPage.isCanonicalAlias())
                    .filter(state -> publishingPolicyService.isIndexableStateMoneyPage(stateMoneyPage, state))
                    .map(StateProfile::slug)
                    .map(stateMoneyPage::path)
                    .map(seoService::absoluteUrl)
                    .ifPresent(url -> entries.add(entry(
                            url,
                            researchDataService.findStateByCode(stateMoneyPage.stateCode())
                                    .map(state -> stateMoneyPageLastMod(stateMoneyPage, state))
                                    .orElse("")
                    )));
        }

        return renderUrlSet(entries);
    }

    public String countySitemapXml() {
        List<SitemapEntry> entries = new ArrayList<>();
        addCountyRecordsEntries(entries);
        return renderUrlSet(entries);
    }

    private void addCountyRecordsEntries(List<SitemapEntry> entries) {
        for (CountyRecordsPage countyPage : researchDataService.getPublicCountyRecordsPages()) {
            researchDataService.findStateByCode(countyPage.stateCode())
                    .ifPresent(state -> entries.add(entry(
                            seoService.absoluteUrl(countyPage.path(state.slug())),
                            countyRecordsPageLastMod(countyPage, state)
                    )));
        }
    }

    private String renderUrlSet(List<SitemapEntry> entries) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (SitemapEntry entry : entries.stream().distinct().sorted((left, right) -> left.url().compareTo(right.url())).toList()) {
            xml.append("  <url><loc>").append(entry.url()).append("</loc>");
            if (entry.lastMod() != null && !entry.lastMod().isBlank()) {
                xml.append("<lastmod>").append(entry.lastMod()).append("</lastmod>");
            }
            xml.append("</url>\n");
        }
        xml.append("</urlset>\n");
        return xml.toString();
    }

    private SitemapEntry entry(String url, String lastMod) {
        return new SitemapEntry(url, lastMod);
    }

    private String stateMoneyPageLastMod(StateMoneyPage stateMoneyPage, StateProfile state) {
        Stream<String> pageAndStateDates = Stream.of(
                stateMoneyPage.updatedAt(),
                stateMoneyPage.reviewedAt(),
                state.lastVerifiedAt(),
                "septic-records-checklist".equals(stateMoneyPage.contentSlug())
                        ? STATE_RECORDS_REVISION_DATE
                        : SHARED_WORKFLOW_REVISION_DATE
        );
        Stream<String> sourceDates = researchDataService.getSources(stateMoneyPage.officialSourceIds()).stream()
                .map(SourceRecord::contentVerifiedAt);
        return latestValidDate(Stream.concat(pageAndStateDates, sourceDates));
    }

    private String countyRecordsPageLastMod(CountyRecordsPage countyPage, StateProfile state) {
        return latestValidDate(Stream.of(
                countyContentQualityService.effectiveUpdatedAt(countyPage),
                COUNTY_RECORDS_REVISION_DATE,
                countyPage.searchGuide() == null ? "" : countyPage.searchGuide().reviewedAt()
        ));
    }

    private boolean isRecordsWorkflowContentPage(String slug) {
        return switch (slug) {
            case "official-septic-lookup-tools",
                    "tdec-septic-records",
                    "north-carolina-septic-permit-lookup",
                    "texas-ossf-records-search",
                    "florida-ostds-permit-lookup",
                    "dhec-septic-permit-lookup",
                    "how-to-find-septic-records-online",
                    "septic-records-by-county",
                    "septic-permit-search-by-address",
                    "septic-permit-lookup",
                    "septic-as-built-records",
                    "septic-tank-location-records" -> true;
            default -> false;
        };
    }

    private String materialRevision(String path) {
        return MATERIAL_PAGE_REVISIONS.getOrDefault(path, "");
    }

    private String statePageLastMod(StateProfile state) {
        return latestValidDate(Stream.of(state.lastVerifiedAt(), SHARED_WORKFLOW_REVISION_DATE));
    }

    private String latestValidDate(Stream<String> dates) {
        return dates.filter(this::isIsoDate)
                .max(String::compareTo)
                .orElse("");
    }

    private boolean isIsoDate(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            LocalDate.parse(value);
            return true;
        } catch (java.time.format.DateTimeParseException exception) {
            return false;
        }
    }

    private record SitemapEntry(String url, String lastMod) {
    }
}
