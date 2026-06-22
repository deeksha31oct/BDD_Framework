package Com.base.utilites;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.WebDriver;

/**
 * Factory class to initialize WebDriver based on browser configuration
 */
public class BrowserFactory {

    /**
     * Initialize WebDriver based on browser type from config.properties
     * Supported browsers: chrome, firefox, edge
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        String browserName = ConfigReader.getProperty("browser", "chrome").toLowerCase();
        boolean useWebDriverManager = ConfigReader.getBoolean("use.webdrivermanager");
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver;

        switch (browserName) {
            case "firefox":
                driver = initializeFirefox(useWebDriverManager, headless);
                break;
            case "edge":
                driver = initializeEdge(useWebDriverManager, headless);
                break;
            case "chrome":
            default:
                driver = initializeChrome(useWebDriverManager, headless);
                break;
        }

        // Set implicit and explicit wait timeouts
        int implicitWait = ConfigReader.getInt("implicit.wait", 10);
        int pageLoadTimeout = ConfigReader.getInt("page.load.timeout", 30);

        driver.manage().timeouts()
                .implicitlyWait(java.time.Duration.ofSeconds(implicitWait))
                .pageLoadTimeout(java.time.Duration.ofSeconds(pageLoadTimeout));

        return driver;
    }

    /**
     * Initialize Chrome WebDriver
     */
    private static WebDriver initializeChrome(boolean useWebDriverManager, boolean headless) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        // Common arguments
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080",
                "--disable-blink-features=AutomationControlled"
        );

        if (useWebDriverManager) {
            WebDriverManager.chromedriver().setup();
        }

        return new ChromeDriver(options);
    }

    /**
     * Initialize Firefox WebDriver
     */
    private static WebDriver initializeFirefox(boolean useWebDriverManager, boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        // Common arguments
        options.addArguments(
                "--no-sandbox",
                "--disable-gpu",
                "--width=1920",
                "--height=1080"
        );

        if (useWebDriverManager) {
            WebDriverManager.firefoxdriver().setup();
        }

        return new FirefoxDriver(options);
    }

    /**
     * Initialize Edge WebDriver
     */
    private static WebDriver initializeEdge(boolean useWebDriverManager, boolean headless) {
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        // Common arguments
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080"
        );

        if (useWebDriverManager) {
            WebDriverManager.edgedriver().setup();
        }

        return new EdgeDriver(options);
    }

    /**
     * Quit driver safely
     */
    public static void quitDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error closing WebDriver: " + e.getMessage());
            }
        }
    }
}


