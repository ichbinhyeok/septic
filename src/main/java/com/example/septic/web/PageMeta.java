package com.example.septic.web;

import java.util.List;

public record PageMeta(
        String title,
        String description,
        String canonicalUrl,
        String robots,
        List<String> jsonLdBlocks
) {
    public PageMeta {
        robots = robots == null || robots.isBlank() ? "index,follow" : robots;
        jsonLdBlocks = jsonLdBlocks == null ? List.of() : List.copyOf(jsonLdBlocks);
    }
}
