package com.br.Hornback_stepdef;

import com.br.stepdefinations.hooks;
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

public class FactoryExpensesNavigationSteps {

    // change this to your current ngrok URL
    private final String BASE_URL = "https://concur-dandy-crabmeat.ngrok-free.dev/";

    @Given("user is on the Hornback IMS application")
    public void user_on_hornback_app() {
        hooks.driver.get(BASE_URL);
    }

    @When("user clicks on {string} in the sidebar")
    public void user_clicks_sidebar_item(String menuName) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space()='" + menuName + "']"))).click();
    }

    @And("user clicks on {string} in the sidebar")
    public void user_clicks_sidebar_subitem(String menuName) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space()='" + menuName + "']"))).click();
    }

    @Then("the Factory Expenses page should be displayed")
    public void factory_expenses_page_displayed() {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'FACTORY EXPENSES')]")));
        Assert.assertTrue(heading.isDisplayed(), "Factory Expenses page not displayed");
    }
}