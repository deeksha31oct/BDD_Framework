package Com.base.Extradata;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtility provides screenshot capture functionality
 * Screenshots are saved with timestamp and can be used in ExtentReports
 */
public class ScreenshotUtility {

    private static final String SCREENSHOT_PATH = "test-output/Screenshots/";

    /**
     * Capture screenshot and save it with timestamp
     * @param driver - WebDriver instance
     * @param screenshotName - Name for the screenshot
     * @return Path to the saved screenshot
     */
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            // Create screenshot directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_PATH);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // Generate timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String fileName = screenshotName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_PATH + fileName;

            // Take screenshot
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File srcFile = screenshot.getScreenshotAs(OutputType.FILE);

            // Copy screenshot to destination
            File destFile = new File(filePath);
            Files.copy(srcFile.toPath(), destFile.toPath());

            System.out.println("Screenshot captured: " + filePath);
            return filePath;

        } catch (IOException e) {
            System.err.println("Error capturing screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Capture screenshot with system timestamp (milliseconds)
     * @param driver - WebDriver instance
     * @return Path to the saved screenshot
     */
    public static String captureScreenshot(WebDriver driver) {
        return captureScreenshot(driver, "Screenshot_" + System.currentTimeMillis());
    }

    /**
     * Get the screenshot directory path
     * @return Screenshot directory path
     */
    public static String getScreenshotPath() {
        return SCREENSHOT_PATH;
    }
}
