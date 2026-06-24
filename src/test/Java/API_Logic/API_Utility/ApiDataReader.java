package API_Logic.API_Utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiDataReader {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String DATA_PATH =
            System.getProperty("user.dir") + "/FeatureFiles/APL_HORNBACK_AUTOMATE/Data_API/";

    // pull one named payload out of the multi-payload file, return it as a JSON string
    public static String getPayload(String fileName, String key) {
        try {
            JsonNode root = mapper.readTree(new java.io.File(DATA_PATH + fileName));
            JsonNode payload = root.get(key);
            if (payload == null) throw new RuntimeException("Key not found: " + key);
            return mapper.writeValueAsString(payload);   // node -> JSON string
        } catch (Exception e) {
            throw new RuntimeException("getPayload failed: " + e.getMessage());
        }
    }
}
