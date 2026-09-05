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
class TennesseeRecordHelpBrowserRegressionTest {

    private static final String INPUT_ADDRESS = "2163 Sugar Grove Valley Rd, Harriman, TN 37748";
    private static final String MATCHED_ADDRESS = "2163 SUGAR GROVE VALLEY RD, HARRIMAN, TN, 37748";

    @LocalServerPort
    private int port;

    @MockitoBean
    private CensusAddressLookupService censusAddressLookupService;

    // Regression: ISSUE-001 and ISSUE-002 — verify the real browser journey across both page seams.
    // Found by /qa on 2026-09-06 after the first real Roane County record-help request.
    // Report: .gstack/qa-reports/qa-report-septicpath-record-help-handoff-2026-09-06.md
    @Test
    void preservesTheResolvedPropertyAcrossTheTdecAndHelpHandoffs() {
        when(censusAddressLookupService.lookup(anyString())).thenReturn(
                new CensusAddressLookupService.CensusAddressLookupResult(
                        CensusAddressLookupService.CensusAddressLookupResult.Status.MATCHED,
                        MATCHED_ADDRESS,
                        "TN",
                        "Roane"
                )
        );

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,1200");

        WebDriver driver = new ChromeDriver(options);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            String statePage = baseUrl() + "/septic-records-checklist/tennessee/";

            driver.get(statePage);
            new Select(driver.findElement(By.cssSelector("[data-address-record-finder-purpose]")))
                    .selectByValue("location");
            driver.findElement(By.cssSelector("[data-address-record-finder-input]"))
                    .sendKeys(INPUT_ADDRESS);
            driver.findElement(By.cssSelector("[data-address-record-finder-submit]"))
                    .click();

            WebElement route = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space()='Continue with Roane County']")
            ));
            String routeHref = route.getAttribute("href");
            assertThat(routeHref)
                    .contains("/tdec-septic-records/?county=roane")
                    .contains("address=")
                    .contains("purpose=location")
                    .doesNotContain("/septic-records-checklist/tennessee/#");

            route.click();
            wait.until(ExpectedConditions.urlContains("/tdec-septic-records/"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-tdec-result]")));

            Select county = new Select(driver.findElement(By.cssSelector("[data-tdec-county]")));
            assertThat(county.getFirstSelectedOption().getAttribute("value")).isEqualTo("roane");
            assertThat(driver.findElement(By.cssSelector("[data-tdec-address]"))
                    .getAttribute("value")).isEqualTo(MATCHED_ADDRESS);
            assertThat(driver.findElement(By.cssSelector("[data-tdec-result-title]"))
                    .getText()).isEqualTo("Roane County record route");
            assertThat(driver.findElements(By.linkText("Contact Knoxville if no file appears"))).hasSize(1);

            driver.get(statePage);
            new Select(driver.findElement(By.cssSelector("[data-address-record-finder-purpose]")))
                    .selectByValue("location");
            driver.findElement(By.cssSelector("[data-address-record-finder-input]"))
                    .sendKeys(INPUT_ADDRESS);
            driver.findElement(By.cssSelector("[data-address-record-finder-submit]"))
                    .click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-address-record-finder-result]")));

            WebElement helpLink = driver.findElement(By.cssSelector("a[data-track-source-context='state_records_tn']"));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});",
                    helpLink
            );
            wait.until(ExpectedConditions.elementToBeClickable(helpLink));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", helpLink);
            wait.until(ExpectedConditions.urlContains("/offer-prep-septic-file-check/"));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("propertyAddress")));

            assertThat(driver.findElement(By.name("propertyAddress")).getAttribute("value"))
                    .isEqualTo(MATCHED_ADDRESS);
            assertThat(new Select(driver.findElement(By.name("stateCode")))
                    .getFirstSelectedOption().getAttribute("value")).isEqualTo("TN");
            assertThat(driver.findElement(By.name("countyName")).getAttribute("value"))
                    .isEqualTo("Roane");
        } finally {
            driver.quit();
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }
}
