package Com.base.testng;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Reproter {

    private static final Logger log = LogManager.getLogger(Reproter.class);

    private static ExtentReports extent;
    private static String reportPath;

    // feature-level nodes, keyed by feature name (so we reuse the same feature node)
    private static final Map<String, ExtentTest> featureMap = new HashMap<>();

    // current scenario + step held per thread (parallel-safe)
    private static final ThreadLocal<ExtentTest> currentScenario = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> currentStep = new ThreadLocal<>();

    // ===================================================================
    //  INIT / FLUSH
    // ===================================================================

    // initiate Extent logs (create the report object once)
    public static void initiateExtentLogs() {
        if (extent != null) return;   // already initialised

        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        reportPath = System.getProperty("user.dir")
                + File.separator + "target"
                + File.separator + "ExtentReport_" + stamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

        // load XML config if present
        try {
            File cfg = new File(System.getProperty("user.dir")
                    + "/src/main/resources/BaseConfiguration/extend-config.xml");
            if (cfg.exists()) spark.loadXMLConfig(cfg);
        } catch (Exception e) {
            log.warn("Spark XML config not loaded: " + e.getMessage());
        }

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Project", "ORANGE_FRAMEWORK");
        log.info("Extent report initialised: " + reportPath);
    }

    // flush - MANDATORY at end of run, writes the html file
    public static void flush() {
        if (extent != null) {
            extent.flush();
            log.info("Extent report flushed: " + reportPath);
        }
    }

    // ===================================================================
    //  FEATURE NODES
    // ===================================================================

    // create a feature node (top level)
    public static ExtentTest createExtentTestFeatureNode(String featureName) {
        if (extent == null) initiateExtentLogs();
        ExtentTest feature = extent.createTest(
                com.aventstack.extentreports.gherkin.model.Feature.class, featureName);
        featureMap.put(featureName, feature);
        return feature;
    }

    // alias kept for naming you asked for
    public static ExtentTest createFeatureFileNode(String featureName) {
        return createExtentTestFeatureNode(featureName);
    }

    // check if a feature node already exists
    public static boolean checkFeatureIsAvailable(String featureName) {
        return featureMap.containsKey(featureName);
    }

    // get an existing feature node (or create it if missing)
    public static ExtentTest getExistingFeature(String featureName) {
        if (checkFeatureIsAvailable(featureName)) {
            return featureMap.get(featureName);
        }
        return createExtentTestFeatureNode(featureName);
    }

    // ===================================================================
    //  SCENARIO NODES
    // ===================================================================

    // create a plain extent test (non-gherkin) - generic
    public static ExtentTest createExtentTest(String testName) {
        if (extent == null) initiateExtentLogs();
        ExtentTest t = extent.createTest(testName);
        currentScenario.set(t);
        return t;
    }

    // add a scenario node under a feature
    public static ExtentTest addScenarioNode(String featureName, String scenarioName) {
        ExtentTest feature = getExistingFeature(featureName);
        ExtentTest scenario = feature.createNode(
                com.aventstack.extentreports.gherkin.model.Scenario.class, scenarioName);
        currentScenario.set(scenario);
        return scenario;
    }

    // ===================================================================
    //  STEP NODES
    // ===================================================================

    // create a step node under the current scenario
    public static ExtentTest createStepNode(String stepName) {
        ExtentTest scenario = currentScenario.get();
        if (scenario == null) {
            log.warn("No current scenario - creating step as standalone test");
            currentStep.set(createExtentTest(stepName));
        } else {
            currentStep.set(scenario.createNode(stepName));
        }
        return currentStep.get();
    }

    // ===================================================================
    //  STEP LOGS
    // ===================================================================

    // generic info step log (logs on the step if present, else scenario)
    public static void addStepLog(String message) {
        ExtentTest node = (currentStep.get() != null) ? currentStep.get() : currentScenario.get();
        if (node != null) node.log(Status.INFO, message);
        log.info(message);
    }

    // pass step log
    public static void addPassStepLog(String message) {
        ExtentTest node = (currentStep.get() != null) ? currentStep.get() : currentScenario.get();
        if (node != null) node.log(Status.PASS, message);
        log.info("PASS: " + message);
    }

    // fail step log
    public static void addFailStepLog(String message) {
        ExtentTest node = (currentStep.get() != null) ? currentStep.get() : currentScenario.get();
        if (node != null) node.log(Status.FAIL, message);
        log.error("FAIL: " + message);
    }

    // ===================================================================
    //  MARKUP (JSON / XML)
    // ===================================================================

    public static void addJsonMarkup(String json) {
        ExtentTest node = (currentStep.get() != null) ? currentStep.get() : currentScenario.get();
        if (node != null) node.log(Status.INFO, MarkupHelper.createCodeBlock(json));
    }

    public static void addXmlMarkup(String xml) {
        ExtentTest node = (currentStep.get() != null) ? currentStep.get() : currentScenario.get();
        if (node != null) node.log(Status.INFO, MarkupHelper.createCodeBlock(xml));
    }

    // ===================================================================
    //  SCREENSHOT
    // ===================================================================

    // attach a screenshot from a file path to the current node
    public static void addScreenshotFromPath(String screenshotPath) {
        ExtentTest node = (currentStep.get() != null) ? currentStep.get() : currentScenario.get();
        try {
            if (node != null)
                node.log(Status.INFO, "Screenshot",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        } catch (Exception e) {
            log.error("addScreenshotFromPath failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  CATEGORY / TAGS
    // ===================================================================

    // assign a category (e.g. @smoke) to the current scenario
    public static void setCategoryName(String category) {
        ExtentTest scenario = currentScenario.get();
        if (scenario != null) scenario.assignCategory(category);
    }

    // ===================================================================
    //  ENHANCE HTML REPORT
    // ===================================================================

    // post-run cosmetic / system-info touches on the html report
    public static void enhanceHtmlReport() {
        if (extent == null) return;
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Report Path", reportPath);
        log.info("HTML report enhanced with system info");
    }
}