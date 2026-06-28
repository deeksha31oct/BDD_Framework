package com.br.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class Dashboard {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public Dashboard(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ---------- Locators (UPDATE with real values from the dashboard DOM) ----------
    private final By overviewHeader   = By.xpath("//*[normalize-space()='OVERVIEW']");        // UPDATE
    private final By welcomeMessage   = By.xpath("//*[contains(text(),'WELCOME')]");          // UPDATE

    // KPI cards (top-right tiles)
    private final By kpiInventoryValue = By.xpath("//*[normalize-space()='INVENTORY Σ']/following::*[1]"); // UPDATE
    private final By kpiItems          = By.xpath("//*[normalize-space()='ITEMS']/following::*[1]");        // UPDATE
    private final By kpiPendingDispatch= By.xpath("//*[normalize-space()='PENDING DISPATCH']/following::*[1]"); // UPDATE
    private final By kpiFgBicycles     = By.xpath("//*[normalize-space()='FG BICYCLES']/following::*[1]");  // UPDATE

    // Stock-health cards
    private final By outOfStockCount   = By.xpath("//*[normalize-space()='OUT OF STOCK']/preceding::*[1]"); // UPDATE
    private final By criticalCount     = By.xpath("//*[normalize-space()='CRITICAL']/preceding::*[1]");     // UPDATE
    private final By lowStockCount     = By.xpath("//*[normalize-space()='LOW STOCK']/preceding::*[1]");    // UPDATE
    private final By healthyCount      = By.xpath("//*[normalize-space()='HEALTHY']/preceding::*[1]");      // UPDATE

    // Reorder-needed table
    private final By reorderTableRows  = By.cssSelector("table tbody tr");                    // UPDATE

    // Left-navigation links (generic: pass the menu text)
    private By navMenu(String name) {
        return By.xpath("//*[normalize-space()='" + name + "']");                             // UPDATE if not text-based
    }

    // ---------- Page-load verification ----------
    public boolean isDashboardLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(overviewHeader)).isDisplayed();
    }

    public String getWelcomeMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeMessage)).getText().trim();
    }

    // ---------- KPI card readers ----------
    public String getInventoryValue() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(kpiInventoryValue)).getText().trim();
    }

    public String getItemsCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(kpiItems)).getText().trim();
    }

    public String getPendingDispatch() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(kpiPendingDispatch)).getText().trim();
    }

    public String getFgBicycles() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(kpiFgBicycles)).getText().trim();
    }

    // ---------- Stock-health readers ----------
    public String getOutOfStockCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(outOfStockCount)).getText().trim();
    }

    public String getCriticalCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(criticalCount)).getText().trim();
    }

    public String getLowStockCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(lowStockCount)).getText().trim();
    }

    public String getHealthyCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(healthyCount)).getText().trim();
    }

    // ---------- Reorder table ----------
    public int getReorderRowCount() {
        return driver.findElements(reorderTableRows).size();
    }

    public boolean isReorderItemPresent(String itemCode) {
        List<WebElement> rows = driver.findElements(reorderTableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(itemCode)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Left navigation ----------
    public void clickNavMenu(String menuName) {
        wait.until(ExpectedConditions.elementToBeClickable(navMenu(menuName))).click();
    }

    public boolean isNavMenuVisible(String menuName) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(navMenu(menuName))).isDisplayed();
    }
}
