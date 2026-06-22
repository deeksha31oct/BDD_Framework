package utilites;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentReportManager handles all ExtentReport related operations
 * It provides methods to initialize, create tests, and log steps with screenshots
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static ExtentTest test;
    private static final String REPORT_PATH = "test-output/ExtentReport/";
    private static final String REPORT_FILE_NAME = "ExtentReport.html";

    /**
     * Initialize ExtentReports with Spark Reporter
     * Creates report directory if it doesn't exist
     */
    public static void initializeExtentReport() {
        // Create report directory if it doesn't exist
        File reportDir = new File(REPORT_PATH);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Initialize ExtentSparkReporter
        String reportFilePath = REPORT_PATH + REPORT_FILE_NAME;
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);

        // Configure Spark Reporter
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("BDD Framework - Test Report");
        sparkReporter.config().setReportName("Automation Test Execution Report");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        // Initialize ExtentReports
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Add system information
        extent.setSystemInfo("Application Name", "BDD Framework");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Report Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        System.out.println("ExtentReport initialized at: " + reportFilePath);
    }

    /**
     * Create a new test in the ExtentReport
     * @param testName - Name of the test
     * @param testDescription - Description of the test
     * @return ExtentTest object
     */
    public static ExtentTest createTest(String testName, String testDescription) {
        test = extent.createTest(testName, testDescription);
        return test;
    }

    /**
     * Get the current test object
     * @return ExtentTest object
     */
    public static ExtentTest getTest() {
        return test;
    }

    /**
     * Log information step in the report
     * @param message - Step message to log
     */
    public static void logInfo(String message) {
        if (test != null) {
            test.info(message);
            System.out.println("INFO: " + message);
        }
    }

    /**
     * Log pass step in the report
     * @param message - Step message to log
     */
    public static void logPass(String message) {
        if (test != null) {
            test.pass(message);
            System.out.println("PASS: " + message);
        }
    }

    /**
     * Log fail step in the report
     * @param message - Step message to log
     */
    public static void logFail(String message) {
        if (test != null) {
            test.fail(message);
            System.out.println("FAIL: " + message);
        }
    }

    /**
     * Log warning step in the report
     * @param message - Step message to log
     */
    public static void logWarning(String message) {
        if (test != null) {
            test.warning(message);
            System.out.println("WARNING: " + message);
        }
    }

    /**
     * Log skip step in the report
     * @param message - Step message to log
     */
    public static void logSkip(String message) {
        if (test != null) {
            test.skip(message);
            System.out.println("SKIP: " + message);
        }
    }

    /**
     * Attach screenshot to the report
     * @param driver - WebDriver instance
     * @param screenshotName - Name for the screenshot
     */
    public static void attachScreenshot(WebDriver driver, String screenshotName) {
        try {
            String screenshotPath = ScreenshotUtility.captureScreenshot(driver, screenshotName);
            if (test != null && screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath);
                logPass("Screenshot attached: " + screenshotName);
            }
        } catch (Exception e) {
            logFail("Failed to attach screenshot: " + e.getMessage());
        }
    }

    /**
     * Attach exception to the report
     * @param exception - Exception to log
     */
    public static void logException(Exception exception) {
        if (test != null) {
            test.fail(exception);
            System.out.println("EXCEPTION: " + exception.getMessage());
        }
    }

    /**
     * Flush and finalize the ExtentReport
     */
    public static void flushExtentReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("ExtentReport flushed successfully");
        }
    }

    /**
     * Get the ExtentReports instance
     * @return ExtentReports object
     */
    public static ExtentReports getExtentReports() {
        return extent;
    }

    /**
     * Get report file path
     * @return Report file path
     */
    public static String getReportPath() {
        return REPORT_PATH + REPORT_FILE_NAME;
    }
}
