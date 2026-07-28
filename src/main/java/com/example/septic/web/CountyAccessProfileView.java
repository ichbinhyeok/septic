package com.example.septic.web;

import java.util.List;

public record CountyAccessProfileView(
        String countyKey,
        String mode,
        String modeLabel,
        String heading,
        String summary,
        String primaryLabel,
        String primaryUrl,
        String secondaryLabel,
        String secondaryUrl,
        String completionLabel,
        String limitation,
        List<String> requiredInputs,
        List<String> expectedArtifacts,
        List<String> steps
) {
    public boolean hasSecondaryAction() {
        return secondaryLabel != null
                && !secondaryLabel.isBlank()
                && secondaryUrl != null
                && !secondaryUrl.isBlank();
    }

    public String tone() {
        return switch (mode) {
            case "direct_portal", "portal_with_fallback" -> "direct";
            case "official_request", "phone_assisted" -> "request";
            case "metadata_only", "jurisdiction_first" -> "verify";
            case "temporarily_unavailable" -> "blocked";
            default -> "neutral";
        };
    }

    public String primaryAccessNote() {
        return switch (countyKey) {
            case "NY::suffolk-county" -> "Starts a phone call. Have the Tax Map number and construction year ready.";
            default -> "";
        };
    }

    public String secondaryAccessNote() {
        return switch (countyKey) {
            case "VA::prince-william-county" -> "Optional fallback. The VDH request site may require browser verification or return an access block.";
            case "TX::tarrant-county" -> "Optional formal-request fallback. JustFOIA may return an access block; use the OSSF office first.";
            case "NC::lincoln-county" -> "Optional submission portal. NextRequest may return an access block; the Environmental Health page and phone remain available.";
            case "NY::suffolk-county" -> "The county instructions page may reject some browsers. The phone route above is the working action.";
            default -> "";
        };
    }

    public boolean primaryOpensNewWindow() {
        return primaryUrl != null && primaryUrl.startsWith("http");
    }
}
