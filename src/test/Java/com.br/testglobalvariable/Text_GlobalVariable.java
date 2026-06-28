package com.br.testglobalvariable;

import Com.base.globalvariable.Framework_globalvariable;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Text_GlobalVariable {

    // file separator pulled from framework-level globals
   // public static String gloabal_fs = Framework_GloablVariable.fileSepartor;

    // ---- Excel "Key"/column names (must EXACTLY match the sheet) ----
    public static final String BASE_URL = "BASE_URL";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String ID       = "ID";
    public static final String EMAIL    = "EMAIL";
    public static final String DOB      = "DOB";
    public static final String MOBILE   = "MOBILE";
    public static final String COUNTRY  = "COUNTRY";

    // ---- Excel data holder: each row -> Map<columnName, value> ----
    public static List<Map<String, String>> completedTestData = new ArrayList<>();
    private static String excelPath;
public  static String global_fs = Framework_globalvariable.FILE_SEPARATOR;
    // STEP 1: set path
    public static void setExcelPath(String path) {
        excelPath = path;
        System.out.println("Excel path set to: " + excelPath);
    }

    // STEP 2: read all rows into completedTestData
    public static void readExcelData(String sheetName) {
        Connection connection = null;
        Recordset recordset = null;
        try {
            Fillo fillo = new Fillo();
            connection = fillo.getConnection(excelPath);
            recordset = connection.executeQuery("SELECT * FROM " + sheetName);

            List<String> columns = recordset.getFieldNames();
            while (recordset.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String col : columns) {
                    row.put(col, recordset.getField(col));
                }
                completedTestData.add(row);
            }
            System.out.println("Excel read: " + completedTestData.size() + " rows");

        } catch (FilloException e) {
            System.out.println("readExcelData failed: " + e.getMessage());
        } finally {
            if (recordset != null) recordset.close();
            if (connection != null) connection.close();
        }
    }
}