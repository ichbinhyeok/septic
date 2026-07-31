package com.example.septic.service;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.CountyRecordsPage;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SitemapService {
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
        entries.add(entry(seoService.absoluteUrl("/"), ""));
        entries.add(entry(seoService.absoluteUrl("/septic-system-cost-calculator/"), ""));
        entries.add(entry(seoService.absoluteUrl("/septic-tank-size-estimator/"), ""));
        entries.add(entry(seoService.absoluteUrl("/septic-pump-schedule-estimator/"), ""));
        entries.add(entry(seoService.absoluteUrl("/drain-field-estimator/"), ""));
        seoService.staticPagePaths().stream()
                .map(seoService::absoluteUrl)
                .map(url -> entry(url, ""))
                .forEach(entries::add);

        for (ContentPage contentPage : researchDataService.getPublicContentPages()) {
            if (!"septic-system-cost-calculator".equals(contentPage.slug())) {
                entries.add(entry(
                        seoService.absoluteUrl("/" + contentPage.slug() + "/"),
                        validDateOrBlank(contentPage.updatedAt())
                ));
            }
        }

        for (StateProfile state : researchDataService.getPublicStateProfiles()) {
            entries.add(entry(
                    seoService.absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/"),
                    statePageLastMod(state)
            ));
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
        return validDateOrBlank(stateMoneyPage.updatedAt());
    }

    private String countyRecordsPageLastMod(CountyRecordsPage countyPage, StateProfile state) {
        return validDateOrBlank(countyContentQualityService.effectiveUpdatedAt(countyPage));
    }

    private String statePageLastMod(StateProfile state) {
        return validDateOrBlank(state.lastVerifiedAt());
    }

    private String validDateOrBlank(String value) {
        return isIsoDate(value) ? value : "";
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
