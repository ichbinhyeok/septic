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
        List<String> officialSourceIds
) {
    public String key() {
        return contentSlug + "::" + stateCode;
    }

    public String calculatorProjectType() {
        return switch (contentSlug) {
            case "septic-replacement-cost" -> "replacement";
            case "perc-test-cost" -> "perc_test";
            case "drain-field-replacement-cost" -> "drainfield_replacement";
            case "septic-pumping-cost" -> "pumping";
            case "buying-a-house-with-a-septic-system" -> "buying_home";
            default -> "new_install";
        };
    }

    public String path(String stateSlug) {
        return "/" + contentSlug + "/" + stateSlug + "/";
    }
}
