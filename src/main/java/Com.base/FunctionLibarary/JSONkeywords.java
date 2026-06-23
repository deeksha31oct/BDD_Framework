package Com.base.FunctionLibarary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSONkeywords - JSON read / update / validate keyword library.
 * Uses Jayway JsonPath for path operations, Jackson for tree + XML->JSON + schema.
 */
public class JSONkeywords {

    private static final Logger log = LogManager.getLogger(JSONkeywords.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    // ===================================================================
    //  CONVERT / READ
    // ===================================================================

    // convert a JSON string into an org.json JSONObject
    public static JSONObject json_convertStringToJsonObject(String json) {
        return new JSONObject(json);
    }

    // read a JSON file from disk into a string
    public static String json_readFileToString(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            log.error("json_readFileToString failed: " + e.getMessage());
            return null;
        }
    }

    // pretty-print a JSON string
    public static String prettyJsonFormat(String json) {
        try {
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("prettyJsonFormat failed: " + e.getMessage());
            return json;
        }
    }

    // convert an XML string to JSON
    public static String convertXmlToJson(String xml) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(xml.getBytes());
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            log.error("convertXmlToJson failed: " + e.getMessage());
            return null;
        }
    }

    // ===================================================================
    //  GET PROPERTY VALUE  (org.json - simple top-level)
    // ===================================================================

    public static Object json_getPropertyValue(String json, String key) {
        JSONObject obj = new JSONObject(json);
        return obj.has(key) ? obj.get(key) : null;
    }

    // update a top-level property on a json string, returns new json string
    public static String json_updatePropertyValue(String json, String key, Object value) {
        JSONObject obj = new JSONObject(json);
        obj.put(key, value);
        return obj.toString();
    }

    // update a property directly on a JSONObject (mutates and returns it)
    public static JSONObject json_updatePropertyValueInJsonObj(JSONObject obj, String key, Object value) {
        obj.put(key, value);
        return obj;
    }

    // ===================================================================
    //  JSONPATH READ
    // ===================================================================

    // read a value at a json path, e.g. "$.data.email"
    public static <T> T json_readPropertyValueWithJsonPath(String json, String jsonPath) {
        try {
            return JsonPath.read(json, jsonPath);
        } catch (Exception e) {
            log.error("json_readPropertyValueWithJsonPath failed: " + e.getMessage());
            return null;
        }
    }

    // read a value, return "" if null/missing instead of throwing
    public static String json_readPropertyWithJsonPath_nullAsEmpty(String json, String jsonPath) {
        try {
            Object value = JsonPath.read(json, jsonPath);
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // check whether a json path exists and whether it is a string or array
    // returns "string" / "array" / "other" / "missing"
    public static String json_checkGivenJsonPath_stringOrArray(String json, String jsonPath) {
        try {
            Object value = JsonPath.read(json, jsonPath);
            if (value == null) return "missing";
            if (value instanceof List) return "array";
            if (value instanceof String) return "string";
            return "other";
        } catch (Exception e) {
            return "missing";
        }
    }

    // get a json array at a path as a List
    public static List<Object> getJsonArrayWithPath(String json, String jsonPath) {
        try {
            return JsonPath.read(json, jsonPath);
        } catch (Exception e) {
            log.error("getJsonArrayWithPath failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // get a json array of objects at a path as List<HashMap>
    public static List<HashMap<String, Object>> getJsonArrayWithPathSupportHashMap(String json, String jsonPath) {
        try {
            return JsonPath.read(json, jsonPath);
        } catch (Exception e) {
            log.error("getJsonArrayWithPathSupportHashMap failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // read a value from a specific array element, "" if null/empty
    public static String json_readPropertyValueFromJsonArray_nullOrEmpty(String json, String jsonPath) {
        try {
            Object value = JsonPath.read(json, jsonPath);
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // ===================================================================
    //  JSONPATH UPDATE (typed)
    // ===================================================================

    // update a path with a String value
    public static String updateJsonWithJsonPath_ifString(String json, String jsonPath, String value) {
        DocumentContext ctx = JsonPath.parse(json);
        ctx.set(jsonPath, value);
        return ctx.jsonString();
    }

    // update a path with a boolean value
    public static String updateJsonWithJsonPath_ifBoolean(String json, String jsonPath, boolean value) {
        DocumentContext ctx = JsonPath.parse(json);
        ctx.set(jsonPath, value);
        return ctx.jsonString();
    }

    // update a path with a numeric value
    public static String updateJsonWithJsonPath_ifNumber(String json, String jsonPath, Number value) {
        DocumentContext ctx = JsonPath.parse(json);
        ctx.set(jsonPath, value);
        return ctx.jsonString();
    }

    // update a path with an object value (e.g. replace a whole node)
    public static String updateJsonArrayWithJsonPathObject(String json, String jsonPath, Object value) {
        DocumentContext ctx = JsonPath.parse(json);
        ctx.set(jsonPath, value);
        return ctx.jsonString();
    }

    // delete a json node / array element at a path
    public static String deleteJsonArrayOrJsonNode_withJsonPath(String json, String jsonPath) {
        DocumentContext ctx = JsonPath.parse(json);
        ctx.delete(jsonPath);
        return ctx.jsonString();
    }

    // ===================================================================
    //  VERIFY
    // ===================================================================

    // verify a JSONObject is available (not null and not empty)
    public static boolean verifyJsonObjectIsAvailable(JSONObject obj) {
        boolean available = obj != null && obj.length() > 0;
        log.info("verifyJsonObjectIsAvailable -> " + available);
        return available;
    }

    // validate a json string against a json schema string. returns true if valid.
    public static boolean verifyJsonSchema(String json, String schema) {
        return consumeSchemaErrorToList(json, schema).isEmpty();
    }

    // validate an API response body against a schema file on disk
    public static boolean verifyJsonSchemaWithApiResponse(String responseBody, String schemaFilePath) {
        String schema = json_readFileToString(schemaFilePath);
        return verifyJsonSchema(responseBody, schema);
    }

    // run schema validation and return the list of error messages (empty = valid)
    public static List<String> consumeSchemaErrorToList(String json, String schema) {
        List<String> errors = new ArrayList<>();
        try {
            JsonSchemaFactory factory =
                    JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema jsonSchema = factory.getSchema(schema);
            JsonNode node = mapper.readTree(json);
            Set<ValidationMessage> messages = jsonSchema.validate(node);
            for (ValidationMessage msg : messages) errors.add(msg.getMessage());
            log.info("Schema validation errors: " + errors.size());
        } catch (Exception e) {
            log.error("consumeSchemaErrorToList failed: " + e.getMessage());
            errors.add(e.getMessage());
        }
        return errors;
    }
}

//note
//String email = JSONkeywords.json_readPropertyValueWithJsonPath(body, "$.data.email");
//String updated = JSONkeywords.updateJsonWithJsonPath_ifString(body, "$.name", "Raj");
//String removed = JSONkeywords.deleteJsonArrayOrJsonNode_withJsonPath(body, "$.items[0]");
//List<Object> items = JSONkeywords.getJsonArrayWithPath(body, "$.items");
//
// schema validation against your saved schema file
//boolean valid = JSONkeywords.verifyJsonSchemaWithApiResponse(response, "src/test/resources/schemas/user.json");
//List<String> errors = JSONkeywords.consumeSchemaErrorToList(response, schemaString);
//