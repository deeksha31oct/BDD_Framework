package com.br.stepdefinations;

import com.br.busniesslibarary.supportFunctions;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class hooks {

    public static WebDriver driver;
    private final supportFunctions sf = new supportFunctions();

    // @Before - prerequisites BEFORE each scenario (setup)
    @Before
    public void addPrerequisites(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
        // kill any leftover driver process before launching a fresh one
        if (sf.checkProcessInTaskManager("chromedriver.exe")) {
            sf.killingTheExistingProcessor("chromedriver.exe");
        }
        driver = sf.selectionOfDriver("chrome");
        driver.manage().window().maximize();
    }

    // @BeforeStep - runs BEFORE each step inside a scenario
    @BeforeStep
    public void beforeStep(Scenario scenario) {
        System.out.println("Executing next step in: " + scenario.getName());
    }

    // @After - cleanup AFTER each scenario
    @After
    public void tearDown(Scenario scenario) {
        closeBrowserSession();
        checkProcessInTaskManager();
        cleanupTempDirectories();
    }

    // close the browser session
    public void closeBrowserSession() {
        try {
            if (driver != null) {
                driver.quit();
                System.out.println("Browser session closed");
            }
        } catch (Exception e) {
            System.out.println("closeBrowserSession failed: " + e.getMessage());
        }
    }

    // check task manager and kill leftover driver process
    public void checkProcessInTaskManager() {
        try {
            if (sf.checkProcessInTaskManager("chromedriver.exe")) {
                sf.killingTheExistingProcessor("chromedriver.exe");
                System.out.println("Leftover driver process killed");
            }
        } catch (Exception e) {
            System.out.println("checkProcessInTaskManager failed: " + e.getMessage());
        }
    }

    // clean up temp directories created during the run
    public void cleanupTempDirectories() {
        try {
            String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "selenium_temp";
            deleteDirectory(new File(tempPath));
        } catch (Exception e) {
            System.out.println("cleanupTempDirectories failed: " + e.getMessage());
        }
    }

    // recursively delete a directory and all its contents
    public void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) return;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);   // recurse into sub-folders
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
        System.out.println("Deleted directory: " + directory.getPath());
    }
}