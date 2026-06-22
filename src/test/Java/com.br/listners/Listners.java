package com.br.listners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.br.busniesslibarary.browserIntialtes;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listners implements ITestListener {

    private static ExtentReports extent;
    // ThreadLocal -> safe for parallel execution
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // runs ONCE before all tests - set up the report
    @Override
    public void onStart(ITestContext context) {
        ExtentSparkReporter spark = new ExtentSparkReporter(
                System.getProperty("user.dir") + "/target/ExtentReport.html");
        spark.config().setReportName("Automation Test Report");
        spark.config().setDocumentTitle("Test Results");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Tester", "QA Team");
        extent.setSystemInfo("Environment", "QA");
    }

    // runs before EACH test - create a report entry
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    // on PASS
    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "Test Passed: " + result.getMethod().getMethodName());
    }

    // on FAIL - log error + attach screenshot
    @Override
    public void onTestFailure(ITestResult result) {
        test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        try {
            String base64 = ((TakesScreenshot) browserIntialtes.getDriver())
                    .getScreenshotAs(OutputType.BASE64);
            test.get().addScreenCaptureFromBase64String(base64, "Failure Screenshot");
        } catch (Exception e) {
            System.out.println("Screenshot capture failed: " + e.getMessage());
        }
    }

    // on SKIP
    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());
    }

    // runs ONCE after all tests - write the report to disk
    @Override
    public void onFinish(ITestContext context) {
        extent.flush();   // IMPORTANT: report is only written on flush()
    }
}