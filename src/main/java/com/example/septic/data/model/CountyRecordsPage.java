package com.example.septic.data.model;

import java.util.List;

public record CountyRecordsPage(
        String stateCode,
        String countyName,
        String countySlug,
        String title,
        String metaDescription,
        String introCopy,
        String uniqueAngle,
        String targetReader,
        String officeLabel,
        String officeUrl,
        String recordsLabel,
        String recordsUrl,
        String contactLine,
        String parcelAnchorLabel,
        String parcelAnchorUrl,
        String parcelAnchorNote,
        String requestScriptHeading,
        String requestScriptBody,
        List<String> buyerActions,
        List<String> sellerActions,
        List<String> contractorActions,
        List<String> redFlagChecklist,
        List<String> decisionSteps,
        List<String> recordsToRequest,
        List<String> lowEndBreakers,
        List<FaqBlock> faqBlocks,
        List<String> internalLinkTargets,
        List<String> officialSourceIds,
        CountyWorkflowStructureData workflowStructure,
        String updatedAt,
        String publishStatus
) {
    public String key() {
        return stateCode + "::" + countySlug;
    }

    public boolean isPublished() {
        return "published".equalsIgnoreCase(publishStatus) && hasLaunchQuality();
    }

    public boolean hasLaunchQuality() {
        return hasText(stateCode)
                && hasText(countyName)
                && hasText(countySlug)
                && hasText(title)
                && hasText(metaDescription)
                && hasText(introCopy)
                && hasText(uniqueAngle)
                && hasText(targetReader)
                && hasText(officeLabel)
                && hasText(officeUrl)
                && hasText(recordsLabel)
                && hasText(recordsUrl)
                && hasItems(decisionSteps, 2)
                && hasItems(recordsToRequest, 2)
                && hasItems(lowEndBreakers, 2)
                && hasItems(faqBlocks, 2)
                && hasItems(internalLinkTargets, 1)
                && hasItems(officialSourceIds, 1);
    }

    public String path(String stateSlug) {
        return "/septic-records-checklist/" + stateSlug + "/" + countySlug + "/";
    }

    public boolean hasParcelAnchor() {
        return hasText(parcelAnchorLabel) && hasText(parcelAnchorUrl);
    }

    public boolean hasActionPlaybook() {
        return hasText(requestScriptBody)
                || hasItems(buyerActions, 1)
                || hasItems(sellerActions, 1)
                || hasItems(contractorActions, 1)
                || hasItems(redFlagChecklist, 1);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasItems(List<?> values, int minimumSize) {
        return values != null && values.size() >= minimumSize;
    }
}
