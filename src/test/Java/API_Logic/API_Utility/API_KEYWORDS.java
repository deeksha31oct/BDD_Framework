package API_Logic.API_Utility;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * API_KEYWORDS - API-layer keyword wrapper.
 * Token-aware: pulls base URI + bearer token from API_TokenManager so step
 * definitions don't have to set them every time.
 */
public class API_KEYWORDS {

    private static final Logger log = LogManager.getLogger(API_KEYWORDS.class);

    private static RequestSpecification request;

    // ===================================================================
    //  CONNECTION / SPEC
    // ===================================================================

    // create a fresh request using the base URI from API_TokenManager
    public static void createConnection() {
        RestAssured.baseURI = API_TokenManager.getBaseUri();
        request = RestAssured.given().header("Content-Type", "application/json");
        log.info("Connection created -> " + API_TokenManager.getBaseUri());
    }

    // create a fresh AUTHENTICATED request (adds the bearer token)
    public static void createAuthenticatedConnection() {
        RestAssured.baseURI = API_TokenManager.getBaseUri();
        request = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", API_TokenManager.getBearerToken());
        log.info("Authenticated connection created");
    }

    // get the current request specification (creates a plain one if null)
    public static RequestSpecification getRequestSpecification() {
        if (request == null) createConnection();
        return request;
    }

    // ===================================================================
    //  PARAMETERS / HEADERS
    // ===================================================================

    public static void setQueryParameter(String key, String value) {
        getRequestSpecification().queryParam(key, value);
        log.info("Query param: " + key + " = " + value);
    }

    public static void setHeaderParameter(String key, String value) {
        getRequestSpecification().header(key, value);
        log.info("Header: " + key + " = " + value);
    }

    // ===================================================================
    //  EXECUTE
    // ===================================================================

    // execute by method name (GET/POST/PUT/DELETE/PATCH) with optional body
    public static Response getResponseWithMethod(String method, String endpoint, String body) {
        RequestSpecification spec = getRequestSpecification();
        if (body != null && !body.isEmpty()) spec.body(body);

        Response response;
        switch (method.toUpperCase()) {
            case "GET":    response = spec.get(endpoint);    break;
            case "POST":   response = spec.post(endpoint);   break;
            case "PUT":    response = spec.put(endpoint);    break;
            case "DELETE": response = spec.delete(endpoint); break;
            case "PATCH":  response = spec.patch(endpoint);  break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        log.info(method + " " + endpoint + " -> status " + response.getStatusCode());
        request = null;   // reset so next call starts a clean spec
        return response;
    }

    // same but does NOT validate status (alias kept for your naming)
    public static Response getResponseWithMethodRequestWithoutStatusCheck(String method,
                                                                          String endpoint, String body) {
        return getResponseWithMethod(method, endpoint, body);
    }

    // get the raw REST response (alias)
    public static Response getRestResponse(String method, String endpoint, String body) {
        return getResponseWithMethod(method, endpoint, body);
    }

    // ===================================================================
    //  RESPONSE FORMATTING
    // ===================================================================

    public static String toPrettyJsonFormat(Response response) {
        return response.getBody().prettyPrint();
    }

    public static String jsonPrettyPrint(Response response) {
        return response.getBody().prettyPrint();
    }
}