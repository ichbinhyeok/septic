package com.example.septic.service;

import com.example.septic.data.model.ContentPage;
import com.example.septic.data.model.StateMoneyPage;
import com.example.septic.data.model.StateProfile;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SitemapService {
    private final ResearchDataService researchDataService;
    private final SeoService seoService;

    public SitemapService(ResearchDataService researchDataService, SeoService seoService) {
        this.researchDataService = researchDataService;
        this.seoService = seoService;
    }

    public String robotsTxt() {
        return String.join("\n",
                "User-agent: *",
                "Allow: /",
                "Disallow: /quote-request/",
                "Sitemap: " + seoService.absoluteUrl("/sitemap.xml"),
                ""
        );
    }

    public String sitemapXml() {
        List<SitemapEntry> entries = new ArrayList<>();
        String defaultLastMod = latestPublishedUpdate();
        entries.add(entry(seoService.absoluteUrl("/"), defaultLastMod));
        entries.add(entry(seoService.absoluteUrl("/septic-system-cost-calculator/"), defaultLastMod));
        entries.add(entry(seoService.absoluteUrl("/septic-tank-size-estimator/"), defaultLastMod));
        entries.add(entry(seoService.absoluteUrl("/septic-pump-schedule-estimator/"), defaultLastMod));
        seoService.staticPagePaths().stream()
                .map(seoService::absoluteUrl)
                .map(url -> entry(url, defaultLastMod))
                .forEach(entries::add);

        for (ContentPage contentPage : researchDataService.getPublicContentPages()) {
            if (!"septic-system-cost-calculator".equals(contentPage.slug())) {
                entries.add(entry(
                        seoService.absoluteUrl("/" + contentPage.slug() + "/"),
                        researchDataService.contentPagesGeneratedAt()
                ));
            }
        }

        for (StateProfile state : researchDataService.getPublicStateProfiles()) {
            entries.add(entry(
                    seoService.absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/"),
                    state.lastVerifiedAt()
            ));
        }

        for (StateMoneyPage stateMoneyPage : researchDataService.getPublicStateMoneyPages()) {
            researchDataService.findStateByCode(stateMoneyPage.stateCode())
                    .map(StateProfile::slug)
                    .map(stateMoneyPage::path)
                    .map(seoService::absoluteUrl)
                    .ifPresent(url -> entries.add(entry(
                            url,
                            researchDataService.findStateByCode(stateMoneyPage.stateCode())
                                    .map(StateProfile::lastVerifiedAt)
                                    .orElse(researchDataService.stateMoneyPagesGeneratedAt())
                    )));
        }

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

    private String latestPublishedUpdate() {
        return List.of(
                        researchDataService.stateProfilesGeneratedAt(),
                        researchDataService.contentPagesGeneratedAt(),
                        researchDataService.stateMoneyPagesGeneratedAt()
                ).stream()
                .filter(value -> value != null && !value.isBlank())
                .max(String::compareTo)
                .orElse("");
    }

    private record SitemapEntry(String url, String lastMod) {
    }
}
