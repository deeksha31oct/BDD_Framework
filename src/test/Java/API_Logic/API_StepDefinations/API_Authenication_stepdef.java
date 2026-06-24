package API_Logic.API_StepDefinations;

import API_Logic.API_Utility.API_TokenManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class API_Authenication_stepdef {

    private static final Logger log = LogManager.getLogger(API_Authenication_stepdef.class);
    private Response response;
    private RequestSpecification request;

    @Given("the API base URI is set")
    public void the_api_base_uri_is_set() {
        RestAssured.baseURI = API_TokenManager.getBaseUri();
        request = RestAssured.given().header("Content-Type", "application/json");
        log.info("Base URI set: " + API_TokenManager.getBaseUri());
    }

    @When("user logs in with username {string} and password {string}")
    public void user_logs_in_with_username_and_password(String username, String password) {
        // ===== CONFIRM these keys match your Swagger login body =====
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        response = request.body(body).post("/api/Auth/login");
        log.info("Login status: " + response.getStatusCode());
    }

    @Then("the login is successful and access token is saved")
    public void the_login_is_successful_and_token_saved() {
        Assert.assertEquals(response.getStatusCode(), 200, "Login should return 200");

        // ===== CONFIRM this path matches your response JSON =====
        String token = response.jsonPath().getString("token");

        Assert.assertNotNull(token, "Token should not be null");
        API_TokenManager.setAccessToken(token);
        log.info("Access token saved: " + token.substring(0, Math.min(token.length(), 15)) + "...");
    }
}