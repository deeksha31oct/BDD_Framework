package com.br.busniesslibarary;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

public class browserIntialtes {

    // ThreadLocal -> each thread gets its own driver (parallel-safe)
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // launch the browser based on the type
    public static void initializeBrowser(String browser) {
        WebDriver webDriver;
        try {
            switch (browser.toLowerCase()) {
                case "chrome":
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--start-maximized");
                    options.addArguments("--remote-allow-origins=*");
                    webDriver = new ChromeDriver(options);
                    break;
                case "firefox":
                    webDriver = new FirefoxDriver();
                    break;
                case "edge":
                    webDriver = new EdgeDriver();
                    break;
                default:
                    throw new IllegalArgumentException("Browser not supported: " + browser);
            }

            webDriver.manage().window().maximize();
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

            driver.set(webDriver);
            System.out.println(browser + " browser launched");

        } catch (Exception e) {
            System.out.println("initializeBrowser failed: " + e.getMessage());
        }
    }

    // get the current thread's driver
    public static WebDriver getDriver() {
        return driver.get();
    }

    // launch a URL
    public static void launchUrl(String url) {
        if (getDriver() != null) {
            getDriver().get(url);
            System.out.println("Navigated to: " + url);
        }
    }

    // quit and clean up
    public static void quitDriver() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
            System.out.println("Browser closed");
        }
    }
}