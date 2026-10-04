package com.example.septic.web;

import java.util.List;
import java.util.Set;

/** Preparation guidance, not a finding about the property or a work order. */
public record RecordResultNextStep(String purpose, String heading, String introduction,
                                   List<String> checklist, String projectType, String action) {
    private static final Set<String> PURPOSES = Set.of("location", "inspection", "pumping", "repair", "building", "records", "unsure");

    public static String normalize(String value) {
        if (value == null || value.isBlank()) return "unsure";
        if (!PURPOSES.contains(value)) throw new IllegalArgumentException("Choose a valid record-help purpose");
        return value;
    }

    public static RecordResultNextStep forPurpose(String value) {
        String purpose = normalize(value);
        return switch (purpose) {
            case "location" -> new RecordResultNextStep(purpose, "Prepare for a tank or field locate.",
                    "Use the source drawing as a starting point for an on-site discussion.", List.of(
                    "Keep the drawing with its date, legend and any recorded offsets.",
                    "Ask whether the service locates the tank only, or also the drain field and access points.",
                    "Have buried components confirmed on site before digging; an old drawing is not a current locate."),
                    "location", "Ask about a locate");
            case "inspection" -> new RecordResultNextStep(purpose, "Bring a clearer file to the inspection.",
                    "The records help an inspector understand the documented system before checking its present condition.", List.of(
                    "Keep permits, plans, final approvals and repair entries in date order.",
                    "Point out differences between the listing, permit and final record without treating them as resolved.",
                    "Ask the inspector what the visit covers and share any transaction deadline; records are not an inspection."),
                    "inspection", "Ask about an inspection");
            case "pumping" -> new RecordResultNextStep(purpose, "Prepare for pumping or maintenance.",
                    "Pass on useful record details so the provider can plan the visit.", List.of(
                    "Share recorded tank capacity and any access or location clues, identifying their source.",
                    "Tell the provider if lids or access points have not been located on site.",
                    "Ask whether locating, lid access and disposal are included in the quote; a permit does not show the last pump-out."),
                    "pumping", "Ask about pumping");
            case "repair" -> new RecordResultNextStep(purpose, "Prepare for an on-site diagnosis.",
                    "Use the record history to give the professional context, not to diagnose the present problem.", List.of(
                    "Describe the current symptoms and when they started separately from historical record findings.",
                    "Keep previous repair or replacement approvals available for the visit.",
                    "Ask what investigation comes before a repair quote. Active wastewater problems should not wait for a records search."),
                    "diagnosis", "Ask about a site assessment");
            case "building" -> new RecordResultNextStep(purpose, "Prepare for the project conversation.",
                    "Bring the existing file into your planning discussion before deciding where work can go.", List.of(
                    "Keep any recorded tank, field and reserve-area layout with the original source.",
                    "Describe the proposed addition, pool or installation separately from the existing approval.",
                    "Ask the responsible authority and qualified professional what site checks or approvals the new work needs."),
                    "", "Ask about the project");
            case "records" -> new RecordResultNextStep(purpose, "Keep a useful property record file.",
                    "If the records answer your question, there is no need to request a service visit.", List.of(
                    "Download the reviewed package before this private link expires.",
                    "Keep the original documents alongside the explanation and its limits.",
                    "Reply with your request reference if something in the file needs clarification."),
                    "", "");
            default -> new RecordResultNextStep(purpose, "Choose the next step that fits your question.",
                    "Start with what the reviewed file answers and what remains open.", List.of(
                    "Save the source records and explanation together.",
                    "Reply with your request reference if you need help understanding the result.",
                    "If you still need a locate, inspection, pumping or repair, we can review local service options."),
                    "", "Start a service inquiry");
        };
    }

    public boolean offersService() { return !action.isEmpty(); }

    public String servicePath() {
        return "/septic-system-cost-calculator/?quoteMode=true"
                + (projectType.isEmpty() ? "" : "&projectType=" + projectType)
                + "&sourcePageHint=/record-result/#quote-request";
    }
}
