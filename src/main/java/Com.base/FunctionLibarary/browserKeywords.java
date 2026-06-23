package Com.base.FunctionLibarary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * browserKeywords - central Selenium keyword library.
 * Locator format used throughout: "type:value"  e.g. "id:username", "xpath://h6", "css:.btn"
 */
public class browserKeywords {

    private static final Logger log = LogManager.getLogger(browserKeywords.class);

    private static final int DEFAULT_TIMEOUT = 20;

    // ===================================================================
    //  DRIVER / WAIT HELPERS
    // ===================================================================

    public static WebDriver driver() {
        return driver();
    }

    private static WebDriverWait getWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    private static WebDriverWait getWait(int seconds) {
        return new WebDriverWait(driver(), Duration.ofSeconds(seconds));
    }

    // split "id:username" -> By
    public static By selectLocator(String locator) {
        String[] parts = locator.split(":", 2);
        String type = parts[0].trim().toLowerCase();
        String value = parts[1].trim();
        switch (type) {
            case "id":          return By.id(value);
            case "name":        return By.name(value);
            case "css":
            case "cssselector": return By.cssSelector(value);
            case "xpath":       return By.xpath(value);
            case "class":
            case "classname":   return By.className(value);
            case "tag":
            case "tagname":     return By.tagName(value);
            case "link":
            case "linktext":    return By.linkText(value);
            case "partiallink":
            case "partiallinktext": return By.partialLinkText(value);
            default:
                log.warn("Unknown locator type '" + type + "', defaulting to xpath");
                return By.xpath(value);
        }
    }

    // ===================================================================
    //  BROWSER LIFECYCLE / NAVIGATION
    // ===================================================================

    public static void launchBrowser(String browser) {
        logInfo("Launched browser: " + browser);
    }

    public static void maximizeBrowser() {
        driver().manage().window().maximize();
    }

    public static void minimizeBrowser() {
        driver().manage().window().minimize();
    }

    public static void closeBrowser() {
        driver().quit();
        logInfo("Browser closed");
    }

    public static void navigateBack() {
        driver().navigate().back();
    }

    // navigate back then forward
    public static void navigateBackwardAndForward() {
        driver().navigate().back();
        driver().navigate().forward();
    }

    // alias for navigateBack (you listed both)
    public static void backToNavigation() {
        driver().navigate().back();
    }

    public static void refreshPage() {
        driver().navigate().refresh();
    }

    // ===================================================================
    //  WINDOW HANDLING
    // ===================================================================

    public static String getWindowHandle() {
        return driver().getWindowHandle();
    }

    public static Set<String> getWindowHandles() {
        return driver().getWindowHandles();
    }

    public static void switchToWindow(String handle) {
        driver().switchTo().window(handle);
    }

    // close child window(s) and switch back to the original parent
    public static void closeChildAndSwitchToParentWindow(String parentHandle) {
        for (String handle : driver().getWindowHandles()) {
            if (!handle.equals(parentHandle)) {
                driver().switchTo().window(handle);
                driver().close();
            }
        }
        driver().switchTo().window(parentHandle);
        logInfo("Closed child windows, back to parent");
    }

    // switch to a window by closing all the others
    public static void switchToWindowByClosingOtherWindow(String targetHandle) {
        for (String handle : driver().getWindowHandles()) {
            if (!handle.equals(targetHandle)) {
                driver().switchTo().window(handle);
                driver().close();
            }
        }
        driver().switchTo().window(targetHandle);
    }

    // ===================================================================
    //  FRAMES
    // ===================================================================

    public static void switchToFrame(String locator) {
        driver().switchTo().frame(driver().findElement(selectLocator(locator)));
    }

    public static void switchToFrameId(String idOrName) {
        driver().switchTo().frame(idOrName);
    }

    public static void switchToParentFrame() {
        driver().switchTo().parentFrame();
    }

    // ===================================================================
    //  COOKIES / ALERTS
    // ===================================================================

    public static void deleteAllCookies() {
        driver().manage().deleteAllCookies();
        logInfo("All cookies deleted");
    }

    public static void dismissAlert() {
        try {
            driver().switchTo().alert().dismiss();
        } catch (NoAlertPresentException e) {
            logError("No alert to dismiss");
        }
    }

    public static void waitForAlert() {
        getWait().until(ExpectedConditions.alertIsPresent());
    }

    // ===================================================================
    //  TEXT / INPUT ACTIONS
    // ===================================================================

    public static void enterText(String locator, String text) {
        WebElement el = waitForElementVisible(locator);
        el.clear();
        el.sendKeys(text);
        logInfo("Entered text '" + text + "' into " + locator);
    }

    public static void enterTextWithoutClear(String locator, String text) {
        WebElement el = waitForElementVisible(locator);
        el.sendKeys(text);
    }

    // enter text then send a key (e.g. ENTER, TAB)
    public static void enterTextWithKey(String locator, String text, Keys key) {
        WebElement el = waitForElementVisible(locator);
        el.clear();
        el.sendKeys(text);
        el.sendKeys(key);
    }

    public static void clearText(String locator) {
        waitForElementVisible(locator).clear();
    }

    // ===================================================================
    //  CLICK ACTIONS
    // ===================================================================

    public static void elementClick(String locator) {
        waitForElementClickable(locator).click();
        logInfo("Clicked: " + locator);
    }

    // click using a By directly
    public static void elementClickByLocator(By by) {
        getWait().until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    public static void doubleClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        new Actions(driver()).doubleClick(el).perform();
    }

    public static void rightClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        new Actions(driver()).contextClick(el).perform();
    }

    public static void dragAndDrop(String sourceLocator, String targetLocator) {
        WebElement src = waitForElementVisible(sourceLocator);
        WebElement tgt = waitForElementVisible(targetLocator);
        new Actions(driver()).dragAndDrop(src, tgt).perform();
    }

    // ===================================================================
    //  MOUSE HOVER
    // ===================================================================

    public static void mouseHover(String locator) {
        WebElement el = waitForElementVisible(locator);
        new Actions(driver()).moveToElement(el).perform();
    }

    public static void mouseHoverClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        new Actions(driver()).moveToElement(el).click().perform();
    }

    public static void mouseHoverDoubleClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        new Actions(driver()).moveToElement(el).doubleClick().perform();
    }

    // ===================================================================
    //  JAVASCRIPT ACTIONS
    // ===================================================================

    private static JavascriptExecutor js() {
        return (JavascriptExecutor) driver();
    }

    public static void javascriptClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        js().executeScript("arguments[0].click();", el);
    }

    // js click without waiting for visibility (element present only)
    public static void javascriptClickWithoutVisible(String locator) {
        WebElement el = driver().findElement(selectLocator(locator));
        js().executeScript("arguments[0].click();", el);
    }

    public static void javascriptDoubleClick(String locator) {
        WebElement el = waitForElementVisible(locator);
        js().executeScript(
                "var evt = new MouseEvent('dblclick', {bubbles:true, cancelable:true, view:window});" +
                        "arguments[0].dispatchEvent(evt);", el);
    }

    // get an attribute via javascript
    public static String javascriptAttribute(String locator, String attribute) {
        WebElement el = driver().findElement(selectLocator(locator));
        return (String) js().executeScript(
                "return arguments[0].getAttribute(arguments[1]);", el, attribute);
    }

    public static String getJavascriptText(String locator) {
        WebElement el = driver().findElement(selectLocator(locator));
        return (String) js().executeScript("return arguments[0].textContent;", el);
    }

    // ===================================================================
    //  SCROLL
    // ===================================================================

    public static void scrollIntoElementView(String locator) {
        WebElement el = driver().findElement(selectLocator(locator));
        js().executeScript("arguments[0].scrollIntoView(true);", el);
    }

    // alias (you listed both spellings)
    public static void scrollToElementView(String locator) {
        scrollIntoElementView(locator);
    }

    // scroll a horizontally-scrollable element right (positive) or left (negative)
    public static void scrollHorizontalRightLeft(String locator, int pixels) {
        WebElement el = driver().findElement(selectLocator(locator));
        js().executeScript("arguments[0].scrollLeft += arguments[1];", el, pixels);
    }

    // ===================================================================
    //  HIGHLIGHT
    // ===================================================================

    public static void selectedElementHighlighted(String locator) {
        WebElement el = driver().findElement(selectLocator(locator));
        js().executeScript("arguments[0].style.border='3px solid red'", el);
    }

    // ===================================================================
    //  DROPDOWN
    // ===================================================================

    public static void selectDropdownValue(String locator, String visibleText) {
        Select select = new Select(waitForElementVisible(locator));
        select.selectByVisibleText(visibleText);
    }

    public static String getDropdownValue(String locator) {
        Select select = new Select(waitForElementVisible(locator));
        return select.getFirstSelectedOption().getText();
    }

    public static List<String> getDropdownOptionNames(String locator) {
        Select select = new Select(waitForElementVisible(locator));
        List<String> names = new ArrayList<>();
        for (WebElement option : select.getOptions()) {
            names.add(option.getText());
        }
        return names;
    }

    // ===================================================================
    //  GET TEXT / TITLE / URL / SCREENSHOT
    // ===================================================================

    public static String getText(String locator) {
        return waitForElementVisible(locator).getText();
    }

    public static String getTitle() {
        return driver().getTitle();
    }

    // get a property/attribute value of an element
    public static String getObjectPropertyValue(String locator, String attribute) {
        return driver().findElement(selectLocator(locator)).getAttribute(attribute);
    }

    public static String getBrowserScreenshot() {
        return ((TakesScreenshot) driver()).getScreenshotAs(OutputType.BASE64);
    }

    public static String getWindowScreenshot() {
        return getBrowserScreenshot();
    }

    // count of rows/columns of a table located by locator (returns int[]{rows, cols})
    public static int[] getRowAndColumnCount(String tableLocator) {
        WebElement table = driver().findElement(selectLocator(tableLocator));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        int rowCount = rows.size();
        int colCount = rowCount > 0 ? rows.get(0).findElements(By.tagName("td")).size() : 0;
        return new int[]{rowCount, colCount};
    }

    // ===================================================================
    //  GET LIST OF VALUES (multiple elements -> their text)
    // ===================================================================

    // standard: waits for visibility of all, returns text list
    public static List<String> getAllListOfValue(String locator) {
        getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(selectLocator(locator)));
        return collectText(driver().findElements(selectLocator(locator)));
    }

    // no exception: returns empty list instead of throwing
    public static List<String> getAllListOfValueWithoutException(String locator) {
        try {
            return collectText(driver().findElements(selectLocator(locator)));
        } catch (Exception e) {
            logError("getAllListOfValueWithoutException: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // without visibility wait: just findElements as-is
    public static List<String> getAllListOfValueWithoutVisibility(String locator) {
        return collectText(driver().findElements(selectLocator(locator)));
    }

    // with object existence check first
    public static List<String> getAllListOfValueWithObjectExistence(String locator) {
        if (verifyObjectExistence(locator)) {
            return collectText(driver().findElements(selectLocator(locator)));
        }
        return new ArrayList<>();
    }

    private static List<String> collectText(List<WebElement> elements) {
        List<String> values = new ArrayList<>();
        for (WebElement el : elements) values.add(el.getText());
        return values;
    }

    // ===================================================================
    //  WAITS
    // ===================================================================

    public static WebElement waitForElementVisible(String locator) {
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(selectLocator(locator)));
    }

    public static WebElement waitForElementClickable(String locator) {
        return getWait().until(ExpectedConditions.elementToBeClickable(selectLocator(locator)));
    }

    public static WebElement waitForElementPresent(String locator) {
        return getWait().until(ExpectedConditions.presenceOfElementLocated(selectLocator(locator)));
    }

    // wait for the Nth duplicate element (index) to be visible
    public static WebElement waitForDuplicateElementVisible(String locator, int index) {
        List<WebElement> els = getWait().until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(selectLocator(locator)));
        return els.get(index);
    }

    public static void waitPageLoad() {
        getWait().until(d ->
                js().executeScript("return document.readyState").equals("complete"));
    }

    // alias (you listed both)
    public static void waitForPageCompletelyLoaded() {
        waitPageLoad();
    }

    // ===================================================================
    //  VERIFY / VALIDATE (return boolean)
    // ===================================================================

    public static boolean verifyObjectExistence(String locator) {
        try {
            return !driver().findElements(selectLocator(locator)).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // verify there are NO duplicate login objects (e.g. only one login button)
    public static boolean verifyDuplicateLoginObjectExistence(String locator) {
        int count = driver().findElements(selectLocator(locator)).size();
        boolean single = count == 1;
        logInfo("Login object count = " + count + " (single=" + single + ")");
        return single;
    }

    public static boolean verifyElementsEnabled(String locator) {
        return waitForElementVisible(locator).isEnabled();
    }

    public static boolean verifyElementIsSelected(String locator) {
        return driver().findElement(selectLocator(locator)).isSelected();
    }

    public static boolean verifyInvisibilityOfObject(String locator) {
        return getWait().until(
                ExpectedConditions.invisibilityOfElementLocated(selectLocator(locator)));
    }

    public static boolean verifyApplicationTitle(String expectedTitle) {
        boolean match = driver().getTitle().equals(expectedTitle);
        logInfo("verifyApplicationTitle expected='" + expectedTitle + "' actual='" + driver().getTitle() + "'");
        return match;
    }

    public static boolean verifyCurrentUrl(String expectedUrl) {
        return driver().getCurrentUrl().equals(expectedUrl);
    }

    public static boolean verifyObjectText(String locator, String expectedText) {
        return getText(locator).equals(expectedText);
    }

    // tooltip is usually the "title" attribute
    public static String verifyTooltipText(String locator) {
        return driver().findElement(selectLocator(locator)).getAttribute("title");
    }

    // validate an element's property/attribute equals an expected value
    public static boolean validateObjectPropertyValue(String locator, String attribute, String expected) {
        String actual = getObjectPropertyValue(locator, attribute);
        boolean match = expected.equals(actual);
        logInfo("validateObjectPropertyValue " + attribute + " expected='" + expected + "' actual='" + actual + "'");
        return match;
    }

    // ===================================================================
    //  LOGGING
    // ===================================================================

    public static void logInfo(String message) {
        log.info(message);
    }

    public static void logError(String message) {
        log.error(message);
    }
}