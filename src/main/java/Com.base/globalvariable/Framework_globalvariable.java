package Com.base.globalvariable;

import java.io.FileInputStream;
import java.util.Properties;

public class Framework_globalvariable {

    // ---------- File separator (cross-platform: \ on Windows, / on Linux) ----------
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    // ---------- Folder Paths ----------
    public static final String PROJECT_PATH    = System.getProperty("user.dir");
    public static final String RESOURCES_PATH  = PROJECT_PATH + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "resources";
    public static final String CONFIG_PATH     = RESOURCES_PATH + FILE_SEPARATOR + "config";
    public static final String TESTDATA_PATH   = RESOURCES_PATH + FILE_SEPARATOR + "testdata";
    public static final String OR_PATH         = RESOURCES_PATH + FILE_SEPARATOR + "objectRepository";
    public static final String REPORT_PATH     = PROJECT_PATH + FILE_SEPARATOR + "target" + FILE_SEPARATOR + "reports";
    public static final String SCREENSHOT_PATH = PROJECT_PATH + FILE_SEPARATOR + "target" + FILE_SEPARATOR + "screenshots";

    // ---------- Browser Variables ----------
    public static String browserName;
    public static boolean headless;
    public static int implicitWait;
    public static int explicitWait;
    public static int pageLoadTimeout;
            public static String TC_ID;

    // ---------- Cucumber Variables ----------
    public static String featuresPath;
    public static String gluePackage;
    public static String cucumberTags;

    // ---------- Extent Report Variables ----------
    public static String extentReportPath;
    public static String extentConfigPath;
    public static String reportTitle;
    public static String reportName;

    // ---------- Database Variables ----------
    public static String dbUrl;
    public static String dbUsername;
    public static String dbPassword;
    public static String dbDriver;

    // ---------- Browser Properties holder ----------
    public static Properties browserProperties = new Properties();

    // ---------- getProperty: read a String value from a .properties file ----------
    public static String getProperty(String fileName, String propertyName) {
        String value = null;
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH + FILE_SEPARATOR + fileName)) {
            Properties prop = new Properties();
            prop.load(fis);
            value = prop.getProperty(propertyName);
        } catch (Exception e) {
            System.out.println("getProperty failed [" + fileName + " -> " + propertyName + "]: " + e.getMessage());
        }
        return value;
    }

    // ---------- getIntProperty: read a property and convert to int ----------
    public static int getIntProperty(String fileName, String propertyName) {
        int value = 0;
        try {
            String prop = getProperty(fileName, propertyName);
            if (prop != null) {
                value = Integer.parseInt(prop.trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("getIntProperty failed [" + propertyName + "]: " + e.getMessage());
        }
        return value;
    }

    // ---------- load all config values into the variables (call once at startup) ----------
    public static void loadFrameworkConfig() {
        String file = "config.properties";

        // browser
        browserName     = getProperty(file, "browser");
        headless        = Boolean.parseBoolean(getProperty(file, "headless"));
        implicitWait    = getIntProperty(file, "implicitWait");
        explicitWait    = getIntProperty(file, "explicitWait");
        pageLoadTimeout = getIntProperty(file, "pageLoadTimeout");

        // cucumber
        featuresPath = getProperty(file, "featuresPath");
        gluePackage  = getProperty(file, "gluePackage");
        cucumberTags = getProperty(file, "cucumberTags");

        // extent report
        extentReportPath = getProperty(file, "extentReportPath");
        extentConfigPath = getProperty(file, "extentConfigPath");
        reportTitle      = getProperty(file, "reportTitle");
        reportName       = getProperty(file, "reportName");

        // database
        dbUrl      = getProperty(file, "dbUrl");
        dbUsername = getProperty(file, "dbUsername");
        dbPassword = getProperty(file, "dbPassword");
        dbDriver   = getProperty(file, "dbDriver");

        System.out.println("Framework config loaded successfully");
    }
}
