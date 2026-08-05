package com.example.septic.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DocumentDecisionWorkspaceUiRegressionTest {

    @Test
    void documentWorkspaceRoutesEachOutcomeToAConcreteNextDecision() throws IOException {
        String script = Files.readString(
                Path.of("src/main/resources/static/app.js"),
                StandardCharsets.UTF_8
        );

        assertThat(script).contains("function workspaceNextDecision(summary)");
        assertThat(script).contains("No record is not no system");
        assertThat(script).contains("Separate past repair history from the current problem");
        assertThat(script).contains("Turn the file into a site-specific replacement scope");
        assertThat(script).contains("Use the records to scope the inspection, not replace it");
        assertThat(script).contains("Compare the approved bedroom count with the intended use");
        assertThat(script).contains("document_decision_selected");
        assertThat(script).contains("recordStatus");
        assertThat(script).contains("county");
    }
}
