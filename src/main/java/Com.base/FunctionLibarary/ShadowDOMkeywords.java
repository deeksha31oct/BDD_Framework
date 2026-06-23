package Com.base.FunctionLibarary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * ShadowDOMkeywords - utilities for elements inside a shadow root.
 * Shadow elements are reached via host element -> getShadowRoot() -> CSS selector
 * (XPath does NOT work inside a shadow root - CSS only).
 *
 * hostCss   = CSS of the element that hosts the shadow root
 * innerCss  = CSS of the target element INSIDE that shadow root
 */
public class ShadowDOMkeywords {

    private static final Logger log = LogManager.getLogger(ShadowDOMkeywords.class);

    private static final int DEFAULT_TIMEOUT = 20;

    public static WebDriver driver() {
        return driver();
    }

    private static JavascriptExecutor js() {
        return (JavascriptExecutor) driver();
    }

    // ===================================================================
    //  CORE: get the shadow element
    // ===================================================================

    // returns the shadow root of the host element as a SearchContext
    private static SearchContext getShadowRoot(String hostCss) {
        WebElement host = driver().findElement(By.cssSelector(hostCss));
        return host.getShadowRoot();
    }

    // find a single element inside a shadow root
    public static WebElement selectShadowElement(String hostCss, String innerCss) {
        return getShadowRoot(hostCss).findElement(By.cssSelector(innerCss));
    }

    // alias - select shadow element by chars/css inside the root
    public static WebElement selectElementByChars(String hostCss, String innerCss) {
        return selectShadowElement(hostCss, innerCss);
    }

    // get a LIST of elements inside a shadow root
    public static List<WebElement> getListOfElements(String hostCss, String innerCss) {
        return getShadowRoot(hostCss).findElements(By.cssSelector(innerCss));
    }

    // ===================================================================
    //  ACTIONS
    // ===================================================================

    public static void shadowElementClick(String hostCss, String innerCss) {
        selectShadowElement(hostCss, innerCss).click();
        log.info("Shadow click: " + hostCss + " >>> " + innerCss);
    }

    public static void shadowElementSendKeys(String hostCss, String innerCss, String text) {
        WebElement el = selectShadowElement(hostCss, innerCss);
        el.clear();
        el.sendKeys(text);
        log.info("Shadow sendKeys '" + text + "': " + innerCss);
    }

    public static void shadowElementDoubleClick(String hostCss, String innerCss) {
        WebElement el = selectShadowElement(hostCss, innerCss);
        new Actions(driver()).doubleClick(el).perform();
    }

    public static String shadowElementGetText(String hostCss, String innerCss) {
        return selectShadowElement(hostCss, innerCss).getText();
    }

    // select / tick a checkbox inside shadow root (only clicks if not already selected)
    public static void selectCheckbox(String hostCss, String innerCss) {
        WebElement checkbox = selectShadowElement(hostCss, innerCss);
        if (!checkbox.isSelected()) {
            checkbox.click();
            log.info("Checkbox selected: " + innerCss);
        }
    }

    // ===================================================================
    //  DROPDOWNS (shadow <select> can't use Selenium Select - use JS)
    // ===================================================================

    // select a dropdown option by visible text using JavaScript
    public static void selectDropdownSelectTextByJavaScript(String hostCss, String innerCss, String visibleText) {
        WebElement dropdown = selectShadowElement(hostCss, innerCss);
        js().executeScript(
                "const sel = arguments[0]; const txt = arguments[1];" +
                        "for (let i = 0; i < sel.options.length; i++) {" +
                        "  if (sel.options[i].text === txt) { sel.selectedIndex = i;" +
                        "    sel.dispatchEvent(new Event('change', {bubbles:true})); break; } }",
                dropdown, visibleText);
        log.info("Shadow dropdown selected '" + visibleText + "'");
    }

    // alias for the same behaviour
    public static void selectShadowDropdown(String hostCss, String innerCss, String visibleText) {
        selectDropdownSelectTextByJavaScript(hostCss, innerCss, visibleText);
    }

    // ===================================================================
    //  JAVASCRIPT HELPERS
    // ===================================================================

    // run arbitrary JS against a shadow element (e.g. click via JS)
    public static Object shadowElementJavaScript(String hostCss, String innerCss, String script) {
        WebElement el = selectShadowElement(hostCss, innerCss);
        return js().executeScript(script, el);
    }

    public static void scrollToViewElement(String hostCss, String innerCss) {
        WebElement el = selectShadowElement(hostCss, innerCss);
        js().executeScript("arguments[0].scrollIntoView(true);", el);
    }

    // ===================================================================
    //  WAITS
    // ===================================================================

    // explicit wait until a shadow element is present/visible.
    // (standard ExpectedConditions can't pierce shadow DOM, so we poll manually.)
    public static WebElement shadowExplicitWait(String hostCss, String innerCss) {
        WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(d -> {
            try {
                WebElement el = selectShadowElement(hostCss, innerCss);
                return (el != null && el.isDisplayed()) ? el : null;
            } catch (Exception e) {
                return null;   // keep polling
            }
        });
    }

    // simple wait (hard sleep) in milliseconds
    public static void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("wait interrupted: " + e.getMessage());
        }
    }
}
