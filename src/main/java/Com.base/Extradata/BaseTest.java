package Com.base.Extradata;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.WebDriver;
import Com.base.testng.ExtentReportManager;

/**
 * BaseTest class serves as the parent class for all test classes
 * It handles initialization of ExtentReports and WebDriver
 */
public class BaseTest {

    protected WebDriver driver;

    /**
     * Setup method executed before each test
     * Initializes ExtentReports and WebDriver
     */
    @BeforeMethod
    public void setUp() {
        // Initialize ExtentReport on first test
        if (ExtentReportManager.getExtentReports() == null) {
            ExtentReportManager.initializeExtentReport();
        }

        // Initialize WebDriver
        driver = Driverkeyword.launchBrowser("chrome");
        ExtentReportManager.logInfo("Browser launched successfully");
    }

    /**
     * Teardown method executed after each test
     * Closes WebDriver and captures final state
     */
    @AfterMethod
    public void tearDown() {
        try {
            if (driver != null) {
                Driverkeyword.closeBrowser();
                ExtentReportManager.logInfo("Browser closed successfully");
            }
        } catch (Exception e) {
            ExtentReportManager.logException(e);
        }
    }

    /**
     * Get the current WebDriver instance
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }
}
