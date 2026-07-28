package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeadStoragePrivacyScrubTest {

    @TempDir
    Path storageRoot;

    @Test
    void startupScrubsHistoricalPropertyQueriesWithoutLosingWorkflowStages() throws Exception {
        Path eventFile = storageRoot.resolve("events/2026/07/28.ndjson");
        Files.createDirectories(eventFile.getParent());
        Files.writeString(eventFile, """
                {"eventType":"workflow_stage","sourcePage":"/county/?address=123%20Main&workflowRunId=query-secret&src=gsc","targetPath":"/next/?parcel=PIN-44&purpose=buying","workflowRunId":"safe-random-run-id","stage":"official_route_opened","provenance":{"sourcePage":"/county/?address=123%20Main&src=gsc","queryString":"address=123%20Main&src=gsc","referrer":"https://septicpath.com/county/?address=123%20Main&workflowRunId=query-secret&src=gsc"}}
                """, StandardCharsets.UTF_8);

        LeadStorageService service = new LeadStorageService(new AppStorageProperties(storageRoot.toString()));
        service.initializeDirectories();

        String scrubbed = Files.readString(eventFile);
        assertFalse(scrubbed.contains("123%20Main"));
        assertFalse(scrubbed.contains("query-secret"));
        assertFalse(scrubbed.contains("PIN-44"));
        assertTrue(scrubbed.contains("\"workflowRunId\":\"safe-random-run-id\""));
        assertTrue(scrubbed.contains("src=gsc"));
        assertTrue(scrubbed.contains("purpose=buying"));
        assertTrue(scrubbed.contains("\"stage\":\"official_route_opened\""));
    }
}
