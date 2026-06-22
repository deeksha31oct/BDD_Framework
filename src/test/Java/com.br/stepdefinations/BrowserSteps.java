package com.br.stepdefinations;

import com.br.busniesslibarary.browserIntialtes;
import io.cucumber.java.en.Given;

public class BrowserSteps {

    @Given("Browser Selection is {string}")
    public void browser_selection_is(String browser) {
        browserIntialtes.initializeBrowser(browser);   // launches Chrome/Firefox/Edge
    }
}