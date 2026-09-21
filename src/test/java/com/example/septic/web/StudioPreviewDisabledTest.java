package com.example.septic.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.storage.root=./build/test-storage")
class StudioPreviewDisabledTest {
    @Autowired ApplicationContext context;

    @Test void previewRequiresAnExplicitOptIn() {
        assertThat(context.getBeansOfType(StudioPreviewController.class)).isEmpty();
    }
}
