package com.br.jirautility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class Jirautility {

    // ---- Jira config (move these to a config file in real use) ----
    private static final String JIRA_URL  = "https://your-domain.atlassian.net";
    private static final String EMAIL     = "you@example.com";
    private static final String API_TOKEN = "your-jira-api-token";

    // build the Basic auth header (email:token -> Base64)
    private static String getAuthHeader() {
        String auth = EMAIL + ":" + API_TOKEN;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    // 1. create a defect/bug in Jira when a test fails
    public String createDefect(String projectKey, String summary, String description) {
        String response = null;
        try {
            URL url = new URL(JIRA_URL + "/rest/api/2/issue");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", getAuthHeader());
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "{ \"fields\": {"
                    + "\"project\": { \"key\": \"" + projectKey + "\" },"
                    + "\"summary\": \"" + summary + "\","
                    + "\"description\": \"" + description + "\","
                    + "\"issuetype\": { \"name\": \"Bug\" } } }";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
            }

            response = readResponse(conn);
            System.out.println("Defect created in Jira: " + response);

        } catch (Exception e) {
            System.out.println("createDefect failed: " + e.getMessage());
        }
        return response;
    }

    // 2. get details of an existing issue
    public String getIssueDetails(String issueKey) {
        String response = null;
        try {
            URL url = new URL(JIRA_URL + "/rest/api/2/issue/" + issueKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", getAuthHeader());
            conn.setRequestProperty("Content-Type", "application/json");
            response = readResponse(conn);
        } catch (Exception e) {
            System.out.println("getIssueDetails failed: " + e.getMessage());
        }
        return response;
    }

    // 3. import test cases (called in TestRunner_JiraImportExport @BeforeClass)
    public void importTestCasesFromJira() {
        try {
            System.out.println("Importing test cases from Jira...");
            // e.g. GET test cases via Jira / Xray / Zephyr API, then parse into your data
        } catch (Exception e) {
            System.out.println("importTestCasesFromJira failed: " + e.getMessage());
        }
    }

    // 4. export results back to Jira (called in @AfterClass)
    public void exportResultsToJira() {
        try {
            System.out.println("Exporting results to Jira...");
            // e.g. POST execution status (pass/fail) per test via Jira / Xray / Zephyr API
        } catch (Exception e) {
            System.out.println("exportResultsToJira failed: " + e.getMessage());
        }
    }

    // helper - read the HTTP response body
    private String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}