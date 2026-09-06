package com.example.septic.web;

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

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.storage.root=./build/test-storage",
                "app.site.base-url=https://example.test"
        }
)
class StateRecordsBrowserFunnelRegressionTest {

    @LocalServerPort
    private int port;

    // Regression: ISSUE-STATE-FUNNEL-001 — state route results and request fallbacks could be
    // invisible to assistive technology, and a fallback silently stopped after a reload.
    // Found by /qa on 2026-09-06.
    // Report: .gstack/qa-reports/qa-report-septicpath-state-funnels-2026-09-06.md
    @Test
    void keepsEveryDedicatedStateRouteUsableThroughValidationResultReloadAndFallback() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=390,844");

        WebDriver driver = new ChromeDriver(options);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            for (StateRoute route : routes()) {
                driver.get(baseUrl() + route.path());
                ((JavascriptExecutor) driver).executeScript("localStorage.clear(); sessionStorage.clear(); location.reload();");
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(route.countySelector())));

                WebElement county = driver.findElement(By.cssSelector(route.countySelector()));
                click(driver, driver.findElement(By.cssSelector(route.formSelector() + " button[type='submit']")));
                WebElement error = driver.findElement(By.id(route.errorId()));
                assertThat(county.getAttribute("aria-invalid")).as(route.stateCode()).isEqualTo("true");
                assertThat(county.getAttribute("aria-describedby")).as(route.stateCode()).contains(route.errorId());
                assertThat(driver.switchTo().activeElement()).as(route.stateCode()).isEqualTo(county);
                assertThat(error.getText()).as(route.stateCode()).isNotBlank();

                new Select(county).selectByIndex(1);
                WebElement clue = driver.findElement(By.cssSelector(route.clueSelector()));
                clue.sendKeys("123 Main Street");
                assertThat(county.getAttribute("aria-invalid")).as(route.stateCode()).isNull();
                click(driver, driver.findElement(By.cssSelector(route.formSelector() + " button[type='submit']")));

                WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(route.resultSelector())));
                WebElement resultTitle = result.findElement(By.cssSelector("h2"));
                assertThat(driver.switchTo().activeElement()).as(route.stateCode()).isEqualTo(resultTitle);
                assertInViewport(driver, resultTitle, route.stateCode() + " result");

                driver.navigate().refresh();
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(route.countySelector())));
                click(driver, driver.findElement(By.cssSelector("[data-state-fallback-outcome~='not_found_online'] summary")));
                click(driver, driver.findElement(By.cssSelector(route.requestButtonSelector())));

                WebElement request = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(route.requestSelector())));
                WebElement requestTitle = request.findElement(By.cssSelector("h2"));
                assertThat(driver.switchTo().activeElement()).as(route.stateCode()).isEqualTo(requestTitle);
                assertInViewport(driver, requestTitle, route.stateCode() + " request");
                assertThat(request.findElement(By.cssSelector("textarea")).getAttribute("value"))
                        .as(route.stateCode())
                        .contains("123 Main Street");
                assertThat(request.findElement(By.cssSelector(route.safetyNetSelector())).getAttribute("href"))
                        .as(route.stateCode() + " official safety net")
                        .contains(route.safetyNetHref());
            }
        } finally {
            driver.quit();
        }
    }

    private void assertInViewport(WebDriver driver, WebElement element, String label) {
        Number top = (Number) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].getBoundingClientRect().top;", element
        );
        Number height = (Number) ((JavascriptExecutor) driver).executeScript("return window.innerHeight;");
        assertThat(top.doubleValue()).as(label).isBetween(0.0, height.doubleValue());
    }

    private void click(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private List<StateRoute> routes() {
        return List.of(
                new StateRoute("NC", "/north-carolina-septic-permit-lookup/", "[data-nc-route-form]", "[data-nc-county]", "[data-nc-clue]", "nc-form-error", "[data-nc-result]", "[data-nc-open-request]", "[data-nc-request-section]", "a[href*='dph.ncdhhs.gov/environmental-health/ehs-directory']", "dph.ncdhhs.gov"),
                new StateRoute("SC", "/dhec-septic-permit-lookup/", "[data-sc-route-form]", "[data-sc-county]", "[data-sc-clue]", "sc-form-error", "[data-sc-result]", "[data-sc-open-request]", "[data-sc-request-section]", "a[href^='mailto:']", "OSWWCentral@des.sc.gov"),
                new StateRoute("FL", "/florida-ostds-permit-lookup/", "[data-fl-route-form]", "[data-fl-county]", "[data-fl-clue]", "fl-form-error", "[data-fl-result]", "[data-fl-open-request]", "[data-fl-request-section]", "a[href*='floridahealth.gov']", "floridahealth.gov"),
                new StateRoute("TX", "/texas-ossf-records-search/", "[data-tx-route-form]", "[data-tx-county]", "[data-tx-clue]", "tx-form-error", "[data-tx-result]", "[data-tx-open-request]", "[data-tx-request-section]", "a[href*='tceq.texas.gov/oars']", "tceq.texas.gov/oars")
        );
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private record StateRoute(
            String stateCode,
            String path,
            String formSelector,
            String countySelector,
            String clueSelector,
            String errorId,
            String resultSelector,
            String requestButtonSelector,
            String requestSelector,
            String safetyNetSelector,
            String safetyNetHref
    ) {}
}
