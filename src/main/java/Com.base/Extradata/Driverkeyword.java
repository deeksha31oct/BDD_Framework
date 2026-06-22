package Com.base.Extradata;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

/**
 * Driver factory for initializing and managing WebDriver instances
 * Supports Chrome, Firefox, and Edge browsers with configurable options
 */
public class Driverkeyword {

    public static WebDriver driver;

    /**
     * Initialize WebDriver based on the specified browser type
     * @param browserName - chrome, firefox, or edge
     * @return WebDriver instance
     */
    public static WebDriver launchBrowser(String browserName) {
        browserName = browserName.toLowerCase().trim();

        switch (browserName) {
            case "chrome":
                driver = initializeChrome();
                break;
            case "firefox":
                driver = initializeFirefox();
                break;
            case "edge":
                driver = initializeEdge();
                break;
            default:
                System.out.println("Browser: " + browserName + " is not supported. Launching Chrome...");
                driver = initializeChrome();
        }

        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        return driver;
    }

    /**
     * Initialize Chrome WebDriver with options
     */
    private static WebDriver initializeChrome() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        return new ChromeDriver(options);
    }

    /**
     * Initialize Firefox WebDriver with options
     */
    private static WebDriver initializeFirefox() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        return new FirefoxDriver(options);
    }

    /**
     * Initialize Edge WebDriver with options
     */
    private static WebDriver initializeEdge() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return new EdgeDriver(options);
    }

    /**
     * Get the current driver instance
     */
    public static WebDriver getDriver() {
        return driver;
    }

    /**
     * Close the browser instance
     */
    public static void closeBrowser() {
        if (driver != null) {
            try {
                driver.quit();
                driver = null;
                System.out.println("Browser closed successfully");
            } catch (Exception e) {
                System.err.println("Error closing browser: " + e.getMessage());
            }
        }
    }

    /**
     * Get current browser name
     */
    public static String getBrowserName() {
        if (driver != null) {
            return driver.getClass().getSimpleName();
        }
        return "Unknown";
    }

    /**
     * Navigate to URL
     */
    public static void navigateToUrl(String url) {
        if (driver != null) {
            driver.navigate().to(url);
            System.out.println("Navigated to: " + url);
        }
    }
}
