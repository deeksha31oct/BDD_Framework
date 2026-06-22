@UI_Navigation_OrangeHRM
Feature: OrangeHRM Navigation

  @T_1 @NavigationE2ESUITE @TCid_0011
  Scenario Outline:TC_11 :: :: Navigate to PIM module
    Given Browser Selection is "Chrome"
    And read data '<sheet>' from sheet
    Given user is logged into OrangeHRM
    When user clicks on "PIM" menu
    Then user should see the "PIM" page
    Examples:
      |sheet      | Scenario_UI                                |Success_msg                      |
      |valid_login| Navigate to PIM module                     |your request has been submitted  |