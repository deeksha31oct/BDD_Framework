package com.br.stepdefinations;

import com.br.busniesslibarary.browserIntialte;
import io.cucumber.java.en.Given;

public class BrowserSteps {

    @Given("Browser Selection is {string}")
    public void browser_selection_is(String browser) {
        browserIntialte.initializeBrowser(browser);   // launches Chrome/Firefox/Edge
    }
}
