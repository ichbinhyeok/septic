package com.example.septic.web;

import com.example.septic.service.CensusAddressLookupService;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.storage.root=./build/test-storage",
                "app.site.base-url=https://example.test"
        }
)
class QaRecordIntentHandoffRegressionTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private CensusAddressLookupService censusAddressLookupService;

    // Regression: ISSUE-002 — preserve the user's decision intent at the human-help handoff.
    // Found by /qa on 2026-09-14 while testing the approved-bedroom-count journey.
    // Report: .gstack/qa-reports/qa-report-localhost-2026-09-14.md
    @Test
    void carriesBedroomResearchIntentIntoTheHumanReviewForm() {
        when(censusAddressLookupService.lookup(anyString())).thenReturn(
                new CensusAddressLookupService.CensusAddressLookupResult(
                        CensusAddressLookupService.CensusAddressLookupResult.Status.MATCHED,
                        "800 MARKET ST, KNOXVILLE, TN, 37902",
                        "TN",
                        "Knox County"
                )
        );

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,1200");

        WebDriver driver = new ChromeDriver(options);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.get("http://localhost:" + port + "/");

            new Select(driver.findElement(By.cssSelector("[data-address-record-finder-purpose]")))
                    .selectByValue("bedrooms");
            driver.findElement(By.cssSelector("[data-address-record-finder-input]"))
                    .sendKeys("800 Market St, Knoxville, TN 37902");
            WebElement submit = driver.findElement(By.cssSelector("[data-address-record-finder-submit]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);

            WebElement help = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-address-record-finder-result] [data-record-help-cta]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", help);

            wait.until(ExpectedConditions.urlContains("/offer-prep-septic-file-check/"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("propertyAddress")));

            assertThat(driver.findElement(By.name("propertyAddress")).getAttribute("value"))
                    .isEqualTo("800 MARKET ST, KNOXVILLE, TN, 37902");
            assertThat(new Select(driver.findElement(By.name("stateCode")))
                    .getFirstSelectedOption().getAttribute("value")).isEqualTo("TN");
            assertThat(driver.findElement(By.name("countyName")).getAttribute("value"))
                    .isEqualTo("Knox County");
            assertThat(driver.findElement(By.name("recordType")).getAttribute("value"))
                    .isEqualTo("septic");
            assertThat(new Select(driver.findElement(By.name("researchGoal")))
                    .getFirstSelectedOption().getAttribute("value")).isEqualTo("design_capacity");
            assertThat(new Select(driver.findElement(By.name("recordStatus")))
                    .getFirstSelectedOption().getAttribute("value")).isEqualTo("not_started");
        } finally {
            driver.quit();
        }
    }
}
