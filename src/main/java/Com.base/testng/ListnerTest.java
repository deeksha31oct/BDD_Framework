package Com.base.testng;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static Com.base.Extradata.Driverkeyword.driver;

public class ListnerTest implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // exposed so Assertlogs (and others) can borrow the current test
    public static ExtentTest getTest() {
        return test.get();
    }

    public static void flushReport() {
    }

    // ---------------- SUITE START ----------------
    @Override
    public void onStart(ITestContext context) {

    }

    // ---------------- PER-TEST ----------------
    @Override
    public void onTestStart(ITestResult result) {
        test.set(extent.createTest(result.getMethod().getMethodName()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (test.get() != null)
            test.get().log(Status.PASS, "PASSED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = test.get();
        if (t != null) {
            t.log(Status.FAIL, "FAILED: " + result.getMethod().getMethodName());
            if (result.getThrowable() != null)
                t.log(Status.FAIL, result.getThrowable());
            String b64 = browserScreenshotBase64();
            if (b64 != null)
                t.addScreenCaptureFromBase64String(b64, "Failure Screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (test.get() != null)
            test.get().log(Status.SKIP, "SKIPPED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        if (test.get() != null)
            test.get().log(Status.WARNING,
                    "FAILED WITHIN SUCCESS %: " + result.getMethod().getMethodName());
    }

    // ---------------- SUITE END ----------------
    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) extent.flush();   // MANDATORY - writes the report
    }

    // ---------------- helper ----------------
    private static String browserScreenshotBase64() {
        try {
            return ((TakesScreenshot)driver)
                    .getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            System.out.println("Screenshot failed: " + e.getMessage());
            return null;
        }
    }
}