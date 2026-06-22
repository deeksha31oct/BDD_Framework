package com.br.stepdefinations;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class LoginSteps {

    private final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @Given("user is on the OrangeHRM login page")
    public void user_is_on_login_page() {
        hooks.driver.get(LOGIN_URL);
    }

    @When("user enters username {string} and password {string}")
    public void user_enters_credentials(String username, String password) {
        hooks.driver.findElement(By.name("username")).sendKeys(username);
        hooks.driver.findElement(By.name("password")).sendKeys(password);
    }

    @And("user clicks the login button")
    public void user_clicks_login() {
        hooks.driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    @Then("user should see the dashboard")
    public void user_should_see_dashboard() {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[text()='Dashboard']")));
        Assert.assertTrue(header.isDisplayed(), "Dashboard not displayed");
    }

    @Then("user should see an {string} error")
    public void user_should_see_error(String expectedError) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'oxd-alert-content-text')]")));
        Assert.assertEquals(error.getText().trim(), expectedError, "Error message mismatch");
    }
}