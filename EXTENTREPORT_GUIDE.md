# ExtentReport Integration Guide

## Overview
This guide explains how the ExtentReport framework has been integrated into the BDD Framework for generating comprehensive test execution reports with screenshots, logs, and detailed test metrics.

## Components Created

### 1. **ExtentReportManager.java**
Main utility class that handles all ExtentReport operations.

**Key Methods:**
- `initializeExtentReport()` - Initialize ExtentReports with configuration
- `createTest(testName, testDescription)` - Create a new test in the report
- `logInfo(message)` - Log informational step
- `logPass(message)` - Log passed step (GREEN)
- `logFail(message)` - Log failed step (RED)
- `logWarning(message)` - Log warning step (ORANGE)
- `logSkip(message)` - Log skipped step (BLUE)
- `attachScreenshot(driver, screenshotName)` - Attach screenshot to report
- `logException(exception)` - Log exception details
- `flushExtentReport()` - Finalize and generate HTML report

### 2. **ScreenshotUtility.java**
Handles screenshot capture functionality.

**Key Methods:**
- `captureScreenshot(driver, screenshotName)` - Capture screenshot with timestamp
- `captureScreenshot(driver)` - Capture screenshot with auto-generated name
- `getScreenshotPath()` - Get the screenshot directory path

**Screenshot Storage:** `test-output/Screenshots/`

### 3. **ExtentTestListener.java**
TestNG listener that captures test events and logs them to ExtentReports.

**Implemented Methods:**
- `onStart(ITestContext)` - Before test suite execution
- `onFinish(ITestContext)` - After test suite execution
- `onTestStart(ITestResult)` - Before individual test
- `onTestSuccess(ITestResult)` - When test passes
- `onTestFailure(ITestResult)` - When test fails
- `onTestSkipped(ITestResult)` - When test is skipped
- `onTestFailedWithTimeout(ITestResult)` - When test times out

### 4. **BaseTest.java**
Base class for all test classes providing common setup/teardown.

**Methods:**
- `setUp()` - Initializes ExtentReports and WebDriver before each test
- `tearDown()` - Closes WebDriver and logs completion after each test
- `getDriver()` - Returns current WebDriver instance

### 5. **SampleTest.java**
Template test class demonstrating ExtentReport usage.

**Sample Tests:**
- `testSampleLogin()` - Demonstrates basic logging and screenshots
- `testApplicationLoad()` - Smoke test example
- `testElementPresence()` - Regression test example

---

## Report Structure

### Generated Report Path
`test-output/ExtentReport/ExtentReport.html`

### Report Features
- **Dark Theme** - Professional dark UI
- **Detailed Metrics** - Pass/Fail/Skip statistics
- **System Information** - OS, Java version, execution time
- **Screenshots** - Attached to relevant test steps
- **Exception Details** - Full stack traces for failures
- **Execution Time** - Duration for each test and step

---

## How to Use

### Step 1: Create a New Test Class

```java
package Base;

import org.testng.Assert;
import org.testng.annotations.Test;
import Com.base.testng.ExtentReportManager;
import Com.base.Extradata.ScreenshotUtility;

public class YourTestClass extends BaseTest {

    @Test(description = "Test description here")
    public void testYourScenario() {
        try {
            // Step 1
            ExtentReportManager.logInfo("Step 1: Your step description");
            // Your test code

            // Step 2
            ExtentReportManager.logInfo("Step 2: Another step");
            // More test code
            Assert.assertTrue(condition, "Assertion message");
            ExtentReportManager.logPass("Step passed successfully");

            // Capture screenshot
            ScreenshotUtility.captureScreenshot(driver, "StepName");
            ExtentReportManager.attachScreenshot(driver, "StepName");

        } catch (Exception e) {
            ExtentReportManager.logFail("Test failed: " + e.getMessage());
            ExtentReportManager.logException(e);
            ScreenshotUtility.captureScreenshot(driver, "Failure");
            ExtentReportManager.attachScreenshot(driver, "Failure");
            throw new RuntimeException(e);
        }
    }
}
```

### Step 2: Add Test Class to testng.xml

```xml
<test name="BDD_Tests" enabled="true">
    <parameter name="browser" value="chrome"/>
    <classes>
        <class name="Base.YourTestClass"/>
    </classes>
</test>
```

### Step 3: Run Tests

**Using Maven:**
```bash
mvn test
```

**Using TestNG:**
```bash
testng testng.xml
```

### Step 4: View Report

Open the generated HTML report:
- Windows: `test-output/ExtentReport/ExtentReport.html`
- Use any web browser to view the report

---

## Logging Examples

### Information Logs
```java
ExtentReportManager.logInfo("Navigating to login page");
```

### Pass Logs (Green)
```java
ExtentReportManager.logPass("Login successful");
```

### Fail Logs (Red)
```java
ExtentReportManager.logFail("Unable to find login button");
```

### Warning Logs (Orange)
```java
ExtentReportManager.logWarning("Timeout occurred, retrying...");
```

### Skip Logs (Blue)
```java
ExtentReportManager.logSkip("Test skipped due to prerequisites");
```

### Exception Logs
```java
try {
    // code
} catch (Exception e) {
    ExtentReportManager.logException(e);
}
```

---

## Test Groups and Filtering

### Define Test Groups

```java
@Test(groups = {"smoke"})
public void testSmokeTest() { }

@Test(groups = {"regression"})
public void testRegressionTest() { }

@Test(groups = {"sanity"})
public void testSanityTest() { }
```

### Run Specific Groups

**Run only smoke tests:**
```bash
mvn test -Dgroups=smoke
```

**Run specific suite from testng.xml:**
```bash
mvn test -Dtest=testng.xml
```

---

## Configuration Details

### System Information Added to Report
- Application Name: BDD Framework
- User Name: Current system user
- OS: Operating System name
- Java Version: JDK version
- Report Date: Execution date and time

### Report Theme
- **Dark Theme** - Professional appearance
- **Responsive Design** - Works on all browsers
- **Timestamps** - Every log entry is timestamped

---

## Directory Structure

```
BDD_Framework/
├── test-output/
│   ├── ExtentReport/
│   │   └── ExtentReport.html (Generated Report)
│   └── Screenshots/
│       └── Screenshot_*.png (Captured screenshots)
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── Base/
│   │       │   ├── BaseTest.java
│   │       │   ├── SampleTest.java
│   │       │   ├── Driverkeyword.java
│   │       │   └── Main.java
│   │       └── utilites/
│   │           ├── ExtentReportManager.java
│   │           ├── ScreenshotUtility.java
│   │           ├── ExtentTestListener.java
│   │           ├── BrowserFactory.java
│   │           └── ConfigReader.java
│   └── test/
│       └── resources/
└── testng.xml
```

---

## Troubleshooting

### Report Not Generated
- Check if `test-output` directory has write permissions
- Verify `ExtentReportManager.flushExtentReport()` is called
- Ensure listener is properly configured in testng.xml

### Screenshots Not Attached
- Verify WebDriver instance is available when capturing
- Check `test-output/Screenshots/` directory exists
- Ensure `attachScreenshot()` is called after capturing

### Listener Not Running
- Verify listener class name is correct in testng.xml: `utilites.ExtentTestListener`
- Check test classes extend `BaseTest` or call `ExtentReportManager` methods
- Ensure TestNG version is compatible

### Report Shows Incomplete Information
- Call `flushExtentReport()` in test suite teardown
- Ensure all tests use ExtentReportManager for logging
- Check that `onFinish()` listener method is executed

---

## Best Practices

1. **Always Use BaseTest** - Extend BaseTest class in all test classes for consistent setup/teardown
2. **Log Every Step** - Use `logInfo()` for every test step
3. **Capture Screenshots** - Attach screenshots at critical points and on failures
4. **Use Meaningful Names** - Give descriptive names to tests and screenshots
5. **Handle Exceptions** - Always catch and log exceptions with `logException()`
6. **Group Tests** - Use test groups for better organization
7. **Update testng.xml** - Add new test classes to testng.xml suite
8. **Regular Cleanup** - Archive old reports periodically

---

## Dependencies

Ensure the following are in pom.xml:
- `extentreports` (5.0.9)
- `testng` (7.10.2)
- `selenium-java` (4.24.0)

---

## Support and Documentation

For more information:
- ExtentReports: https://extentreports.com/
- TestNG: https://testng.org/
- Selenium: https://www.selenium.dev/

---

**Last Updated:** 2026-06-19
**Framework Version:** 1.0
