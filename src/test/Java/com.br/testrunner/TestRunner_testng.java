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
        features = "FeatureFiles",                          // path to .feature files
        glue = {"com.br.stepdefinations"},                  // step definitions + hooks package
        plugin = {"pretty", "html:target/cucumber-reports/report.html", "json:target/cucumber-reports/report.json"},
        monochrome = true,
        dryRun = false,
        tags = ("@TCid_001")
)
public class TestRunner_testng {

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