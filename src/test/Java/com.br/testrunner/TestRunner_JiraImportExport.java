package com.br.testrunner;

//import com.br.jirautility.JiraUtility;          // your Jira helper class
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@CucumberOptions(
        features = "FeatureFiles",
        glue = {"com.br.stepdefinations"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/jira-report.html",
                "json:target/cucumber-reports/jira-report.json"
        },
        monochrome = true,
        dryRun = false,
        tags = "@jira"                                    // run only Jira-linked scenarios
)
public class TestRunner_JiraImportExport {

    private TestNGCucumberRunner testNGCucumberRunner;
    // 2. @Test - runs ONE scenario per DataProvider row
    @Test(groups = "cucumber", description = "Runs Jira-linked Scenarios", dataProvider = "scenarios")
    public void runScenario(PickleWrapper pickleWrapper, FeatureWrapper featureWrapper) throws Throwable {
        testNGCucumberRunner.runScenario(pickleWrapper.getPickle());
    }

    // 3. @DataProvider - supplies all scenarios to @Test
    @DataProvider
    public Object[][] scenarios() {
        if (testNGCucumberRunner == null) {
            return new Object[0][0];
        }
        return testNGCucumberRunner.provideScenarios();
    }

    // 4. @AfterClass - finish runner + EXPORT results back to Jira
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (testNGCucumberRunner != null) {
            testNGCucumberRunner.finish();
        }

    }
}