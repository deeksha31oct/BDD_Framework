package Com.base.FunctionLibarary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONschemaKeywords {

    private static final ObjectMapper mapper = new ObjectMapper();

    // JSON string -> JsonNode tree
    public static JsonNode outputAsString(String json) throws Exception {
        return mapper.readTree(json);
    }

    // JSON string -> POJO of given class
    public static <T> T outputAsPojo(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    // convert a JsonNode back to a string
    public static String convertNodeToString(JsonNode node) throws Exception {
        return mapper.writeValueAsString(node);
    }

    // create an empty schema/object node to build into
    public static ObjectNode schemaNode() {
        return mapper.createObjectNode();
    }

    // getBs - ??? need you to clarify

    // cleanup - reset/clear (clarify what state needs clearing)
    public static void cleanup() {
        // ...
    }
}