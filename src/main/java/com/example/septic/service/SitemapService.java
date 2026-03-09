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
        List<String> urls = new ArrayList<>();
        urls.add(seoService.absoluteUrl("/"));
        urls.add(seoService.absoluteUrl("/septic-system-cost-calculator/"));
        urls.add(seoService.absoluteUrl("/septic-tank-size-estimator/"));
        urls.add(seoService.absoluteUrl("/septic-pump-schedule-estimator/"));
        seoService.staticPagePaths().stream()
                .map(seoService::absoluteUrl)
                .forEach(urls::add);

        for (ContentPage contentPage : researchDataService.getContentPages()) {
            if (!"septic-system-cost-calculator".equals(contentPage.slug())) {
                urls.add(seoService.absoluteUrl("/" + contentPage.slug() + "/"));
            }
        }

        for (StateProfile state : researchDataService.getStateProfiles()) {
            urls.add(seoService.absoluteUrl("/septic-system-cost-calculator/" + state.slug() + "/"));
        }

        for (StateMoneyPage stateMoneyPage : researchDataService.getStateMoneyPages()) {
            researchDataService.findStateByCode(stateMoneyPage.stateCode())
                    .map(StateProfile::slug)
                    .map(stateMoneyPage::path)
                    .map(seoService::absoluteUrl)
                    .ifPresent(urls::add);
        }

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String url : urls.stream().distinct().sorted().toList()) {
            xml.append("  <url><loc>").append(url).append("</loc></url>\n");
        }
        xml.append("</urlset>\n");
        return xml.toString();
    }
}
