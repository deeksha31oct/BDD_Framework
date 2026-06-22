package Base;

import org.testng.Assert;
import org.testng.annotations.Test;
import utilites.ExtentReportManager;
import utilites.ScreenshotUtility;

/**
 * Sample test class demonstrating ExtentReport usage
 * This is a template for creating new test classes
 */
public class SampleTest extends BaseTest {

    @Test(description = "Sample test to demonstrate ExtentReport functionality")
    public void testSampleLogin() {
        try {
            // Step 1: Navigate to application
            ExtentReportManager.logInfo("Step 1: Navigating to application URL");
            driver.navigate().to("https://www.example.com");
            Thread.sleep(2000);
            
            // Step 2: Verify page title
            ExtentReportManager.logInfo("Step 2: Verifying page title");
            String pageTitle = driver.getTitle();
            ExtentReportManager.logInfo("Page Title: " + pageTitle);
            Assert.assertNotNull(pageTitle, "Page title should not be null");
            ExtentReportManager.logPass("Page title verified successfully");
            
            // Step 3: Capture screenshot on success
            ExtentReportManager.logInfo("Step 3: Capturing screenshot");
            ScreenshotUtility.captureScreenshot(driver, "PageLoaded");
            ExtentReportManager.attachScreenshot(driver, "PageLoaded");
            
            // Step 4: Log test completion
            ExtentReportManager.logPass("Test completed successfully");
            
        } catch (Exception e) {
            ExtentReportManager.logFail("Test failed with exception: " + e.getMessage());
            ExtentReportManager.logException(e);
            
            // Capture screenshot on failure
            try {
                ScreenshotUtility.captureScreenshot(driver, "TestFailure");
                ExtentReportManager.attachScreenshot(driver, "TestFailure");
            } catch (Exception screenshotException) {
                ExtentReportManager.logWarning("Failed to capture screenshot: " + screenshotException.getMessage());
            }
            
            throw new RuntimeException(e);
        }
    }

    @Test(description = "Smoke test - Verify application loads", groups = {"smoke"})
    public void testApplicationLoad() {
        try {
            ExtentReportManager.logInfo("Test: Verify application loads");
            driver.navigate().to("https://www.example.com");
            Thread.sleep(1000);
            
            String pageTitle = driver.getTitle();
            Assert.assertNotNull(pageTitle);
            
            ExtentReportManager.logPass("Application loaded successfully");
            
        } catch (Exception e) {
            ExtentReportManager.logFail("Application failed to load");
            ExtentReportManager.logException(e);
            throw new RuntimeException(e);
        }
    }

    @Test(description = "Regression test - Verify element presence", groups = {"regression"})
    public void testElementPresence() {
        try {
            ExtentReportManager.logInfo("Test: Verify element presence");
            driver.navigate().to("https://www.example.com");
            
            // Example: Verify page source contains expected content
            String pageSource = driver.getPageSource();
            Assert.assertTrue(pageSource.length() > 0);
            
            ExtentReportManager.logPass("Element presence verified");
            
        } catch (Exception e) {
            ExtentReportManager.logFail("Element not found");
            ExtentReportManager.logException(e);
            throw new RuntimeException(e);
        }
    }
}
