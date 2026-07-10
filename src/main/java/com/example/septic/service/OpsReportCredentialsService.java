package com.example.septic.service;

import com.example.septic.config.AppStorageProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;
import org.springframework.stereotype.Service;

@Service
public class OpsReportCredentialsService {
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String OPS_USERNAME = "ops";

    private final AppStorageProperties storageProperties;
    private final SecureRandom secureRandom = new SecureRandom();
    private OpsReportCredentials credentials;

    public OpsReportCredentialsService(AppStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @PostConstruct
    void loadCredentials() {
        Path credentialsFile = credentialsFile();
        try {
            Files.createDirectories(credentialsFile.getParent());
            credentials = Files.exists(credentialsFile) ? readCredentials(credentialsFile) : createCredentials(credentialsFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize the private operations report credentials", exception);
        }
    }

    public OpsReportCredentials credentials() {
        return credentials;
    }

    private OpsReportCredentials createCredentials(Path credentialsFile) throws IOException {
        byte[] secret = new byte[24];
        secureRandom.nextBytes(secret);
        OpsReportCredentials generated = new OpsReportCredentials(
                OPS_USERNAME,
                Base64.getUrlEncoder().withoutPadding().encodeToString(secret)
        );
        Properties properties = new Properties();
        properties.setProperty(USERNAME_KEY, generated.username());
        properties.setProperty(PASSWORD_KEY, generated.password());
        try (var output = Files.newOutputStream(credentialsFile, StandardOpenOption.CREATE_NEW)) {
            properties.store(output, "Private SepticPath operations report credentials");
        } catch (java.nio.file.FileAlreadyExistsException exception) {
            return readCredentials(credentialsFile);
        }
        return generated;
    }

    private OpsReportCredentials readCredentials(Path credentialsFile) throws IOException {
        Properties properties = new Properties();
        try (var input = Files.newInputStream(credentialsFile)) {
            properties.load(input);
        }
        String username = properties.getProperty(USERNAME_KEY, "").trim();
        String password = properties.getProperty(PASSWORD_KEY, "").trim();
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Private operations report credentials are incomplete");
        }
        return new OpsReportCredentials(username, password);
    }

    private Path credentialsFile() {
        return Path.of(storageProperties.root()).resolve("ops-report-credentials.properties");
    }

    public record OpsReportCredentials(String username, String password) {
    }
}
