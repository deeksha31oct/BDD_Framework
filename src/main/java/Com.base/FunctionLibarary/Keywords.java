package Com.base.FunctionLibarary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Keywords - general-purpose utility keyword library
 * (strings, dates, files, encryption, random data, robot keys, process execution).
 */
public class Keywords {

    private static final Logger log = LogManager.getLogger(Keywords.class);
    private static final Random RANDOM = new Random();

    // ===================================================================
    //  DICTIONARY / MAP
    // ===================================================================

    public static void clearDictionaryObject(Map<?, ?> map) {
        if (map != null) {
            map.clear();
            log.info("Dictionary/map cleared");
        }
    }

    // ===================================================================
    //  STRING CONVERSIONS
    // ===================================================================

    // trim + null-safe convert to a clean string
    public static String convertStringData(Object data) {
        return data == null ? "" : data.toString().trim();
    }

    public static String convertUpperCase(String input) {
        return input == null ? "" : input.toUpperCase();
    }

    // "hello world" -> "Hello world"  (first letter capital, rest as-is)
    public static String strConvertToSentenceCase(String input) {
        if (input == null || input.isEmpty()) return input;
        return Character.toUpperCase(input.charAt(0)) + input.substring(1).toLowerCase();
    }

    // get a substring char at a given index (safe)
    public static String getStringByIndex(String input, int index) {
        if (input == null || index < 0 || index >= input.length()) return "";
        return String.valueOf(input.charAt(index));
    }

    // ===================================================================
    //  DATE CONVERSIONS
    // ===================================================================

    // parse a string into a Date using the given format, e.g. "yyyy-MM-dd"
    public static Date convertStringDate(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (Exception e) {
            log.error("convertStringDate failed: " + e.getMessage());
            return null;
        }
    }

    // shift current time by +/- minutes, return formatted string
    public static String modifyTimeWithMinutes(int minutes, String format) {
        LocalDateTime time = LocalDateTime.now().plusMinutes(minutes);
        return time.format(DateTimeFormatter.ofPattern(format));
    }

    // ===================================================================
    //  RANDOM / UUID
    // ===================================================================

    // random number of given digit-length
    public static String generateRandomNumber(int digits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits; i++) sb.append(RANDOM.nextInt(10));
        return sb.toString();
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    // ===================================================================
    //  ENCRYPTION
    // ===================================================================

    // decrypt a Base64-encoded password (simple obfuscation, not strong crypto)
    public static String decryptedPassword(String encrypted) {
        try {
            return new String(Base64.getDecoder().decode(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decryptedPassword failed: " + e.getMessage());
            return null;
        }
    }

    // ===================================================================
    //  FILES
    // ===================================================================

    public static void copyFile(String sourcePath, String destPath) {
        try {
            Files.copy(Paths.get(sourcePath), Paths.get(destPath),
                    StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied " + sourcePath + " -> " + destPath);
        } catch (Exception e) {
            log.error("copyFile failed: " + e.getMessage());
        }
    }

    // update a field in an excel file - delegates to your POI excel manager
    public static void updateExcelField(String filePath, String sheet, String column,
                                        String value, int rowIndex) {
        // delegate to your existing ExcelMangerPOI/Fillo updateExcelField
        log.info("updateExcelField " + filePath + " [" + sheet + "] " + column
                + "=" + value + " row " + rowIndex);
        // Com.base.ExcelFuntions.ExcelMangerPOI.updateExcelField(filePath, sheet, column, value, rowIndex);
    }

    // ===================================================================
    //  STREAMS
    // ===================================================================

    public static String convertInputStreamToString(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append(System.lineSeparator());
            return sb.toString();
        } catch (Exception e) {
            log.error("convertInputStreamToString failed: " + e.getMessage());
            return "";
        }
    }

    // ===================================================================
    //  PROCESS / BATCH EXECUTION
    // ===================================================================

    // run an external process and return its console output
    public static String runProcess(String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String output = convertInputStreamToString(process.getInputStream());
            process.waitFor();
            return output;
        } catch (Exception e) {
            log.error("runProcess failed: " + e.getMessage());
            return "";
        }
    }

    // run a .bat file
    public static String runBatchFile(String batFilePath) {
        return runProcess("cmd.exe", "/c", batFilePath);
    }

    // ===================================================================
    //  WAIT
    // ===================================================================

    public static void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("wait interrupted: " + e.getMessage());
        }
    }

    // ===================================================================
    //  ROBOT KEYBOARD ACTIONS
    // ===================================================================

    // type a full string char-by-char using Robot (OS-level keystrokes)
    public static void enterTextWithRobotKeys(String text) {
        try {
            Robot robot = new Robot();
            for (char c : text.toCharArray()) {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                boolean upper = Character.isUpperCase(c);
                if (upper) robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                if (upper) robot.keyRelease(KeyEvent.VK_SHIFT);
            }
            log.info("Robot typed: " + text);
        } catch (Exception e) {
            log.error("enterTextWithRobotKeys failed: " + e.getMessage());
        }
    }

    // press a single key via Robot, e.g. enterWithRobotKeys(KeyEvent.VK_ENTER)
    public static void enterWithRobotKeys(int keyCode) {
        try {
            Robot robot = new Robot();
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            log.info("Robot pressed key: " + keyCode);
        } catch (Exception e) {
            log.error("enterWithRobotKeys failed: " + e.getMessage());
        }
    }

    // press multiple keys together, e.g. CTRL+ALT+DEL
    // enterMultipleRobotKeys(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_DELETE)
    public static void enterMultipleRobotKeys(int... keyCodes) {
        try {
            Robot robot = new Robot();
            for (int key : keyCodes) robot.keyPress(key);
            for (int i = keyCodes.length - 1; i >= 0; i--) robot.keyRelease(keyCodes[i]);
            log.info("Robot pressed multiple keys");
        } catch (Exception e) {
            log.error("enterMultipleRobotKeys failed: " + e.getMessage());
        }
    }
}
