package Com.base.FunctionLibarary;

import io.appium.java_client.windows.WindowsDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class appiumFunction {

    private static final Logger log = LogManager.getLogger(appiumFunction.class);

    // the active WinAppDriver session
    private static WindowsDriver driver;

    // ===================================================================
    //  DRIVER ACCESS
    // ===================================================================

    public static void setWinAppDriver(WindowsDriver winDriver) {
        driver = winDriver;
    }

    public static WindowsDriver getWinAppDriver() {
        return driver;
    }

    // ===================================================================
    //  WINDOW / SESSION HANDLING
    // ===================================================================

    // switch to a window by its handle
    public static void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
        log.info("Switched to window: " + windowHandle);
    }

    // switch to the first window whose title CONTAINS the given text
    public static void switchToWindowContaining(String titlePart) {
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle() != null && driver.getTitle().contains(titlePart)) {
                log.info("Switched to window containing: " + titlePart);
                return;
            }
        }
        log.warn("No window found containing: " + titlePart);
    }

    // ensure the active session's window title CONTAINS the given text
    public static boolean ensureActiveWindowSessionContaining(String titlePart) {
        String title = driver.getTitle();
        boolean match = title != null && title.contains(titlePart);
        log.info("ensureActiveWindowSessionContaining '" + titlePart + "' -> " + match);
        return match;
    }

    // ensure there IS an active window session (driver alive + has a window)
    public static boolean ensureActiveWindowSession() {
        try {
            boolean alive = driver != null && driver.getWindowHandle() != null;
            log.info("ensureActiveWindowSession -> " + alive);
            return alive;
        } catch (Exception e) {
            log.error("No active window session: " + e.getMessage());
            return false;
        }
    }

    // current window's handle (the "option" / identifier)
    public static String getCurrentWindowOption() {
        return driver.getWindowHandle();
    }

    // close the current window
    public static void closeWindow() {
        driver.close();
        log.info("Window closed");
    }

    // ===================================================================
    //  LOCATOR HELPERS  (object repository style: "type:value")
    // ===================================================================

    // split "accessibilityId:saveBtn" -> type = "accessibilityId"
    public static String getObjectiveType(String locator) {
        return locator.split(":", 2)[0].trim();
    }

    // split "accessibilityId:saveBtn" -> value = "saveBtn"
    public static String getObjectiveValue(String locator) {
        return locator.split(":", 2)[1].trim();
    }

    // build a By based on the locator type (WinAppDriver supports these)
    public static By getElementBasedonType(String locator) {
        String type = getObjectiveType(locator).toLowerCase();
        String value = getObjectiveValue(locator);
        switch (type) {
            case "accessibilityid":
            case "id":
                return By.id(value);                       // AutomationId
            case "name":
                return By.name(value);
            case "classname":
            case "class":
                return By.className(value);
            case "xpath":
                return By.xpath(value);
            case "tagname":
                return By.tagName(value);
            default:
                log.warn("Unknown locator type '" + type + "', defaulting to id");
                return By.id(value);
        }
    }

    // find and return the window element from a "type:value" locator
    public static WebElement getWindowElement(String locator) {
        return driver.findElement(getElementBasedonType(locator));
    }

    // ===================================================================
    //  WAITS
    // ===================================================================

    // wait until an element is visible (default 20s)
    public static WebElement waitForElementVisible(String locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(getElementBasedonType(locator)));
    }

    // ===================================================================
    //  ACTIONS
    // ===================================================================

    // type text into a field
    public static void enterText(String locator, String text) {
        WebElement el = waitForElementVisible(locator);
        el.sendKeys(text);
        log.info("Entered text '" + text + "' into " + locator);
    }

    // clear a field
    public static void clearField(String locator) {
        WebElement el = waitForElementVisible(locator);
        el.clear();
        log.info("Cleared field: " + locator);
    }

    // type text then press a button (e.g. submit) - locatorBtn is the button to click
    public static void enterTextToClickEnterButton(String fieldLocator, String text, String buttonLocator) {
        enterText(fieldLocator, text);
        elementClick(buttonLocator);
        log.info("Entered text and clicked button: " + buttonLocator);
    }

    // click an element
    public static void elementClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        el.click();
        log.info("Clicked: " + locator);
    }

    // double-click an element
    public static void doubleClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        new io.appium.java_client.windows.WindowsDriver(driver.getRemoteAddress(),
                driver.getCapabilities());   // not used - kept for clarity
        org.openqa.selenium.interactions.Actions actions =
                new org.openqa.selenium.interactions.Actions(driver);
        actions.doubleClick(el).perform();
        log.info("Double-clicked: " + locator);
    }

    // upload a file: type the full path into a file-path field, then click open/confirm
    public static void uploadFile(String pathFieldLocator, String filePath, String openButtonLocator) {
        enterText(pathFieldLocator, filePath);
        elementClick(openButtonLocator);
        log.info("Uploaded file: " + filePath);
    }
    // ===================================================================
    //  SCREENSHOT
    // ===================================================================

    // WinAppDriver screenshot as Base64
    public static String getWinAppDriverScreenshot() {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("getWinAppDriverScreenshot failed: " + e.getMessage());
            return null;
        }

    }

}
//appiumFunction.setWinAppDriver(myWindowsDriver);     // after you create the session
//appiumFunction.ensureActiveWindowSession();
//appiumFunction.switchToWindowContaining("Calculator");
//appiumFunction.enterText("accessibilityId:num5Button", "5");
//appiumFunction.elementClick("name:Plus");
//appiumFunction.enterTextToClickEnterButton("id:searchBox", "invoice", "name:Search");
//appiumFunction.uploadFile("id:FilePath", "C:/data/test.pdf", "name:Open");
//String shot = appiumFunction.getWinAppDriverScreenshot();
//