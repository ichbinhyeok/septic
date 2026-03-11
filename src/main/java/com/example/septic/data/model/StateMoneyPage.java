package com.example.septic.data.model;

import java.util.List;

public record StateMoneyPage(
        String contentSlug,
        String stateCode,
        String title,
        String metaDescription,
        String introCopy,
        String uniqueAngle,
        String targetReader,
        List<String> fitBullets,
        List<String> decisionSteps,
        List<String> lowEndBreakers,
        List<String> quotePrepChecklist,
        List<String> driverBullets,
        List<FaqBlock> faqBlocks,
        List<String> internalLinkTargets,
        List<String> officialSourceIds,
        String calculatorProjectType,
        String publishStatus
) {
    public String key() {
        return contentSlug + "::" + stateCode;
    }

    public boolean isPublished() {
        return "published".equalsIgnoreCase(publishStatus) && hasLaunchQuality();
    }

    public boolean hasLaunchQuality() {
        return hasText(contentSlug)
                && hasText(stateCode)
                && hasText(title)
                && hasText(metaDescription)
                && hasText(introCopy)
                && hasText(uniqueAngle)
                && hasText(targetReader)
                && hasItems(fitBullets, 2)
                && hasItems(decisionSteps, 2)
                && hasItems(lowEndBreakers, 1)
                && hasItems(quotePrepChecklist, 1)
                && hasItems(driverBullets, 2)
                && hasItems(faqBlocks, 2)
                && hasItems(internalLinkTargets, 1)
                && hasItems(officialSourceIds, 1);
    }

    public String calculatorProjectType() {
        if (calculatorProjectType != null && !calculatorProjectType.isBlank()) {
            return calculatorProjectType;
        }
        return switch (contentSlug) {
            case "septic-replacement-cost" -> "replacement";
            case "perc-test-cost" -> "perc_test";
            case "failed-perc-test-septic" -> "perc_test";
            case "septic-replacement-area" -> "drainfield_replacement";
            case "wet-yard-over-septic-drain-field" -> "drainfield_replacement";
            case "drain-field-replacement-cost" -> "drainfield_replacement";
            case "septic-pumping-cost" -> "pumping";
            case "septic-inspection-cost" -> "inspection";
            case "buying-a-house-with-a-septic-system" -> "buying_home";
            case "septic-records-checklist" -> "buying_home";
            case "septic-permit-process" -> "new_install";
            default -> "new_install";
        };
    }

    public String path(String stateSlug) {
        return "/" + contentSlug + "/" + stateSlug + "/";
    }

    public String localAuthorityHeading() {
        return switch (contentSlug) {
            case "septic-permit-process" -> "Find the office handling this permit path";
            case "septic-records-checklist" -> "Find the office holding the file";
            case "buying-a-house-with-a-septic-system" -> "Find the office tied to this deal";
            case "septic-inspection-cost" -> "Find the office behind the inspection file";
            case "perc-test-cost" -> "Find the office behind the site review";
            case "failed-perc-test-septic" -> "Find the office behind the failed site review";
            case "septic-replacement-area" -> "Find the office behind the replacement-area file";
            case "wet-yard-over-septic-drain-field" -> "Find the office behind the wet-yard or failure file";
            default -> "Find the local permitting authority";
        };
    }

    public String recordsLookupHeading() {
        return switch (contentSlug) {
            case "septic-records-checklist" -> "Open the records trail first";
            case "buying-a-house-with-a-septic-system" -> "Pull the deal paperwork first";
            case "septic-inspection-cost" -> "Pull the inspection file first";
            case "septic-permit-process" -> "Pull the permit file first";
            case "failed-perc-test-septic" -> "Open the site and permit file first";
            case "septic-replacement-area" -> "Open the replacement-area file first";
            case "wet-yard-over-septic-drain-field" -> "Open the failure, inspection, and repair file first";
            default -> "Look up septic records first";
        };
    }

    public String actionChecklistHeading() {
        return switch (contentSlug) {
            case "buying-a-house-with-a-septic-system" -> "Deal checklist";
            case "septic-permit-process" -> "Permit prep checklist";
            case "septic-records-checklist" -> "File check checklist";
            case "septic-inspection-cost" -> "Inspection prep checklist";
            case "septic-replacement-cost", "drain-field-replacement-cost" -> "Replacement prep checklist";
            case "perc-test-cost" -> "Site review checklist";
            case "failed-perc-test-septic" -> "Failed-site prep checklist";
            case "septic-replacement-area" -> "Replacement-area prep checklist";
            case "wet-yard-over-septic-drain-field" -> "Wet-yard failure checklist";
            case "septic-pumping-cost" -> "Maintenance prep checklist";
            default -> "Local action checklist";
        };
    }

    public String localPrepHeading() {
        return switch (contentSlug) {
            case "buying-a-house-with-a-septic-system" -> "Start with this deal prep";
            case "septic-permit-process" -> "Start with this permit prep";
            case "septic-records-checklist" -> "Start with this file prep";
            case "septic-inspection-cost" -> "Start with this inspection prep";
            case "septic-replacement-cost", "drain-field-replacement-cost" -> "Start with this replacement prep";
            case "perc-test-cost" -> "Start with this site-review prep";
            case "failed-perc-test-septic" -> "Start with this failed-site prep";
            case "septic-replacement-area" -> "Start with this replacement-area prep";
            case "wet-yard-over-septic-drain-field" -> "Start with this wet-yard prep";
            case "septic-pumping-cost" -> "Start with this maintenance prep";
            default -> "Start with this local prep";
        };
    }

    public String riskHeading(String stateName) {
        return switch (contentSlug) {
            case "buying-a-house-with-a-septic-system" -> "What turns this " + stateName + " deal into a bigger septic risk";
            case "septic-permit-process" -> "What turns this " + stateName + " permit path into a bigger job";
            case "septic-records-checklist" -> "What makes the file less trustworthy in " + stateName;
            case "septic-inspection-cost" -> "What makes this " + stateName + " inspection more than a simple visit";
            case "septic-replacement-cost" -> "What widens this " + stateName + " replacement range";
            case "drain-field-replacement-cost" -> "What widens this " + stateName + " drain field repair path";
            case "perc-test-cost" -> "What widens this " + stateName + " site-testing range";
            case "failed-perc-test-septic" -> "What widens this " + stateName + " failed-perc path";
            case "septic-replacement-area" -> "What widens this " + stateName + " replacement-area path";
            case "wet-yard-over-septic-drain-field" -> "What widens this " + stateName + " wet-yard failure path";
            case "septic-pumping-cost" -> "What turns pumping into a bigger " + stateName + " maintenance issue";
            default -> "What widens this page in " + stateName;
        };
    }

    public String buyerTriggerHeading() {
        return switch (contentSlug) {
            case "buying-a-house-with-a-septic-system" -> "Closing-risk trigger";
            case "septic-records-checklist" -> "When the missing file becomes a deal problem";
            case "septic-inspection-cost" -> "When the inspection becomes leverage";
            default -> "Buyer trigger";
        };
    }

    public String maintenanceHeading() {
        return switch (contentSlug) {
            case "septic-pumping-cost" -> "Maintenance cadence note";
            case "septic-inspection-cost" -> "Inspection and follow-up note";
            case "septic-permit-process" -> "Long-run maintenance note";
            default -> "Maintenance / inspection note";
        };
    }

    public String quoteChecklistHeading() {
        return switch (contentSlug) {
            case "buying-a-house-with-a-septic-system" -> "Bring this into the next agent or inspector call";
            case "septic-records-checklist" -> "Bring this into the next records call";
            case "septic-permit-process" -> "Bring this into the next permit call";
            case "septic-inspection-cost" -> "Bring this into the next inspection call";
            case "failed-perc-test-septic" -> "Bring this into the next site-review call";
            case "septic-replacement-area" -> "Bring this into the next replacement-area call";
            case "wet-yard-over-septic-drain-field" -> "Bring this into the next failure-risk call";
            default -> "Bring this into the next quote call";
        };
    }

    public String officialLinksHeading() {
        return switch (contentSlug) {
            case "buying-a-house-with-a-septic-system" -> "Official links for the deal file";
            case "septic-records-checklist" -> "Official file and lookup links";
            case "septic-permit-process" -> "Official permit and file links";
            case "septic-inspection-cost" -> "Official inspection and file links";
            case "failed-perc-test-septic" -> "Official site-review and file links";
            case "septic-replacement-area" -> "Official replacement-area and file links";
            case "wet-yard-over-septic-drain-field" -> "Official failure, inspection, and file links";
            default -> "Official links to use next";
        };
    }

    public boolean highlightBuyerTrigger() {
        return switch (contentSlug) {
            case "buying-a-house-with-a-septic-system", "septic-records-checklist", "septic-inspection-cost" -> true;
            default -> false;
        };
    }

    public boolean highlightMaintenanceNote() {
        return switch (contentSlug) {
            case "septic-pumping-cost", "septic-inspection-cost", "septic-records-checklist", "septic-permit-process" -> true;
            default -> false;
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasItems(List<?> values, int minimumSize) {
        return values != null && values.size() >= minimumSize;
    }
}
