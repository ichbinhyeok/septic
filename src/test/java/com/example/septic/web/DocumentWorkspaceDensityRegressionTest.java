package com.example.septic.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DocumentWorkspaceDensityRegressionTest {

    @Test
    void evidenceAndChecklistsStayBehindExplicitDisclosure() throws IOException {
        String script = Files.readString(
                Path.of("src/main/resources/static/app.js"),
                StandardCharsets.UTF_8
        );

        assertThat(script).contains("const checklistSection = document.createElement(\"details\")");
        assertThat(script).contains("const checklistHeading = document.createElement(\"summary\")");
        assertThat(script).contains("const verificationSection = document.createElement(\"details\")");
        assertThat(script).contains("outcomeLimitLabel.textContent = \"Does not prove\"");
        assertThat(script).doesNotContain("What this response establishes");
    }
}
