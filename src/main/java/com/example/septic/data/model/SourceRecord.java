package com.example.septic.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SourceRecord(
        @JsonProperty("source_id") String sourceId,
        @JsonProperty("state_code") String stateCode,
        @JsonProperty("source_type") String sourceType,
        @JsonProperty("agency_name") String agencyName,
        @JsonProperty("title") String title,
        @JsonProperty("url") String url,
        @JsonProperty("effective_date") String effectiveDate,
        @JsonProperty("draft_or_final_status") String draftOrFinalStatus,
        @JsonProperty("county_or_local") String countyOrLocal,
        @JsonProperty("last_verified_at") String lastVerifiedAt,
        @JsonProperty("notes") String notes,
        @JsonProperty("trust_level") String trustLevel,
        @JsonProperty("last_http_checked_at") String lastHttpCheckedAt,
        @JsonProperty("http_check_status") String httpCheckStatus,
        @JsonProperty("last_content_verified_at") String lastContentVerifiedAt,
        @JsonProperty("verification_method") String verificationMethod,
        @JsonProperty("review_status") String reviewStatus
) {
    public String contentVerifiedAt() {
        return hasText(lastContentVerifiedAt) ? lastContentVerifiedAt : lastVerifiedAt;
    }

    public boolean hasHttpCheck() {
        return hasText(lastHttpCheckedAt) && hasText(httpCheckStatus);
    }

    public String httpCheckLabel() {
        if (!hasHttpCheck()) {
            return "HTTP check pending";
        }
        return switch (httpCheckStatus.toLowerCase()) {
            case "healthy" -> "HTTP reachable";
            case "blocked" -> "Automated check blocked";
            case "rate_limited" -> "Automated check rate-limited";
            case "transient" -> "Temporary server error";
            case "dead" -> "Confirmed dead link";
            default -> "Automated check inconclusive";
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
