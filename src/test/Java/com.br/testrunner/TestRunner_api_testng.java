package com.br.testrunner;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
                features = "FeatureFiles",                          // covers all feature subfolders
                glue = {"com.br.stepdefinations", "API_Logic.API_StepDefinations"},  // BOTH glue packages
                                           // matches your feature tag
                plugin = {"pretty", "json:target/cucumber.json"},
                tags = "@AP_01"
        )

public class TestRunner_api_testng {

    private TestNGCucumberRunner testNGCucumberRunner;

    // 1. @BeforeClass - create the Cucumber runner before any scenario runs
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    // 2. @Test - runs ONE scenario; called once per scenario by the DataProvider
    @Test(groups = "cucumber", description = "Runs Cucumber Scenarios", dataProvider = "scenarios")
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) throws Throwable {
        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    // 3. @DataProvider - supplies all scenarios to the @Test method
    @DataProvider
    public Object[][] scenarios() {
        if (testNGCucumberRunner == null) {

            return new Object[0][0];
        }
        return testNGCucumberRunner.provideScenarios();
    }

    // 4. @AfterClass - finish the runner (flush reports, cleanup) after all scenarios
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (testNGCucumberRunner != null) {
            testNGCucumberRunner.finish();
        }
    }
}