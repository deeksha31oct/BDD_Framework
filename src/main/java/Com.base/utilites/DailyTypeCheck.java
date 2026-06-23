package Com.base.utilites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DailyTypeCheck {

    private static final Logger log = LogManager.getLogger(DailyTypeCheck.class);

    // boolean: just confirm it is true
    public static boolean checkGivenValue(boolean value) {
        log.info("Boolean check: " + value);
        return value;
    }

    // String: valid only if not null and not blank
    public static boolean checkGivenValue(String value) {
        boolean valid = value != null && !value.trim().isEmpty();
        log.info("String check: '" + value + "' -> " + valid);
        return valid;
    }

    // double: valid only if not NaN / not infinite
    public static boolean checkGivenValue(double value) {
        boolean valid = !Double.isNaN(value) && !Double.isInfinite(value);
        log.info("double check: " + value + " -> " + valid);
        return valid;
    }

    // int: valid (always a usable int) - logs and returns true
    public static boolean checkGivenValue(int value) {
        log.info("int check: " + value);
        return true;
    }
}