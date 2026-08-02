package com.example.septic.web;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AlabamaCountyActionLayoutRegressionTest {

    @Test
    void countyOfficialActionsWrapWithReadableSpacing() throws Exception {
        String pagesCss = Files.readString(Path.of("src/main/resources/static/pages.css"));

        assertTrue(pagesCss.contains(
                ".alabama-perc-scope__county-handoff > div, .alabama-county-handoff__actions { display: flex; flex-wrap: wrap; gap: 10px 18px; }"
        ));
    }
}
