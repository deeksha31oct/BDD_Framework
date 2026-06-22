package com.br.testglobalvariable;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import java.util.HashMap;
import java.util.Map;

public class GlobalVariables {

    // holds all key-value test data read from Excel
    public static Map<String, String> data = new HashMap<>();

    private static String excelPath;                 // Excel file path
    public static boolean PRINT_TO_REPORT = true;    // toggle printing on/off

    // commonly reused variables
    public static String BASE_URL;
    public static String USERNAME;
    public static String PASSWORD;
    public static String ID;
    public static String EMAIL;
    public static String DOB;
    public static String MOBILE;
    public static String COUNTRY;

    // ---------- STEP 1: set the Excel path ----------
    public static void setExcelPath(String path) {
        excelPath = path;
        System.out.println("Excel path set to: " + excelPath);
    }

    // ---------- STEP 2 + 3: read Excel data and store into global variables ----------
    public static void readAndStoreData(String sheetName) {
        Connection connection = null;
        Recordset recordset = null;
        try {
            Fillo fillo = new Fillo();
            connection = fillo.getConnection(excelPath);     // uses path from Step 1
            recordset = connection.executeQuery("SELECT * FROM " + sheetName);

            // STEP 2: read every Key/Value row into the map
            while (recordset.next()) {
                String key = recordset.getField("Key");
                String value = recordset.getField("Value");
                data.put(key, value);
            }

            // STEP 3: put Excel values into the global variables
            // GlobalVariable.username = Test_GlobalVariable.completedTestData.get(0).get("username");
            // GlobalVariables.USERNAME = TextVariable.completedTestData.get(0).get("USERNAME");
            BASE_URL = Text_GlobalVariable.completedTestData.get(0).get("BASE_URL");
            USERNAME = Text_GlobalVariable.completedTestData.get(0).get("USERNAME");
            PASSWORD = Text_GlobalVariable.completedTestData.get(0).get("PASSWORD");
            ID       = Text_GlobalVariable.completedTestData.get(0).get("ID");
            EMAIL    = Text_GlobalVariable.completedTestData.get(0).get("EMAIL");
            DOB      = Text_GlobalVariable.completedTestData.get(0).get("DOB");
            MOBILE   = Text_GlobalVariable.completedTestData.get(0).get("MOBILE");
            COUNTRY  = Text_GlobalVariable.completedTestData.get(0).get("COUNTRY");

          /*  BASE_URL = data.get("BASE_URL");
            USERNAME = data.get("USERNAME");
            PASSWORD = data.get("PASSWORD");
            ID       = data.get("ID");
            EMAIL    = data.get("EMAIL");
            DOB      = data.get("DOB");
            MOBILE   = data.get("MOBILE");
            COUNTRY  = data.get("COUNTRY");*/

            System.out.println("Global data loaded: " + data.size() + " entries");

            // STEP 4: print values in report if enabled
            printValuesInReport();

        } catch (FilloException e) {
            System.out.println("readAndStoreData failed: " + e.getMessage());
        } finally {
            if (recordset != null) recordset.close();
            if (connection != null) connection.close();
        }
    }

    // ---------- STEP 4: print all loaded values (only if enabled) ----------
    public static void printValuesInReport() {
        if (!PRINT_TO_REPORT) return;                 // skip if disabled
        System.out.println("------ Global Variables Loaded ------");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            // replace System.out with extentTest.info(...) to log into Extent Report
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("-------------------------------------");
    }

    // generic getter for any key (including ones not declared as fields)
    public static String get(String key) {
        return data.get(key);
    }

    public static void loadGlobalVariables() {
    }
}