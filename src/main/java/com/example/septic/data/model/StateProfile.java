package com.example.septic.data.model;

import java.util.Arrays;
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
        List<String> localAuthoritySourceIds,
        List<String> recordsLookupSourceIds,
        List<String> localActionSteps,
        String whoToCallFirst,
        List<String> recordsToRequest,
        List<String> lowEndRiskChecks,
        String permitTimelineNote,
        String buyerInspectionTrigger,
        String maintenanceInspectionNote,
        String specialAreaNote,
        Double confidenceScore,
        List<String> itemsNeedingVerification
) {
    public String slug() {
        return stateName.toLowerCase().replace(" ", "-");
    }

    public boolean isPublished() {
        return "published".equalsIgnoreCase(publishStatus);
    }

    public String ruleTypeLabel() {
        if (ruleType == null || ruleType.isBlank()) {
            return "Local septic workflow";
        }
        return switch (ruleType.toLowerCase()) {
            case "permit_path" -> "Permit and records path";
            case "sanitary_permit_path" -> "Sanitary permit path";
            case "bedroom_based" -> "Bedroom-based sizing rule";
            case "bedroom_table" -> "Bedroom table sizing rule";
            case "buyer_risk" -> "Buyer-risk records workflow";
            case "design_flow" -> "Design-flow sizing rule";
            case "inspection_path" -> "Inspection and records path";
            case "local_authority" -> "Local authority workflow";
            case "records_path" -> "Records lookup path";
            case "site_approval" -> "Site approval workflow";
            case "county_administered" -> "County-administered workflow";
            case "state_administered" -> "State-administered workflow";
            case "hybrid" -> "Hybrid state and county workflow";
            default -> Arrays.stream(ruleType.split("[_\\-\\s]+"))
                    .filter(part -> !part.isBlank())
                    .map(part -> part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase())
                    .reduce((left, right) -> left + " " + right)
                    .orElse("Local septic workflow");
        };
    }
}
