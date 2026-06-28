package com.br.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class data_validation {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public data_validation(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ============================================================
    // 1. READ DATA FROM THE UI
    // ============================================================

    /** Read the visible text of a single UI element. */
    public String readUiData(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
                .getText().trim();
    }

    /** Read a whole UI table column into a list (e.g. the Reorder 'Code' column). */
    public List<String> readUiColumn(By rowsLocator, int columnIndex) {
        List<String> values = new ArrayList<>();
        List<WebElement> rows = driver.findElements(rowsLocator);
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() > columnIndex) {
                values.add(cells.get(columnIndex).getText().trim());
            }
        }
        return values;
    }

    // ============================================================
    // 2. READ DATA FROM THE DATABASE
    // ============================================================

    /** Returns a single value (first column, first row) for the given query. */
    public String readDbSingleValue(String url, String user, String pass, String query) throws Exception {
        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString(1).trim();
            }
        }
        return null;
    }

    /** Returns a full column of values for the given query. */
    public List<String> readDbColumn(String url, String user, String pass, String query) throws Exception {
        List<String> values = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                values.add(rs.getString(1).trim());
            }
        }
        return values;
    }

    // ============================================================
    // 3. VALIDATIONS  (UI  vs  DB  vs  Excel)
    // ============================================================

    /** UI value should equal the DB value. */
    public boolean validateUiAgainstDb(String uiValue, String dbValue) {
        return uiValue != null && uiValue.equals(dbValue);
    }

    /** UI value should equal the expected value read from the Excel sheet. */
    public boolean validateUiAgainstExcel(String uiValue, String excelValue) {
        return uiValue != null && uiValue.equals(excelValue);
    }

    /** Compare two full lists (e.g. UI column vs DB column) ignoring order. */
    public boolean validateListsMatch(List<String> uiList, List<String> dbList) {
        if (uiList.size() != dbList.size()) {
            return false;
        }
        return uiList.containsAll(dbList) && dbList.containsAll(uiList);
    }

    /** Strict, ordered comparison with a clear failure message. */
    public void assertEquals(String fieldName, String expected, String actual) {
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError(
                    "Data mismatch for [" + fieldName + "] -> expected: '" + expected
                            + "' but found: '" + actual + "'");
        }
    }
}
