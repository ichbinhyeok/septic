package com.example.septic.data.model;

import java.util.List;

public record StateProfile(
        String stateCode,
        String stateName,
        String launchTier,
        String publishStatus,
        String ruleType,
        String ruleSummaryPlainEnglish,
        Integer minTankSizeGallons,
        List<BedroomBand> bedroomTable,
        String garbageDisposalPolicy,
        String permitSummary,
        String siteEvalSummary,
        String localOverrideNote,
        List<String> officialSourceIds,
        Double confidenceScore,
        List<String> itemsNeedingVerification
) {
    public String slug() {
        return stateName.toLowerCase().replace(" ", "-");
    }
}
