package com.example.septic.web;

import java.util.List;

public record PageMeta(
        String title,
        String description,
        String canonicalUrl,
        String robots,
        String socialImageUrl,
        List<String> jsonLdBlocks
) {
    public PageMeta(String title, String description, String canonicalUrl, String robots, List<String> jsonLdBlocks) {
        this(title, description, canonicalUrl, robots, null, jsonLdBlocks);
    }

    public PageMeta {
        robots = robots == null || robots.isBlank() ? "index,follow" : robots;
        socialImageUrl = socialImageUrl == null ? "" : socialImageUrl;
        jsonLdBlocks = jsonLdBlocks == null ? List.of() : List.copyOf(jsonLdBlocks);
    }
}
