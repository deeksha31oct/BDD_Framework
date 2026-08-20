package Com.base;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

public class Api_base_test {
    @BeforeSuite
    public void setup()
    {
        RestAssured.baseURI = "https://hornback.in";
       System.out.println("Base URI set successfully");
    }
}
