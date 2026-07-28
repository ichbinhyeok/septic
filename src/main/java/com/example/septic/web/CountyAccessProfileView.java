package com.example.septic.web;

import java.util.List;

public record CountyAccessProfileView(
        String countyKey,
        boolean countySpecific,
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
    public String profileScope() {
        return countySpecific ? "county_specific" : "official_starting_point";
    }

    public String scopeLabel() {
        return countySpecific ? "County-specific workflow" : "Official starting point only";
    }

    public String scopeSummary() {
        if (countySpecific) {
            return "The route and handoff below were reviewed for this county. The government site or office still controls the final search, signature, payment, and submission.";
        }
        return "We confirmed an official place to start, but not a county-specific intake form, required field set, fee, or processing time. Prepare the property clues here, then follow only what the official source currently asks.";
    }

    public String capabilityTier() {
        return switch (mode) {
            case "direct_portal" -> "search_now";
            case "portal_with_fallback", "metadata_only" -> "search_then_request";
            case "official_request" -> "request_records";
            case "phone_assisted", "jurisdiction_first", "temporarily_unavailable" -> "office_help";
            default -> "official_start";
        };
    }

    public String capabilityLabel() {
        return switch (capabilityTier()) {
            case "search_now" -> "Search online now";
            case "search_then_request" -> "Search first, request if missing";
            case "request_records" -> "Prepare an official request";
            case "office_help" -> "Office help required";
            default -> "Open the official next step";
        };
    }

    public String officialAccessLabel() {
        return switch (mode) {
            case "direct_portal" -> "Direct official search";
            case "portal_with_fallback" -> "Search with official fallback";
            case "metadata_only" -> "Permit metadata only";
            case "official_request" -> "Official request required";
            case "phone_assisted" -> "Phone or office intake";
            case "jurisdiction_first" -> "Authority must be confirmed";
            case "temporarily_unavailable" -> "Primary route unavailable";
            default -> "Official starting route";
        };
    }

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
            case "CA::san-bernardino-county" -> "Opens the current NextRequest portal linked by Environmental Health. It returned a Cloudflare security block in our automated browser.";
            default -> "";
        };
    }

    public String secondaryAccessNote() {
        return switch (countyKey) {
            case "VA::prince-william-county" -> "Optional fallback. The VDH request site may require browser verification or return an access block.";
            case "TX::tarrant-county" -> "Optional formal-request fallback. JustFOIA may return an access block; use the OSSF office first.";
            case "NC::lincoln-county" -> "Optional submission portal. NextRequest may return an access block; the Environmental Health page and phone remain available.";
            case "NY::suffolk-county" -> "The current office page confirms the phone number, Yaphank counter, hours, and online application help.";
            case "CA::san-bernardino-county" -> "Starts a call. Keep the prepared property and record-scope fields open so you only need to confirm the current intake.";
            default -> "";
        };
    }

    public boolean primaryOpensNewWindow() {
        return primaryUrl != null && primaryUrl.startsWith("http");
    }
}
