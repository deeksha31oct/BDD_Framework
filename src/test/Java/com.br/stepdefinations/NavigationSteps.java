package com.br.stepdefinations;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class NavigationSteps {

    @Given("user is logged into OrangeHRM")
    public void user_is_logged_in() {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        hooks.driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        hooks.driver.findElement(By.name("username")).sendKeys("Admin");
        hooks.driver.findElement(By.name("password")).sendKeys("admin123");
        hooks.driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[text()='Dashboard']")));
    }

    @When("user clicks on {string} menu")
    public void user_clicks_menu(String menuName) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='" + menuName + "']"))).click();
    }

    @Then("user should see the {string} page")
    public void user_should_see_page(String pageName) {
        WebDriverWait wait = new WebDriverWait(hooks.driver, Duration.ofSeconds(15));
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[text()='" + pageName + "']")));
        Assert.assertEquals(header.getText().trim(), pageName, "Page header mismatch");
    }
}