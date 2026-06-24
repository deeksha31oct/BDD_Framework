package API_Logic.API_Utility;

public class API_TokenManager {

    public static String accessToken;
    public static String baseUri = "https://concur-dandy-crabmeat.ngrok-free.dev";

    public  static void setAccessToken(String token) { accessToken = token; }
    public String getAccessToken() { return accessToken; }

    public static String getBaseUri() { return baseUri; }
    public  void setBaseUri(String uri) { baseUri = uri; }

    public static String getBearerToken() { return "Bearer " + accessToken; }
}