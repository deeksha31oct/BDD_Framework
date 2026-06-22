@UI_login_OrangeHRM
Feature: OrangeHRM Login

@T_1 @LoginE2ESUITE @TCid_001
  Scenario Outline: Successful login with valid credentials
    Given Browser Selection is "Chrome"
     And read data '<sheet>' from sheet
    Given user is on the OrangeHRM login page
    When user enters username "Admin" and password "admin123"
    And user clicks the login button
    Then user should see the dashboard
  Examples:
    |sheet      | Scenario_UI                                |Success_msg                      |
    |valid_login| Successful login with valid credentials    |your request has been submitted  |

  @T_2 @LoginE2ESUITE @TCid_002
  Scenario Outline: Login fails with invalid credentials
    Given Browser Selection is "Chrome"
    And read data '<sheet>' from sheet
    Given user is on the OrangeHRM login page
    When user enters username "Admin" and password "wrongpass"
    And user clicks the login button
    Then user should see an "Invalid credentials" error
    Examples:
      |sheet        | Scenario_UI                                |Success_msg                      |
      |invalid_login| Login fails with invalid credentials       |your request has been submitted  |