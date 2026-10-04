package com.example.septic.service;

import java.util.LinkedHashMap;
import java.util.Map;

/** Internal intake summary. Never marks contact, agency findings or a buyer as verified. */
public final class CommercialInquiryBrief {
    private CommercialInquiryBrief() {}

    public static Map<String, Object> fromIntake(Map<String, Object> intake) {
        boolean records = intake.containsKey("requestId");
        Map<?, ?> property = object(intake.get("property"));
        Map<?, ?> contact = object(intake.get("contact"));
        Map<?, ?> inputs = object(intake.get("userInputs"));
        Map<String, Object> brief = new LinkedHashMap<>();
        brief.put("version", "commercial-inquiry-brief-v1");
        brief.put("visibility", "internal_only");
        brief.put("inquiryClass", records ? "record_research_request" : "service_inquiry");
        brief.put("reference", intake.get(records ? "requestId" : "leadId"));
        brief.put("receivedAt", intake.get("submittedAt"));
        brief.put("state", value(records ? property.get("stateCode") : intake.get("stateCode")));
        brief.put("county", value(records ? property.get("countyName") : intake.get("countyName")));
        brief.put("purpose", value(records ? property.get("helpPurpose") : intake.get("projectType")));
        brief.put("customerRole", value(contact.get("transactionRole")));
        brief.put("timeframe", value(records ? property.get("timeline") : inputs.get("timeline")));
        brief.put("reportedRecordStatus", value(records ? property.get("recordStatus") : intake.get("recordStatus")));
        brief.put("phoneSupplied", !value(contact.get("phone")).isEmpty());
        brief.put("emailSupplied", !value(contact.get("email")).isEmpty());
        brief.put("phoneVerification", "not_verified");
        brief.put("bookingStatus", "not_confirmed");
        brief.put("agencyFindings", "not_reviewed_in_this_intake");
        brief.put("buyerAcceptance", "not_requested");
        brief.put("attachmentsIncluded", false);
        brief.put("nextAction", "Review buyer service area, actual consent scope and inquiry definition before any sharing.");
        return brief;
    }

    public static Map<String, Object> initialFollowup() {
        Map<String, Object> followup = new LinkedHashMap<>();
        followup.put("status", "unreviewed");
        followup.put("phoneVerification", "not_verified");
        followup.put("fieldWorkIntent", "not_confirmed");
        followup.put("buyerAcceptance", "not_requested");
        followup.put("providerId", "");
        followup.put("paidAmountCents", 0);
        return followup;
    }

    private static Map<?, ?> object(Object value) { return value instanceof Map<?, ?> map ? map : Map.of(); }
    private static String value(Object value) { return value instanceof String text ? text : ""; }
}
