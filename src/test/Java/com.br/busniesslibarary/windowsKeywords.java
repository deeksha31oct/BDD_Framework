package com.br.busniesslibarary;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.WiniumDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class windowsKeywords {

    private final WiniumDriver driver;
    private final WebDriverWait wait;

    public windowsKeywords(WiniumDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // 1. enterText - clear then type into an edit field
    public void enterText(By locator, String text) {
        try {
            WebElement element = driver.findElement(locator);
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            System.out.println("enterText failed: " + e.getMessage());
        }
    }

    // 2. verifyWindowExistence - true if a window/control with that name exists
    public boolean verifyWindowExistence(String windowName) {
        try {
            return driver.findElements(By.name(windowName)).size() > 0;
        } catch (Exception e) {
            System.out.println("verifyWindowExistence failed: " + e.getMessage());
            return false;
        }
    }

    // 3. buttonClick - click a button
    public void buttonClick(By locator) {
        try {
            driver.findElement(locator).click();
        } catch (Exception e) {
            System.out.println("buttonClick failed: " + e.getMessage());
        }
    }

    // 4. waitUntilObjectExists - wait until the element is present
    public void waitUntilObjectExists(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            System.out.println("waitUntilObjectExists failed: " + e.getMessage());
        }
    }

    // 5. verifyUIObjectExistence - true if the element is displayed
    public boolean verifyUIObjectExistence(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            System.out.println("verifyUIObjectExistence failed: " + e.getMessage());
            return false;
        }
    }

    // 6. objectClick - generic click on any object
    public void objectClick(By locator) {
        try {
            driver.findElement(locator).click();
        } catch (Exception e) {
            System.out.println("objectClick failed: " + e.getMessage());
        }
    }

    // 7. setCalendarDate - type a date into a calendar/date field
    public void setCalendarDate(By locator, String date) {
        try {
            WebElement field = driver.findElement(locator);
            field.clear();
            field.sendKeys(date);   // e.g. "06/22/2026"
        } catch (Exception e) {
            System.out.println("setCalendarDate failed: " + e.getMessage());
        }
    }

    // 8. getTextFromEditField - read text/value from an edit field
    public String getTextFromEditField(By locator) {
        String value = "";
        try {
            WebElement field = driver.findElement(locator);
            value = field.getText();
            if (value == null || value.isEmpty()) {
                value = field.getAttribute("Value.Value");   // UIAutomation value pattern
            }
        } catch (Exception e) {
            System.out.println("getTextFromEditField failed: " + e.getMessage());
        }
        return value;
    }

    // 9. verifyLabelExistence - true if a label exists and matches expected text
    public boolean verifyLabelExistence(By locator, String expectedText) {
        try {
            String actual = driver.findElement(locator).getText();
            return actual.trim().equalsIgnoreCase(expectedText.trim());
        } catch (Exception e) {
            System.out.println("verifyLabelExistence failed: " + e.getMessage());
            return false;
        }
    }

    // 10. selectComboBoxOption - open a combo box and select an option by name
    public void selectComboBoxOption(By comboLocator, String optionName) {
        try {
            driver.findElement(comboLocator).click();                 // open the combo
            driver.findElement(By.name(optionName)).click();          // pick the option
        } catch (Exception e) {
            System.out.println("selectComboBoxOption failed: " + e.getMessage());
        }
    }

    // 11. getTableCellValue - read one cell by row & column index (1-based)
    public String getTableCellValue(By tableLocator, int rowIndex, int colIndex) {
        String cellValue = "";
        try {
            WebElement table = driver.findElement(tableLocator);
            // structure varies by app - typical UIA: rows then cells
            WebElement cell = table.findElement(
                    By.xpath(".//*[@LocalizedControlType='row'][" + rowIndex + "]"
                            + "//*[@LocalizedControlType='cell'][" + colIndex + "]"));
            cellValue = cell.getText();
        } catch (Exception e) {
            System.out.println("getTableCellValue failed: " + e.getMessage());
        }
        return cellValue;
    }

    // 12. getTableColumnValue - read all values in one column
    public List<String> getTableColumnValue(By tableLocator, int colIndex) {
        List<String> columnValues = new ArrayList<>();
        try {
            WebElement table = driver.findElement(tableLocator);
            List<WebElement> rows = table.findElements(By.xpath(".//*[@LocalizedControlType='row']"));
            for (WebElement row : rows) {
                WebElement cell = row.findElement(
                        By.xpath(".//*[@LocalizedControlType='cell'][" + colIndex + "]"));
                columnValues.add(cell.getText());
            }
        } catch (Exception e) {
            System.out.println("getTableColumnValue failed: " + e.getMessage());
        }
        return columnValues;
    }
}