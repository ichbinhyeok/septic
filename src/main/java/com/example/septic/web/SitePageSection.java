package com.example.septic.web;

import java.util.List;

public record SitePageSection(
        String title,
        String body,
        List<String> bullets
) {
}
