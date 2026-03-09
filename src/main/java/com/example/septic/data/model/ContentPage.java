package com.example.septic.data.model;

import java.util.List;

public record ContentPage(
        String slug,
        String intentType,
        String primaryKeyword,
        List<String> secondaryKeywords,
        String title,
        String metaDescription,
        String introCopy,
        String calculatorModule,
        List<String> driverBullets,
        List<FaqBlock> faqBlocks,
        List<String> internalLinkTargets,
        String publishStatus
) {
    public boolean isPublished() {
        return publishStatus == null || publishStatus.isBlank() || "published".equalsIgnoreCase(publishStatus);
    }
}
