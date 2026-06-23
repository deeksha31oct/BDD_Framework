package Com.base.utilites;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DateUtility {

    private static final Logger log = LogManager.getLogger(DateUtility.class);

    // default display format - change if your app uses a different one
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMAT_WITH_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ===================================================================
    //  TODAY
    // ===================================================================

    // today's date as String (yyyy-MM-dd)
    public static String getTodayDate() {
        return LocalDate.now().format(FORMAT);
    }

    // today's date+time in a specific time zone, e.g. "Asia/Kolkata"
    public static String getTodayDateWithTimeZone(String zoneId) {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of(zoneId));
        return zdt.format(FORMAT_WITH_TIME) + " (" + zoneId + ")";
    }

    // ===================================================================
    //  PLUS / MINUS DAYS
    // ===================================================================

    // shift today by +days (future) or -days (past). e.g. +7, -3
    public static String generateDateWithPlusMinusDays(int days) {
        return LocalDate.now().plusDays(days).format(FORMAT);
    }

    // same but shift from any given base date
    public static String generateDateWithPlusMinusDays(LocalDate baseDate, int days) {
        return baseDate.plusDays(days).format(FORMAT);
    }

    // ===================================================================
    //  MONTH
    // ===================================================================

    // current month name, e.g. "JUNE"
    public static String getMonthFromCurrentMonth() {
        return LocalDate.now().getMonth().toString();
    }

    // first day of the current month
    public static String getStartDateOfMonth() {
        LocalDate now = LocalDate.now();
        return now.withDayOfMonth(1).format(FORMAT);
    }

    // last day of the current month
    public static String getEndDateOfMonth() {
        LocalDate now = LocalDate.now();
        return now.withDayOfMonth(now.lengthOfMonth()).format(FORMAT);
    }

    // ===================================================================
    //  LOCALDATE OBJECT
    // ===================================================================

    // return a LocalDate object (not String) - useful for further date math
    public static LocalDate getDateWithObjectLocalDate(int plusMinusDays) {
        return LocalDate.now().plusDays(plusMinusDays);
    }

    // ===================================================================
    //  DATE EXCLUDING HOLIDAYS / WEEKENDS
    // ===================================================================

    // get a future date 'workingDays' ahead, skipping weekends AND given holidays.
    // holidays = list of "yyyy-MM-dd" strings to skip.
    public static String getDateWithExcludingHoliday(int workingDays, List<String> holidays) {
        LocalDate date = LocalDate.now();
        int added = 0;
        while (added < workingDays) {
            date = date.plusDays(1);
            if (isWorkingDay(date, holidays)) {
                added++;
            }
        }
        return date.format(FORMAT);
    }

    // helper: a working day is NOT a weekend and NOT a holiday
    private static boolean isWorkingDay(LocalDate date, List<String> holidays) {
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return false;
        }
        if (holidays != null && holidays.contains(date.format(FORMAT))) {
            return false;
        }
        return true;
    }
}

// ===================================================================
//  DateUtility.getTodayDate();                            // 2026-06-22
//DateUtility.getTodayDateWithTimeZone("Asia/Kolkata");  // 2026-06-22 22:15:30 (Asia/Kolkata)
//DateUtility.generateDateWithPlusMinusDays(7);          // 7 days from today
//DateUtility.generateDateWithPlusMinusDays(-3);         // 3 days ago
//DateUtility.getMonthFromCurrentMonth();                // JUNE
//DateUtility.getStartDateOfMonth();                     // 2026-06-01
//DateUtility.getEndDateOfMonth();                       // 2026-06-30
//DateUtility.getDateWithObjectLocalDate(5);             // LocalDate object, +5 days
//
//List<String> holidays = List.of("2026-06-25", "2026-06-26");
//DateUtility.getDateWithExcludingHoliday(5, holidays);  // 5 working days ahead, skipping weekends + those dates
//