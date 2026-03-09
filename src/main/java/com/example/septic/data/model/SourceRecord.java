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
        @JsonProperty("trust_level") String trustLevel
) {
}
