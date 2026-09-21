package com.example.septic.web;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StudioGuideRoutesTest {
    @Test void onlyExactNationalEquivalentsAreMapped() {
        for (String slug : StudioGuideRoutes.RECORDS.keySet()) {
            assertThat(StudioGuideRoutes.preview("/" + slug + "/")).isEqualTo("/design-preview/studio/topics/" + slug + "/");
            assertThat(StudioGuideRoutes.preview("/" + slug + "/tennessee/")).isEqualTo("/" + slug + "/tennessee/");
        }
        for (String slug : StudioGuideRoutes.PLANNING.keySet()) {
            assertThat(StudioGuideRoutes.preview("/" + slug + "/")).isEqualTo("/design-preview/studio/topics/" + slug + "/");
        }
        assertThat(StudioGuideRoutes.preview("/septic-system-cost-calculator/?state=TN&bedrooms=4#quote-request"))
                .isEqualTo("/design-preview/studio/calculator/?state=TN&bedrooms=4#quote-request");
        assertThat(StudioGuideRoutes.preview("/septic-system-cost-calculator/texas/?from=guide#cost-scope"))
                .isEqualTo("/design-preview/studio/texas/?from=guide#cost-scope");
        assertThat(StudioGuideRoutes.preview("https://example.com/septic-as-built-records/"))
                .isEqualTo("https://example.com/septic-as-built-records/");
        assertThat(StudioGuideRoutes.preview("/drain-field-estimator/")).isEqualTo("/design-preview/studio/calculator/?projectType=drainfield_replacement");
        assertThat(StudioGuideRoutes.preview("/drain-field-estimator/?state=GA")).isEqualTo("/design-preview/studio/calculator/?projectType=drainfield_replacement&state=GA");
        assertThat(StudioGuideRoutes.preview("/septic-tank-size-estimator/?state=GA")).isEqualTo("/design-preview/studio/calculator/?mode=tank_size&state=GA");
        assertThat(StudioGuideRoutes.preview("/septic-pump-schedule-estimator/")).isEqualTo("/design-preview/studio/calculator/?mode=pump_schedule");
        assertThat(StudioGuideRoutes.preview("/septic-records-checklist/?source=guide#prepare"))
                .isEqualTo("/design-preview/studio/checklist/?source=guide#prepare");
        assertThat(StudioGuideRoutes.preview("/septic-records-checklist/alabama/"))
                .isEqualTo("/design-preview/studio/records/alabama/");
        assertThat(StudioGuideRoutes.preview("/septic-permit-lookup/?from=guide"))
                .isEqualTo("/design-preview/studio/permit-lookup/?from=guide");
        assertThat(StudioGuideRoutes.preview("/tdec-septic-records/?from=guide#county-route"))
                .isEqualTo("/design-preview/studio/tdec-records/?from=guide#county-route");
        assertThat(StudioGuideRoutes.preview("/official-septic-lookup-tools/?from=guide#route-finder"))
                .isEqualTo("/design-preview/studio/official-lookup-tools/?from=guide#route-finder");
        assertThat(StudioGuideRoutes.preview("/septic-records-checklist/tennessee/"))
                .isEqualTo("/design-preview/studio/records/tennessee/");
        assertThat(StudioGuideRoutes.preview("/septic-records-checklist/tennessee/?from=guide#county-pages"))
                .isEqualTo("/design-preview/studio/records/tennessee/?from=guide#county-pages");
        assertThat(StudioGuideRoutes.preview("/septic-records-checklist/tennessee/davidson-county/"))
                .isEqualTo("/design-preview/studio/tennessee/davidson-county/");
        assertThat(StudioGuideRoutes.preview("/north-carolina-septic-permit-lookup/"))
                .isEqualTo("/design-preview/studio/north-carolina-records/");
        assertThat(StudioGuideRoutes.preview("/septic-permit-process/alabama/"))
                .isEqualTo("/design-preview/studio/permit-process/alabama/");
        assertThat(StudioGuideRoutes.preview("/septic-permit-process/oregon/?from=guide#steps"))
                .isEqualTo("/design-preview/studio/permit-process/oregon/?from=guide#steps");
        assertThat(StudioGuideRoutes.preview("/septic-inspection-cost/texas/"))
                .isEqualTo("/design-preview/studio/inspection-cost/texas/");
        assertThat(StudioGuideRoutes.preview("/buying-a-house-with-a-septic-system/oregon/?from=guide"))
                .isEqualTo("/design-preview/studio/buying-guide/oregon/?from=guide");
        assertThat(StudioGuideRoutes.preview("/septic-replacement-cost/georgia/?from=guide#scope"))
                .isEqualTo("/design-preview/studio/replacement-cost/georgia/?from=guide#scope");
        assertThat(StudioGuideRoutes.preview("/perc-test-cost/oregon/?from=guide#soil-path"))
                .isEqualTo("/design-preview/studio/perc-test-cost/oregon/?from=guide#soil-path");
        assertThat(StudioGuideRoutes.preview("/drain-field-replacement-cost/pennsylvania/?from=guide#field-scope"))
                .isEqualTo("/design-preview/studio/drain-field-cost/pennsylvania/?from=guide#field-scope");
        assertThat(StudioGuideRoutes.preview("/failed-perc-test-septic/oregon/?from=guide"))
                .isEqualTo("/design-preview/studio/failed-perc/oregon/?from=guide");
        assertThat(StudioGuideRoutes.preview("/septic-replacement-area/washington/?from=guide"))
                .isEqualTo("/design-preview/studio/replacement-area/washington/?from=guide");
        assertThat(StudioGuideRoutes.preview("/wet-yard-over-septic-drain-field/florida/?from=guide"))
                .isEqualTo("/design-preview/studio/wet-yard/florida/?from=guide");
    }
}
