package com.example.septic.web;

import java.util.List;

public record PageMeta(
        String title,
        String description,
        String canonicalUrl,
        String robots,
        String socialImageUrl,
        List<PageLink> breadcrumbs,
        List<String> jsonLdBlocks,
        String dataDownloadUrl
) {
    public PageMeta(String title, String description, String canonicalUrl, String robots, List<String> jsonLdBlocks) {
        this(title, description, canonicalUrl, robots, null, List.of(), jsonLdBlocks, null);
    }

    public PageMeta(String title, String description, String canonicalUrl, String robots, String socialImageUrl, List<String> jsonLdBlocks) {
        this(title, description, canonicalUrl, robots, socialImageUrl, List.of(), jsonLdBlocks, null);
    }

    public PageMeta(
            String title,
            String description,
            String canonicalUrl,
            String robots,
            String socialImageUrl,
            List<PageLink> breadcrumbs,
            List<String> jsonLdBlocks
    ) {
        this(title, description, canonicalUrl, robots, socialImageUrl, breadcrumbs, jsonLdBlocks, null);
    }

    public PageMeta {
        robots = robots == null || robots.isBlank() ? "index,follow" : robots;
        socialImageUrl = socialImageUrl == null ? "" : socialImageUrl;
        breadcrumbs = breadcrumbs == null ? List.of() : List.copyOf(breadcrumbs);
        jsonLdBlocks = jsonLdBlocks == null ? List.of() : List.copyOf(jsonLdBlocks);
        dataDownloadUrl = dataDownloadUrl == null ? "" : dataDownloadUrl;
    }

    public PageMeta withDataDownloadUrl(String url) {
        return new PageMeta(
                title,
                description,
                canonicalUrl,
                robots,
                socialImageUrl,
                breadcrumbs,
                jsonLdBlocks,
                url
        );
    }
}
