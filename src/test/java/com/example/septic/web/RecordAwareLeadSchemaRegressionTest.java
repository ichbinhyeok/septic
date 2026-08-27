package com.example.septic.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RecordAwareLeadSchemaRegressionTest {

    @Test
    void quoteFormCarriesLocationRecordStateUrgencyAndContactPreference() throws IOException {
        QuoteLeadForm form = new QuoteLeadForm();
        form.setCountyName("Knox County");
        form.setRecordStatus("repair_record");
        form.setPreferredContactMethod("email");
        form.setServiceNeed("alarm");

        assertThat(form.getCountyName()).isEqualTo("Knox County");
        assertThat(form.getRecordStatus()).isEqualTo("repair_record");
        assertThat(form.getPreferredContactMethod()).isEqualTo("email");
        assertThat(form.getServiceNeed()).isEqualTo("alarm");
        assertThat(form.hasActiveProblem()).isTrue();

        String template = Files.readString(Path.of("src/main/jte/pages/calculator.jte"), StandardCharsets.UTF_8);
        String storage = Files.readString(
                Path.of("src/main/java/com/example/septic/service/LeadStorageService.java"),
                StandardCharsets.UTF_8
        );
        assertThat(template).contains("name=\"countyName\"");
        assertThat(template).contains("name=\"recordStatus\"");
        assertThat(template).contains("name=\"timeline\"");
        assertThat(template).contains("name=\"preferredContactMethod\"");
        assertThat(template).contains("name=\"serviceNeed\"");
        assertThat(storage).contains("project_type,service_need,county_name,record_status,preferred_contact_method,timeline");
        assertThat(storage).contains("\"serviceNeed\", quoteLeadForm.getServiceNeed()");
    }
}
