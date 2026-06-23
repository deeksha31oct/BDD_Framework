package Com.base.FunctionLibarary;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * restassuredKeywords - REST API testing keyword library (Rest Assured).
 * Typical flow:
 *   createConnection(baseUri);
 *   setRequestSpecification();
 *   selectHeaderParameter("Authorization", "Bearer xxx");
 *   selectJsonPayload(body);
 *   Response r = getResponseWithMethod("POST", "/users");
 *   getResponseBody(r);
 */
public class restassuredKeywords {

    private static final Logger log = LogManager.getLogger(restassuredKeywords.class);

    private static RequestSpecification request;

    // ===================================================================
    //  CONNECTION / SPEC
    // ===================================================================

    // set the base URI and start a fresh request spec
    public static void createConnection(String baseUri) {
        RestAssured.baseURI = baseUri;
        request = RestAssured.given();
        log.info("Connection created -> baseURI = " + baseUri);
    }

    // (re)initialise the request specification
    public static RequestSpecification setRequestSpecification() {
        request = RestAssured.given();
        request.header("Content-Type", "application/json");
        return request;
    }

    // get the current request specification (creates one if null)
    public static RequestSpecification getRequestSpecification() {
        if (request == null) setRequestSpecification();
        return request;
    }

    // ===================================================================
    //  PARAMETERS / HEADERS / PAYLOAD
    // ===================================================================

    // add a query parameter (e.g. ?status=active)
    public static void selectQueryParameter(String key, String value) {
        getRequestSpecification().queryParam(key, value);
        log.info("Query param: " + key + " = " + value);
    }

    // add a header (e.g. Authorization token)
    public static void selectHeaderParameter(String key, String value) {
        getRequestSpecification().header(key, value);
        log.info("Header: " + key + " = " + value);
    }

    // set the JSON request body / payload
    public static void selectJsonPayload(String jsonBody) {
        getRequestSpecification().body(jsonBody);
        log.info("JSON payload set");
    }

    // set multiple JSON test-data parameters as form/body params from a Map
    public static void setJsonTestDataParameters(Map<String, ?> params) {
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            getRequestSpecification().formParam(entry.getKey(), entry.getValue());
        }
        log.info("Set " + params.size() + " JSON test-data parameters");
    }

    // read JSON input from a text file and set it as the body
    public static void setJsonInputDataForTextFile(String filePath) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            getRequestSpecification().body(json);
            log.info("JSON body loaded from file: " + filePath);
        } catch (Exception e) {
            log.error("setJsonInputDataForTextFile failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  EXECUTE REQUEST
    // ===================================================================

    // execute a request by method name (GET / POST / PUT / DELETE / PATCH) against an endpoint
    public static Response getResponseWithMethod(String method, String endpoint) {
        Response response;
        switch (method.toUpperCase()) {
            case "GET":    response = getRequestSpecification().get(endpoint);    break;
            case "POST":   response = getRequestSpecification().post(endpoint);   break;
            case "PUT":    response = getRequestSpecification().put(endpoint);    break;
            case "DELETE": response = getRequestSpecification().delete(endpoint); break;
            case "PATCH":  response = getRequestSpecification().patch(endpoint);  break;
            default:
                log.error("Unsupported method: " + method);
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        log.info(method + " " + endpoint + " -> status " + response.getStatusCode());
        return response;
    }

    // same as above but does NOT validate/check status (just returns whatever comes back)
    public static Response getResponseWithMethodWithoutStatusCheck(String method, String endpoint) {
        return getResponseWithMethod(method, endpoint);
    }

    // get the raw REST response object (alias kept for naming)
    public static Response getRestResponse(String method, String endpoint) {
        return getResponseWithMethod(method, endpoint);
    }

    // ===================================================================
    //  RESPONSE HANDLING
    // ===================================================================

    // get the response body as a String
    public static String getResponseBody(Response response) {
        String body = response.getBody().asString();
        log.info("Response body length: " + body.length());
        return body;
    }

    // convert/return the response body as pretty (indented) JSON
    public static String toPrettyJsonFormat(Response response) {
        String pretty = response.getBody().prettyPrint();
        return pretty;
    }

    // pretty-print any JSON string (just logs and returns it formatted via Response is not available,
    // so we use the response's prettyPrint where possible; for a raw string we return as-is)
    public static String jsonPrettyPrint(Response response) {
        return response.getBody().prettyPrint();
    }
}