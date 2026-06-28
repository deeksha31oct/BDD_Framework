package com.br.stepdefinations;

import Com.base.ExcelFuntions.ExcelMangerFillo;
import Com.base.globalvariable.Framework_globalvariable;
import com.br.busniesslibarary.browserIntialtes;
import com.br.testglobalvariable.GlobalVariables;
import com.br.testglobalvariable.Text_GlobalVariable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class commonsteps {

    // helper - fresh wait using the current thread's driver
    private WebDriverWait getWait() {                    // renamed from wait()
        return new WebDriverWait(browserIntialtes.getDriver(), Duration.ofSeconds(20));
    }

    // 1. capture ONE scenario's complete test data (row 0) into globals
    @Given("capture complete scenario test data from sheet {string}")
    public void captureCompleteScenarioTestData(String sheetName, String excel) {
        String completeExcelPath = Text_GlobalVariable.completedTestData + Text_GlobalVariable.global_fs+ excel;
        ExcelMangerFillo fillo = new ExcelMangerFillo(completeExcelPath);
        String query = "Select * from "+ sheetName + "where TC_id"+ Framework_globalvariable.TC_ID+ "";
        Text_GlobalVariable.completedTestData= (List<Map<String, String>>) fillo.readSpreadSheet(completeExcelPath,sheetName,query);
                     // assigns row 0 to globals
        System.out.println("Captured scenario test data from: " + sheetName);
    }

    // 2. capture MULTIPLE scenarios' test data (all rows)
    @Given("capture multiple scenario test data from sheet {string}")
    public void captureMultipleCompleteScenarioTestData(String sheetName) {
        Text_GlobalVariable.readExcelData(sheetName);
        List<Map<String, String>> allRows = Text_GlobalVariable.completedTestData;
        System.out.println("Captured " + allRows.size() + " rows of scenario data");
    }

    // 3. read a specific value from the sheet by key (from row 0)
    @Given("read data {string} from sheet")
    public void readDataFromSheet(String key) {
        if (!Text_GlobalVariable.completedTestData.isEmpty()) {
            String value = Text_GlobalVariable.completedTestData.get(0).get(key);
            System.out.println(key + " = " + value);
        } else {
            System.out.println("No data found - read the sheet first");
        }
    }
    @Given("^Read all the data from '(.+)' in \"([^\"]*)\"$")
    public void read_All_the_Data_From_(String sheetName,String excel) throws Throwable {
        Text_GlobalVariable.completedTestData= null;
        captureCompleteScenarioTestData(sheetName,excel);
        GlobalVariables.loadGlobalVariables();

       // updateGV.Framework_globalvariables();
    }

    // 4. navigate to the search screen
    @When("user navigates to the search screen")
    public void navigateToSearchScreen() {
        getWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[normalize-space()='Search']"))).click();
    }

    // 5. user login with valid credentials (from globals)
    @When("user logs in with valid credentials")
    public void userLoginWithValidCredentials() {
        browserIntialtes.getDriver().findElement(By.name("username"))
                .sendKeys(GlobalVariables.USERNAME);
        browserIntialtes.getDriver().findElement(By.name("password"))
                .sendKeys(GlobalVariables.PASSWORD);
        browserIntialtes.getDriver().findElement(By.cssSelector("button[type='submit']"))
                .click();
    }

    // 6. validate a screen is displayed (by header/text)
    @Then("user validates the {string} screen")
    public void userValidateScreen(String screenName) {
        WebElement header = getWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[normalize-space()='" + screenName + "']")));
        Assert.assertTrue(header.isDisplayed(), screenName + " screen not displayed");
    }

    // 7. wait for the page to load completely (document.readyState)
    @When("user waits for the page to load completely")
    public void waitForPageLoad() {
        getWait().until(d -> ((JavascriptExecutor) d)
                .executeScript("return document.readyState").equals("complete"));
        System.out.println("Page loaded completely");
    }

    // 8. click the Next button
    @When("user clicks the Next button")
    public void clickNextButton() {
        getWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Next']"))).click();
    }

    // 9. validate document title on the summary page
    @Then("user validates the summary page title is {string}")
    public void validateSummaryPageTitle(String expectedTitle) {
        String actualTitle = browserIntialtes.getDriver().getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "Summary page title mismatch");
    }
}