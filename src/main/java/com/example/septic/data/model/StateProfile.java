package com.example.septic.data.model;

import java.util.List;

public record StateProfile(
        String stateCode,
        String stateName,
        String agencyName,
        String launchTier,
        String publishStatus,
        String pageAngle,
        String ruleType,
        String ruleSummaryPlainEnglish,
        Integer designFlowPerBedroomGpd,
        Integer minTankSizeGallons,
        List<BedroomBand> bedroomTable,
        String garbageDisposalPolicy,
        String permitSummary,
        List<String> permitPathSteps,
        String siteEvalSummary,
        List<String> ruleHighlights,
        String localOverrideNote,
        String countyOverrideRisk,
        String lastVerifiedAt,
        List<String> officialSourceIds,
        Double confidenceScore,
        List<String> itemsNeedingVerification
) {
    public String slug() {
        return stateName.toLowerCase().replace(" ", "-");
    }
}
