Feature: Hornback IMS - Factory Expenses Labour

  Scenario: View labour entries for a selected month
    Given user is on the Factory Expenses page
    When user selects "Jun 2026" from the view month dropdown
    And user clicks on the "Labour" tab
    Then the labour entries table should be displayed
    And the table should contain category "Permanent salary"

  Scenario: Open the add labour entry option
    Given user is on the Factory Expenses page
    When user clicks on the "Add labour entry" button
    Then the add labour entry form should be displayed