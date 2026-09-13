package com.example.septic.web;

import com.example.septic.service.CensusAddressLookupService;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.storage.root=./build/qa-invalid-address-test-storage",
                "app.site.base-url=https://example.test"
        }
)
class QaInvalidAddressStateRegressionTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private CensusAddressLookupService censusAddressLookupService;

    @Test
    void hidesPropertyActionsWhenAValidRouteIsFollowedByAnInvalidAddress() {
        // Regression: ISSUE-001 — invalid input retained property actions from the previous county route.
        // Found by /qa on 2026-09-14
        // Report: .gstack/qa-reports/qa-report-localhost-2026-09-14.md
        when(censusAddressLookupService.lookup(anyString())).thenReturn(
                new CensusAddressLookupService.CensusAddressLookupResult(
                        CensusAddressLookupService.CensusAddressLookupResult.Status.MATCHED,
                        "1301 2ND AVE, CONWAY, SC, 29526",
                        "SC",
                        "Horry"
                )
        );

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,1000");
        WebDriver driver = new ChromeDriver(options);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.get("http://localhost:" + port + "/");
            WebElement address = driver.findElement(By.cssSelector("[data-address-record-finder-input]"));
            WebElement submit = driver.findElement(By.cssSelector("[data-address-record-finder-submit]"));

            address.sendKeys("1301 2nd Ave, Conway, SC 29526");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.cssSelector("[data-address-record-finder-heading]"),
                    "Horry County"
            ));
            assertThat(driver.findElement(By.cssSelector("[data-record-search-packet]")).isDisplayed()).isTrue();

            address.clear();
            address.sendKeys("asdf");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.cssSelector("[data-address-record-finder-heading]"),
                    "Enter a full U.S. property address"
            ));

            WebElement result = driver.findElement(By.cssSelector("[data-address-record-finder-result]"));
            assertThat(result.getText()).doesNotContain("Get help with this property", "Copy details", "Office contact");
            assertThat(driver.findElement(By.cssSelector("[data-record-search-packet]")).isDisplayed()).isFalse();
            assertThat(driver.findElement(By.cssSelector(".record-finder__route-details")).isDisplayed()).isFalse();
        } finally {
            driver.quit();
        }
    }
}
