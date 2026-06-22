package com.br.Hornback_stepdef;

import com.br.stepdefinations.hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class FactoryExpensesLabourSteps {

    @Given("user is on the Factory Expenses page")
    public void user_on_factory_expenses_page() {
        // navigate directly to the factory-expenses page
        hooks.driver.get("https://concur-dandy-crabmeat.ngrok-free.dev/factory-expenses");
    }

    @When("user selects {string} from the view month dropdown")
    public void user_selects_month(String month) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        // if it's a native <select>, use Select; adjust locator after inspecting
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//select")));
        new Select(dropdown).selectByVisibleText(month);
    }

    @When("user clicks on the {string} tab")
    public void user_clicks_tab(String tabName) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + tabName + "']"))).click();
    }

    @Then("the labour entries table should be displayed")
    public void labour_table_displayed() {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table")));
        Assert.assertTrue(table.isDisplayed(), "Labour table not displayed");
    }

    @Then("the table should contain category {string}")
    public void table_contains_category(String category) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        WebElement cell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[normalize-space()='" + category + "'] | //*[normalize-space()='" + category + "']")));
        Assert.assertTrue(cell.isDisplayed(), "Category not found: " + category);
    }

    @When("user clicks on the {string} button")
    public void user_clicks_button(String buttonText) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(),'" + buttonText + "')]"))).click();
    }

    @Then("the add labour entry form should be displayed")
    public void add_labour_form_displayed() {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        // adjust to whatever appears - a modal, a form, or an input field
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//form | //*[contains(@class,'modal')] | //input")));
        Assert.assertTrue(form.isDisplayed(), "Add labour entry form not displayed");
    }
}