package Com.base.testng;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
            // must expose: public static ExtentTest getTest()
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static Com.base.Extradata.Driverkeyword.driver;
import static Com.base.testng.ExtentReportManager.test;

public class Assertlogs {

    private static final Logger log = LogManager.getLogger(Assertlogs.class);
    // browser screenshot as Base64
    private static String browserBase64() {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("browser screenshot failed: " + e.getMessage());
            return null;
        }
    }
    public static ExtentTest getTest() {
        return test;
    }

    // full screen / desktop (LeanFT / WinAppDriver) screenshot as Base64 via Robot
    private static String fullScreenBase64() {
        try {
            Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = new Robot().createScreenCapture(screen);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("full screen screenshot failed: " + e.getMessage());
            return null;
        }
    }

    // attach a browser screenshot to the report with a title
    public static void takeBrowserScrshotWithTitle(String title) {
        String b64 = browserBase64();
        if (b64 != null && ListnerTest.getTest() != null)
            ListnerTest.getTest().addScreenCaptureFromBase64String(b64, title);
    }

    // attach a full-screen screenshot to the report
    public static void assertFullScrshot(String title) {
        String b64 = fullScreenBase64();
        if (b64 != null && ListnerTest.getTest() != null)
            ListnerTest.getTest().addScreenCaptureFromBase64String(b64, title);
    }

    public static void writeHtmlLogInfo(String message) {
        if (ListnerTest.getTest() != null) ListnerTest.getTest().log(Status.INFO, message);
    }

    public static void writeHtmlLogError(String message) {
        if (ListnerTest.getTest() != null) ListnerTest.getTest().log(Status.FAIL, message);
    }

    public static void addHtmlHyperlink(String text, String url) {
        if (ListnerTest.getTest() != null)
            ListnerTest.getTest().log(Status.INFO, "<a href='" + url + "' target='_blank'>" + text + "</a>");
    }

    public static void addHtmlBoldText(String text) {
        if (ListnerTest.getTest() != null) ListnerTest.getTest().log(Status.INFO, "<b>" + text + "</b>");
    }

    public static void addXmlText(String xml) {
        if (ListnerTest.getTest() != null) ListnerTest.getTest().log(Status.INFO, MarkupHelper.createCodeBlock(xml));
    }

    public static void throwExceptionAddJsonText(String json) {
        if (ListnerTest.getTest() != null) ListnerTest.getTest().log(Status.FAIL, MarkupHelper.createCodeBlock(json));
        log.error("Exception with JSON: " + json);
        throw new RuntimeException("Assertion failed - see JSON: " + json);
    }

    // ===================================================================
    //  ASSERT TRUE - variants
    // ===================================================================

    // log + browser screenshot, then hard assert
    public static void assertTrue_logBrowserScr(boolean condition, String message) {
        if (condition) {
            writeHtmlLogInfo("PASS: " + message);
        } else {
            writeHtmlLogError("FAIL: " + message);
            takeBrowserScrshotWithTitle("Failure - " + message);
        }
        Assert.assertTrue(condition, message);
    }

    // same as above (explicit name)
    public static void assertTrue_log_browserScrshot(boolean condition, String message) {
        assertTrue_logBrowserScr(condition, message);
    }

    // log only, no screenshot, hard assert
    public static void assertTrue_log_withoutScrshot(boolean condition, String message) {
        if (condition) writeHtmlLogInfo("PASS: " + message);
        else writeHtmlLogError("FAIL: " + message);
        Assert.assertTrue(condition, message);
    }

    // alias
    public static void assertTrueLog_withoutScreenshot(boolean condition, String message) {
        assertTrue_log_withoutScrshot(condition, message);
    }

    // optional log (only logs on failure) + browser screenshot
    public static void assertTrue_optionalLog_browserScr(boolean condition, String message) {
        if (!condition) {
            writeHtmlLogError("FAIL: " + message);
            takeBrowserScrshotWithTitle("Failure - " + message);
        }
        Assert.assertTrue(condition, message);
    }

    // alias
    public static void assertTrue_optionalBrowserScrshot(boolean condition, String message) {
        assertTrue_optionalLog_browserScr(condition, message);
    }

    // optional log (only on failure), no screenshot
    public static void assertTrue_optional_withoutScrshot(boolean condition, String message) {
        if (!condition) writeHtmlLogError("FAIL: " + message);
        Assert.assertTrue(condition, message);
    }

    // optional screenshot, WinAppDriver (full screen)
    public static void assertTrueWithOptionalScrshot_winAppDriver(boolean condition, String message) {
        if (!condition) {
            writeHtmlLogError("FAIL: " + message);
            assertFullScrshot("WinApp Failure - " + message);
        }
        Assert.assertTrue(condition, message);
    }

    // LeanFT desktop window assert + full-screen screenshot
    public static void assertTrue_lft_window(boolean condition, String message) {
        if (condition) {
            writeHtmlLogInfo("PASS: " + message);
        } else {
            writeHtmlLogError("FAIL: " + message);
            assertFullScrshot("LeanFT Failure - " + message);
        }
        Assert.assertTrue(condition, message);
    }

    // ===================================================================
    //  ASSERT NULL
    // ===================================================================

    public static void assertNullWithLog(Object object, String message) {
        if (object == null) writeHtmlLogInfo("PASS (null): " + message);
        else writeHtmlLogError("FAIL (not null): " + message);
        Assert.assertNull(object, message);
    }

    // ===================================================================
    //  ASSERT FAIL - variants (force a failure)
    // ===================================================================

    // log + browser screenshot + throw
    public static void assertFail_logAndBrowserScreenshot(String message) {
        writeHtmlLogError("FAIL: " + message);
        takeBrowserScrshotWithTitle("Failure - " + message);
        Assert.fail(message);
    }

    // override version (same behavior, kept for explicit naming)
    public static void override_assertFail_log_withScrshot(String message) {
        assertFail_logAndBrowserScreenshot(message);
    }

    // log + browser screenshot but DO NOT throw (soft fail)
    public static void assertFailWithoutException_withBrowserScreenshot(String message) {
        writeHtmlLogError("FAIL (no exception): " + message);
        takeBrowserScrshotWithTitle("Failure - " + message);
        // intentionally no Assert.fail()
    }

    // log only, no screenshot, throw
    public static void assertFail_withLog_withoutScr(String message) {
        writeHtmlLogError("FAIL: " + message);
        Assert.fail(message);
    }

    // LeanFT window fail + full-screen screenshot + throw
    public static void assertFail_screenshot_lft_window(String message) {
        writeHtmlLogError("FAIL: " + message);
        assertFullScrshot("LeanFT Failure - " + message);
        Assert.fail(message);
    }

    // LeanFT screenshot WITHOUT html log, then throw
    public static void assert_screenshot_withoutHtmlLog_lft_te(String message) {
        assertFullScrshot("LeanFT - " + message);
        Assert.fail(message);
    }
}