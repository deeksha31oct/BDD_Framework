package API_Logic.API_StepDefinations;

import API_Logic.API_Utility.API_TokenManager;
import API_Logic.API_Utility.ApiDataReader;
import Com.base.FunctionLibarary.JSONkeywords;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class API_stepdefination {

    private static final Logger log = LogManager.getLogger(API_stepdefination.class);

    private Response response;
    private RequestSpecification request;
    private String requestBody;

    // ===================================================================
    //  BUILD REQUEST
    // ===================================================================

    // create a fresh authenticated request (base URI + bearer token + content-type)
    @Given("user creates an authenticated request")
    public void user_creates_an_authenticated_request() {
        RestAssured.baseURI = API_TokenManager.getBaseUri();
        request = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", API_TokenManager.getBearerToken());
        log.info("Authenticated request created");
    }

    // create the request body by reading a payload from the data file
    // NOTE: signature is (fileName, key) - matches getPayload(fileName, key)
    @Given("user creates request body using payload {string} from {string}")
    public void user_creates_request_body(String key, String fileName) {
        requestBody = ApiDataReader.getPayload(fileName, key);
        log.info("Request body created from " + fileName + " [" + key + "]");
    }

    // override a field in the loaded body by JSON path (e.g. make data unique per run)
    @Given("user sets request field {string} to {string}")
    public void user_sets_request_field(String jsonPath, String value) {
        requestBody = JSONkeywords.updateJsonWithJsonPath_ifString(requestBody, jsonPath, value);
        log.info("Set " + jsonPath + " = " + value);
    }

    // ===================================================================
    //  SEND REQUEST
    // ===================================================================

    // POST with the prepared JSON body to an endpoint
    @When("user gives json request body to get response on {string}")
    public void user_gives_json_body_to_get_response(String endpoint) {
        ensureRequest();
        response = request.body(requestBody).post(endpoint);
        log.info("POST " + endpoint + " -> " + response.getStatusCode());
    }

    // simple GET on an endpoint
    @When("user calls the GET api {string}")
    public void user_calls_the_get_api(String endpoint) {
        ensureRequest();
        response = request.get(endpoint);
        log.info("GET " + endpoint + " -> " + response.getStatusCode());
    }

    // GET with an external reference + client number as path/query params
    // e.g. GET /api/Vendors/{externalRef}?clientNumber={clientNumber}
    @When("user calls the GET api with external reference {string} and client number {string}")
    public void user_calls_get_with_reference_and_client(String externalRef, String clientNumber) {
        ensureRequest();
        response = request
                .pathParam("externalRef", externalRef)
                .queryParam("clientNumber", clientNumber)
                .get("/api/Vendors/{externalRef}");
        log.info("GET with ref=" + externalRef + ", client=" + clientNumber
                + " -> " + response.getStatusCode());
    }

    // PUT (update) with the prepared body to an endpoint that has an id
    @When("user updates via PUT api {string} with id {string}")
    public void user_updates_via_put(String endpoint, String id) {
        ensureRequest();
        response = request.body(requestBody)
                .pathParam("id", id)
                .put(endpoint);   // endpoint should contain {id}, e.g. "/api/Vendors/{id}"
        log.info("PUT " + endpoint + " id=" + id + " -> " + response.getStatusCode());
    }

    // DELETE by id
    @When("user deletes via DELETE api {string} with id {string}")
    public void user_deletes_via_delete(String endpoint, String id) {
        ensureRequest();
        response = request.pathParam("id", id).delete(endpoint);
        log.info("DELETE " + endpoint + " id=" + id + " -> " + response.getStatusCode());
    }

    // ===================================================================
    //  VALIDATE RESPONSE
    // ===================================================================

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int expected) {
        Assert.assertEquals(response.getStatusCode(), expected,
                "Status code mismatch");
        log.info("Status code verified: " + expected);
    }

    @Then("the response field {string} should be {string}")
    public void the_response_field_should_be(String jsonPath, String expected) {
        String actual = response.jsonPath().getString(jsonPath);
        Assert.assertEquals(actual, expected,
                "Field " + jsonPath + " mismatch");
        log.info("Field verified: " + jsonPath + " = " + expected);
    }

    @Then("the response field {string} should not be null")
    public void the_response_field_should_not_be_null(String jsonPath) {
        Object actual = response.jsonPath().get(jsonPath);
        Assert.assertNotNull(actual, "Field " + jsonPath + " should not be null");
    }

    @Then("the response body should contain {string}")
    public void the_response_body_should_contain(String text) {
        Assert.assertTrue(response.getBody().asString().contains(text),
                "Response body should contain: " + text);
    }

    // print the response (handy while building tests)
    @Then("user prints the response")
    public void user_prints_the_response() {
        log.info("Response:\n" + response.getBody().prettyPrint());
    }

    // ===================================================================
    //  HELPER
    // ===================================================================

    // if no request was explicitly created, build a default authenticated one
    private void ensureRequest() {
        if (request == null) user_creates_an_authenticated_request();
    }
}
