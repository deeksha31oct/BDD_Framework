package utilites;

import com.aventstack.extentreports.Status;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * ExtentTestListener implements ITestListener interface to capture test events
 * and log them to ExtentReports
 */
public class ExtentTestListener implements ITestListener {

    /**
     * Invoked before running all the Test(s) inside a <test> tag
     */
    @Override
    public void onStart(org.testng.ITestContext context) {
        System.out.println("========== Test Execution Started ==========");
        System.out.println("Test Suite: " + context.getName());
    }

    /**
     * Invoked after running all the Test(s) inside a <test> tag
     */
    @Override
    public void onFinish(org.testng.ITestContext context) {
        System.out.println("========== Test Execution Finished ==========");
        System.out.println("Total Tests: " + context.getAllTestMethods().length);
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size());

        // Flush ExtentReport
        ExtentReportManager.flushExtentReport();
    }

    /**
     * Invoked after the test starts (before first step)
     */
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("========== Test Started: " + result.getName() + " ==========");
        String testDescription = result.getMethod().getDescription() != null ?
                result.getMethod().getDescription() : "No description provided";

        // Create test in ExtentReport
        ExtentReportManager.createTest(result.getName(), testDescription);
        ExtentReportManager.logInfo("Test started: " + result.getName());
    }

    /**
     * Invoked when a test passes
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("========== Test Passed: " + result.getName() + " ==========");
        ExtentReportManager.logPass("Test PASSED: " + result.getName());
        ExtentReportManager.logInfo("Test Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
    }

    /**
     * Invoked when a test fails
     */
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("========== Test Failed: " + result.getName() + " ==========");
        ExtentReportManager.logFail("Test FAILED: " + result.getName());

        // Log failure cause
        if (result.getThrowable() != null) {
            ExtentReportManager.logException((Exception) result.getThrowable());
            System.err.println("Failure Cause: " + result.getThrowable().getMessage());
        }

        ExtentReportManager.logInfo("Test Duration: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
    }

    /**
     * Invoked when a test skips
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("========== Test Skipped: " + result.getName() + " ==========");
        ExtentReportManager.logSkip("Test SKIPPED: " + result.getName());

        if (result.getThrowable() != null) {
            ExtentReportManager.logSkip("Skip Reason: " + result.getThrowable().getMessage());
        }
    }

    /**
     * Invoked when test fails but within success percentage
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("Test failed but within success percentage: " + result.getName());
        ExtentReportManager.logWarning("Test failed but within success percentage: " + result.getName());
    }

    /**
     * Invoked when a test is configured to be executed
     */
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        System.out.println("========== Test Failed with Timeout: " + result.getName() + " ==========");
        ExtentReportManager.logFail("Test FAILED with TIMEOUT: " + result.getName());
        ExtentReportManager.logException((Exception) result.getThrowable());
    }
}
