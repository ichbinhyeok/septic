package com.example.septic.web;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class RecordResultNextStepTest {
    @Test void allPurposesHavePracticalGuidanceAndOnlyAppropriateHandoffs() {
        for (String purpose : java.util.List.of("location", "inspection", "pumping", "repair", "building", "records", "unsure")) {
            var step = RecordResultNextStep.forPurpose(purpose);
            assertThat(step.checklist()).hasSize(3);
            assertThat(step.heading()).isNotBlank();
            assertThat(step.servicePath()).doesNotContain("email", "address", "downloadToken", "bedrooms", "serviceNeed");
        }
        assertThat(RecordResultNextStep.forPurpose("records").offersService()).isFalse();
        assertThat(RecordResultNextStep.forPurpose("repair").servicePath()).contains("projectType=diagnosis");
        assertThat(RecordResultNextStep.forPurpose("building").servicePath()).doesNotContain("projectType=");
        assertThat(RecordResultNextStep.forPurpose("pumping").servicePath()).contains("projectType=pumping");
    }

    @Test void oldDeliveriesDefaultToGeneralGuidanceWithoutGuessingPurpose() {
        assertThat(RecordResultNextStep.forPurpose(null).purpose()).isEqualTo("unsure");
        assertThat(RecordResultNextStep.forPurpose("").purpose()).isEqualTo("unsure");
        assertThatThrownBy(() -> RecordResultNextStep.forPurpose("<script>bad</script>"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
