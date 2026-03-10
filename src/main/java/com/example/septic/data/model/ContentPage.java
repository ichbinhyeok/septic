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
        String calculatorProjectType,
        String targetReader,
        List<String> fitBullets,
        List<String> decisionSteps,
        List<String> lowEndBreakers,
        List<String> quotePrepChecklist,
        List<String> driverBullets,
        List<FaqBlock> faqBlocks,
        List<String> internalLinkTargets,
        String publishStatus
) {
    public boolean isPublished() {
        return "published".equalsIgnoreCase(publishStatus) && hasLaunchQuality();
    }

    public boolean hasLaunchQuality() {
        return hasText(slug)
                && hasText(title)
                && hasText(metaDescription)
                && hasText(introCopy)
                && hasText(targetReader)
                && hasItems(fitBullets, 2)
                && hasItems(decisionSteps, 2)
                && hasItems(lowEndBreakers, 1)
                && hasItems(quotePrepChecklist, 1)
                && hasItems(driverBullets, 2)
                && hasItems(faqBlocks, 2)
                && hasItems(internalLinkTargets, 1);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasItems(List<?> values, int minimumSize) {
        return values != null && values.size() >= minimumSize;
    }
}
