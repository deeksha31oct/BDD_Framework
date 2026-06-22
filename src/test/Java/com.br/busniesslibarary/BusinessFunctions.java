package com.br.busniesslibarary;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class BusinessFunctions {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Properties or = new Properties();   // object repository

    public BusinessFunctions(WebDriver driver, String orFilePath) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try (FileInputStream fis = new FileInputStream(orFilePath)) {
            or.load(fis);                              // load all locators once
        } catch (IOException e) {
            System.out.println("Could not load Object Repository: " + e.getMessage());
        }
    }

    // ---------- Object Repository helpers ----------

    // converts "type:value" from OR into a By object
    private By getLocator(String key) {
        String locator = or.getProperty(key);
        if (locator == null) throw new RuntimeException("Locator key not found in OR: " + key);
        String[] parts = locator.split(":", 2);        // split on FIRST colon only
        String type = parts[0].trim().toLowerCase();
        String value = parts[1].trim();
        switch (type) {
            case "id":        return By.id(value);
            case "name":      return By.name(value);
            case "css":       return By.cssSelector(value);
            case "xpath":     return By.xpath(value);
            case "classname": return By.className(value);
            case "linktext":  return By.linkText(value);
            case "tagname":   return By.tagName(value);
            default: throw new IllegalArgumentException("Unknown locator type: " + type);
        }
    }

    // returns the raw CSS string (shadow DOM is CSS-only)
    private String getCssValue(String key) {
        String locator = or.getProperty(key);
        if (locator == null) throw new RuntimeException("Locator key not found in OR: " + key);
        return locator.toLowerCase().startsWith("css:") ? locator.substring(4).trim() : locator;
    }

    // ---------- Business functions ----------

    // 1. selectShadowElement - reach an element inside a shadow root (CSS only)
    public WebElement selectShadowElement(String shadowHostKey, String childCssKey) {
        try {
            WebElement host = driver.findElement(getLocator(shadowHostKey));
            SearchContext shadowRoot = host.getShadowRoot();
            return shadowRoot.findElement(By.cssSelector(getCssValue(childCssKey)));
        } catch (Exception e) {
            System.out.println("selectShadowElement failed: " + e.getMessage());
            return null;
        }
    }

    // 2. verifyElementExistOrNot - true if element present in DOM
    public boolean verifyElementExistOrNot(String key) {
        try {
            return driver.findElements(getLocator(key)).size() > 0;
        } catch (Exception e) {
            System.out.println("verifyElementExistOrNot failed: " + e.getMessage());
            return false;
        }
    }

    // 3. enterText_withoutClear_shadowElement - type without clearing
    public void enterText_withoutClear_shadowElement(String hostKey, String childKey, String text) {
        try {
            WebElement element = selectShadowElement(hostKey, childKey);
            element.sendKeys(text);
        } catch (Exception e) {
            System.out.println("enterText_withoutClear_shadowElement failed: " + e.getMessage());
        }
    }

    // 4. clearText_shadowElement - clear a shadow input
    public void clearText_shadowElement(String hostKey, String childKey) {
        try {
            WebElement element = selectShadowElement(hostKey, childKey);
            element.sendKeys(Keys.CONTROL + "a");
            element.sendKeys(Keys.DELETE);          // shadow inputs often ignore .clear()
        } catch (Exception e) {
            System.out.println("clearText_shadowElement failed: " + e.getMessage());
        }
    }

    // 5. doubleClick_shadowElement
    public void doubleClick_shadowElement(String hostKey, String childKey) {
        try {
            WebElement element = selectShadowElement(hostKey, childKey);
            new Actions(driver).doubleClick(element).perform();
        } catch (Exception e) {
            System.out.println("doubleClick_shadowElement failed: " + e.getMessage());
        }
    }

    // 6. elementClick_shadowElement
    public void elementClick_shadowElement(String hostKey, String childKey) {
        try {
            WebElement element = selectShadowElement(hostKey, childKey);
            element.click();
        } catch (Exception e) {
            System.out.println("elementClick_shadowElement failed: " + e.getMessage());
        }
    }

    // 7. waitForElementClick - wait until clickable, then click
    public void waitForElementClick(String key) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(getLocator(key))).click();
        } catch (Exception e) {
            System.out.println("waitForElementClick failed: " + e.getMessage());
        }
    }

    // 8. scrollToBottom
    public void scrollToBottom() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception e) {
            System.out.println("scrollToBottom failed: " + e.getMessage());
        }
    }

    // 9. selectDropdown_element - normal <select> by visible text
    public void selectDropdown_element(String key, String visibleText) {
        try {
            Select dropdown = new Select(driver.findElement(getLocator(key)));
            dropdown.selectByVisibleText(visibleText);
        } catch (Exception e) {
            System.out.println("selectDropdown_element failed: " + e.getMessage());
        }
    }

    // 10. selectDropdown_shadowElementWithCssLocator - <select> inside a shadow root
    public void selectDropdown_shadowElementWithCssLocator(String hostKey, String childKey, String visibleText) {
        try {
            WebElement dropdown = selectShadowElement(hostKey, childKey);
            new Select(dropdown).selectByVisibleText(visibleText);
        } catch (Exception e) {
            System.out.println("selectDropdown_shadowElementWithCssLocator failed: " + e.getMessage());
        }
    }

    // 11. deleteCookies_settings
    public void deleteCookies_settings() {
        try {
            driver.manage().deleteAllCookies();
        } catch (Exception e) {
            System.out.println("deleteCookies_settings failed: " + e.getMessage());
        }
    }

    // 12. verifyObjectExistence_element_boolean - true if displayed
    public boolean verifyObjectExistence_element_boolean(String key) {
        try {
            return driver.findElement(getLocator(key)).isDisplayed();
        } catch (Exception e) {
            System.out.println("verifyObjectExistence_element_boolean failed: " + e.getMessage());
            return false;
        }
    }
}