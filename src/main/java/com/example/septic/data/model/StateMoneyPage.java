package com.example.septic.data.model;

import java.util.List;

public record StateMoneyPage(
        String contentSlug,
        String stateCode,
        String title,
        String metaDescription,
        String introCopy,
        String uniqueAngle,
        List<String> driverBullets,
        List<FaqBlock> faqBlocks,
        List<String> internalLinkTargets,
        List<String> officialSourceIds,
        String calculatorProjectType
) {
    public String key() {
        return contentSlug + "::" + stateCode;
    }

    public String calculatorProjectType() {
        if (calculatorProjectType != null && !calculatorProjectType.isBlank()) {
            return calculatorProjectType;
        }
        return switch (contentSlug) {
            case "septic-replacement-cost" -> "replacement";
            case "perc-test-cost" -> "perc_test";
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
}
