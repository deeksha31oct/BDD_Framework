package com.br.stepdefinations;

import com.br.busniesslibarary.browserIntialtes;
import com.br.cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;

public class OrderSteps {

    private final TestContext context;

    // PicoContainer injects the SAME TestContext into every step class
    public OrderSteps(TestContext context) {
        this.context = context;
    }

    @When("user captures the order id")
    public void captureOrderId() {
        String orderId = browserIntialtes.getDriver()
                .findElement(By.id("orderId")).getText();
        context.getRunTimeTestData().setContext("orderId", orderId);   // SET
    }

    @Then("user verifies the order id on the summary page")
    public void verifyOrderId() {
        String orderId = context.getRunTimeTestData().getContextAsString("orderId");  // FETCH
        // assert orderId appears on the summary page
    }
}