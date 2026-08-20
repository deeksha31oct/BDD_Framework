package com.br.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Hornback_Login {

    private WebDriver driver = null;
    private final WebDriverWait wait;

    public Hornback_Login() {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ---------- Locators (UPDATE with real values from the login page DOM) ----------
    private final By usernameField = By.id("username");          // UPDATE
    private final By passwordField = By.id("password");          // UPDATE
    private final By loginButton   = By.cssSelector("button[type='submit']"); // UPDATE
    private final By ssoButton     = By.xpath("//button[normalize-space()='Login with SSO']"); // UPDATE
    private final By errorMessage  = By.cssSelector(".alert-error, .error-message");           // UPDATE
    private final By errorCloseBtn = By.cssSelector(".alert-error .close, .error-message .close"); // UPDATE
    private final By dashboardHeader = By.xpath("//*[normalize-space()='OVERVIEW']");          // post-login check

    // ---------- 1. Login into the app with username & password ----------
    public void login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField)).clear();
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    // ---------- 2. SSO validation (click SSO and confirm redirect / SSO screen) ----------
    public boolean loginWithSSO() {
        wait.until(ExpectedConditions.elementToBeClickable(ssoButton)).click();
        // Validate the SSO provider page loaded (URL changes to the IdP, or an SSO element appears)
        return wait.until(ExpectedConditions.urlContains("sso"));   // UPDATE the expected SSO URL fragment
    }

    // ---------- 3. Open the app URL WITHOUT maximizing the window ----------
    // Note: window sizing belongs in the driver setup, not the page object.
    // Call this from your BaseTest/Hooks instead of driver.manage().window().maximize().
    public void openWithoutMaximize(String url) {
        // Do NOT call driver.manage().window().maximize();
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }

    // ---------- 4. Verify the error message text ----------
    public boolean isErrorDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).isDisplayed();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage))
                .getText().trim();
    }

    public void verifyErrorMessage(String expected) {
        String actual = getErrorMessage();
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected error: '" + expected + "' but got: '" + actual + "'");
        }
    }

    // ---------- 5. Close the error popup ----------
    public void closeErrorPopup() {
        WebElement close = wait.until(ExpectedConditions.elementToBeClickable(errorCloseBtn));
        close.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(errorMessage));
    }

    // ---------- Helper: confirm successful login ----------
    public boolean isDashboardDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader)).isDisplayed();
    }
}