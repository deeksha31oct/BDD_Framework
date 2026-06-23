package Com.base.utilites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class DayDepencyvariables {

    private static final Logger log = LogManager.getLogger(DayDepencyvariables.class);

    // in-memory store for the current execution
    private static final Properties dayProperties = new Properties();

    // date suffix format, e.g. _2026-06-22
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ===================================================================
    //  PLAIN GET / SET
    // ===================================================================

    // set a plain property
    public static void setProperty(String key, String value) {
        dayProperties.setProperty(key, value);
        log.info("setProperty: " + key + " = " + value);
    }

    // get a plain property (null if not present)
    public static String getPropertyFromDayDepency(String key) {
        String value = dayProperties.getProperty(key);
        log.info("getProperty: " + key + " = " + value);
        return value;
    }

    // ===================================================================
    //  GET / SET KEYED BY CURRENT EXECUTION DAY
    // ===================================================================

    // set a property whose key is auto-suffixed with today's date
    // e.g. setPropertyWithCurrentExecutionDay("orderId", "123")
    //      stores under key "orderId_2026-06-22"
    public static void setPropertyWithCurrentExecutionDay(String key, String value) {
        String dayKey = buildDayKey(key);
        dayProperties.setProperty(dayKey, value);
        log.info("setProperty(day): " + dayKey + " = " + value);
    }

    // get a property stored against today's date
    public static String getPropertyWithCurrentExecutionDay(String key) {
        String dayKey = buildDayKey(key);
        String value = dayProperties.getProperty(dayKey);
        log.info("getProperty(day): " + dayKey + " = " + value);
        return value;
    }

    // ===================================================================
    //  HELPER
    // ===================================================================

    // appends today's date to the key
    private static String buildDayKey(String key) {
        return key + "_" + LocalDate.now().format(DAY_FORMAT);
    }
}
//plain
//DayDepencyvariables.setProperty("env", "QA");
//DayDepencyvariables.getPropertyFromDayDepency("env");                  // "QA"
//
// keyed to today's execution date
//DayDepencyvariables.setPropertyWithCurrentExecutionDay("orderId", "123");
//DayDepencyvariables.getPropertyWithCurrentExecutionDay("orderId");     // "123" (only for today's run)
//