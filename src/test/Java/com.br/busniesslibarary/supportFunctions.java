package com.br.busniesslibarary;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class supportFunctions {

    // 1. selectionOfDriver - return a WebDriver based on the browser name
    public WebDriver selectionOfDriver(String browser) {
        WebDriver driver = null;
        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    driver = new ChromeDriver();      // Selenium 4 auto-resolves the driver
                    break;
                case "firefox":
                    driver = new FirefoxDriver();
                    break;
                case "edge":
                    driver = new EdgeDriver();
                    break;
                default:
                    System.out.println("Browser not supported: " + browser);
            }
        } catch (Exception e) {
            System.out.println("selectionOfDriver failed: " + e.getMessage());
        }
        return driver;
    }

    // 2. killingTheExistingProcessor - force-kill a Windows process by name
    public void killingTheExistingProcessor(String processName) {
        try {
            // /F = force, /IM = image name (e.g. "chromedriver.exe")
            Runtime.getRuntime().exec("taskkill /F /IM " + processName);
            System.out.println("Killed process: " + processName);
        } catch (IOException e) {
            System.out.println("killingTheExistingProcessor failed: " + e.getMessage());
        }
    }

    // 3. checkProcessInTaskManager - true if a process is currently running
    public boolean checkProcessInTaskManager(String processName) {
        boolean isRunning = false;
        try {
            Process process = Runtime.getRuntime().exec("tasklist");   // lists all running processes
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(processName.toLowerCase())) {
                    isRunning = true;
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("checkProcessInTaskManager failed: " + e.getMessage());
        }
        return isRunning;
    }
}