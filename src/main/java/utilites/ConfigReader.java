package utilites;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read configuration from config.properties file
 */
public class ConfigReader {

    private static Properties properties;

    static {
        try {
            InputStream input = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            properties = new Properties();
            if (input != null) {
                properties.load(input);
                input.close();
            } else {
                throw new IOException("config.properties file not found in classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage());
        }
    }

    /**
     * Get a property value by key with default fallback
     * @param key property key
     * @param defaultValue default value if key not found
     * @return property value or default
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get a property value by key
     * @param key property key
     * @return property value or null if not found
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a property as boolean
     * @param key property key
     * @return parsed boolean value
     */
    public static boolean getBoolean(String key) {
        String value = getProperty(key, "false");
        return Boolean.parseBoolean(value);
    }

    /**
     * Get a property as integer
     * @param key property key
     * @return parsed integer value
     */
    public static int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

