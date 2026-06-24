package API_Logic.API_StepDefinations;

import API_Logic.API_Utility.API_TokenManager;
import API_Logic.FunctionLibarary.restassuredKeywords;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class API_TOKEN {

    private static final Logger log = LogManager.getLogger(API_TOKEN.class);
    private Response response;

    @When("user logs in to get a bearer token using {string} from {string}")
    public void user_logs_in(String key, String fileName) {
        restassuredKeywords.createConnection(API_TokenManager.getBaseUri());
        restassuredKeywords.setRequestSpecification();
        restassuredKeywords.selectHeaderParameter("Content-Type", "application/json");

        String requestBody = API_Logic.API_Utility.ApiDataReader.getPayload(fileName, key);
        restassuredKeywords.selectJsonPayload(requestBody);

        response = restassuredKeywords.getResponseWithMethod("POST", "/api/Auth/login_1");
        log.info("Login status: " + response.getStatusCode());
    }

    @Then("the login is successful and access bearer token is saved")
    public void the_login_is_successful_and_token_saved() {
        Assert.assertEquals(response.getStatusCode(), 200, "Login should return 200");

        // ===== CONFIRM this path matches your response JSON =====
        String token = response.jsonPath().getString("token");

        Assert.assertNotNull(token, "Token should not be null");
        API_TokenManager.setAccessToken(token);
        log.info("Access token saved: " + token.substring(0, Math.min(token.length(), 14)) + "...");
    }
}