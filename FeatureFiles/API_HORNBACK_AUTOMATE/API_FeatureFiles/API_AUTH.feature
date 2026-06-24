@API_AUTOMATION
Feature: Hornback IMS API Authentication
  @APi_TEST @AP_01
  Scenario: Login with valid credentials and save token
    Given the API base URI is set
    Then the login is successful and access bearer token is saved
    Then the login is successful and access token is saved