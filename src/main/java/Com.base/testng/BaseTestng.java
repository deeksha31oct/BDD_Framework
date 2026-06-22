package Com.base.testng;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTestng {

    private static final Logger log = LogManager.getLogger(BaseTestng.class);

    // ===================================================================
    //  PATHS
    // ===================================================================

    // execution / extent report output path (timestamped)
    public static String getExecutionReportPath() {
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return System.getProperty("user.dir")
                + File.separator + "target"
                + File.separator + "ExtentReport_" + stamp + ".html";
    }

    // test data (Excel) path
    public static String getTestDataPath() {
        return System.getProperty("user.dir")
                + File.separator + "src"
                + File.separator + "test"
                + File.separator + "resources"
                + File.separator + "TestData"
                + File.separator + "TestData.xlsx";
    }

    // ===================================================================
    //  LOG FILE
    // ===================================================================

    // append a line to a plain text log file
    public static void writeLogFile(String message) {
        String logPath = System.getProperty("user.dir")
                + File.separator + "target"
                + File.separator + "execution.log";
        try (FileWriter fw = new FileWriter(logPath, true)) {   // true = append
            String stamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            fw.write("[" + stamp + "] " + message + System.lineSeparator());
        } catch (Exception e) {
            log.error("writeLogFile failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  EXTENT REPORT
    // ===================================================================

    // flush the Extent report (the actual ExtentReports lives in ListnerTest)
    public static void writeExtentReport() {
        try {
            ListnerTest.flushReport();   // add flushReport() in ListnerTest (see note)
            log.info("Extent report written: " + getExecutionReportPath());
        } catch (Exception e) {
            log.error("writeExtentReport failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  CUCUMBER OPTIONS
    // ===================================================================

    // returns the runtime cucumber options as a String[] (feature path, glue, plugins)
    public static String[] getCucumberOptions() {
        return new String[]{
                "--glue", "com.br.stepdefinations",
                "--plugin", "pretty",
                "--plugin", "json:target/cucumber.json",
                "--plugin", "html:target/cucumber-html-report",
                System.getProperty("user.dir") + File.separator + "FeatureFiles"
        };
    }

    // ===================================================================
    //  CLEAN CUCUMBER JSON (remove before/after hook steps)
    // ===================================================================

    // strips the "before" and "after" hook entries from cucumber.json so the report is clean
    public static void removeHookStepsFromCucumberJson() {
        String jsonPath = System.getProperty("user.dir")
                + File.separator + "target"
                + File.separator + "cucumber.json";
        try {
            File f = new File(jsonPath);
            if (!f.exists()) {
                log.warn("cucumber.json not found at: " + jsonPath);
                return;
            }
            String content = new String(Files.readAllBytes(Paths.get(jsonPath)));

            // remove "before":[...] and "after":[...] arrays from each scenario element
            content = content.replaceAll("\"before\"\\s*:\\s*\\[.*?\\],?", "");
            content = content.replaceAll("\"after\"\\s*:\\s*\\[.*?\\],?", "");

            try (FileWriter fw = new FileWriter(jsonPath, false)) {  // false = overwrite
                fw.write(content);
            }
            log.info("Removed before/after hook steps from cucumber.json");
        } catch (Exception e) {
            log.error("removeHookStepsFromCucumberJson failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  RUN UNTIL JAR (execute the runnable jar)
    // ===================================================================

    // runs the built executable jar from /target and waits for it to finish
    public static int runUntilJar(String jarName) {
        try {
            String jarPath = System.getProperty("user.dir")
                    + File.separator + "target"
                    + File.separator + jarName;

            ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath);
            pb.inheritIO();   // pipe child output to console
            Process process = pb.start();
            int exitCode = process.waitFor();   // block until jar finishes
            log.info("Jar finished with exit code: " + exitCode);
            return exitCode;
        } catch (Exception e) {
            log.error("runUntilJar failed: " + e.getMessage());
            return -1;
        }
    }
}
