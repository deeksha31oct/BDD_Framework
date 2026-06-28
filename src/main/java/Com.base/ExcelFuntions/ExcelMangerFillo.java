package Com.base.ExcelFuntions;

import Com.base.FunctionLibarary.ExcelUtility;
import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import java.util.*;
import java.util.logging.Logger;

public class ExcelMangerFillo {

        private final String filePath;
        public static Logger log = Logger.getLogger(ExcelMangerFillo.class.getName());

        public ExcelMangerFillo(String filePath) {
            this.filePath = filePath;
        }

        // Helper - opens a connection
        private Connection getConnection() throws FilloException, FilloException {
            return new Fillo().getConnection(filePath);
        }

        // 1. getData - read a single cell value by a WHERE condition
        public String getData(String sheetName, String selectColumn, String whereColumn, String whereValue) {
            String result = null;
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = getConnection();
                String query = "SELECT * FROM " + sheetName + " WHERE " + whereColumn + "='" + whereValue + "'";
                rs = conn.executeQuery(query);
                if (rs.next()) {
                    result = rs.getField(selectColumn);
                }
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return result;
        }

        // 2. getRowCount - number of data rows in a sheet
        public int getRowCount(String sheetName) {
            int count = 0;
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = getConnection();
                rs = conn.executeQuery("SELECT * FROM " + sheetName);
                count = rs.getCount();
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return count;
        }

        // 3. getColumnCount - number of columns (headers) in a sheet
        public int getColumnCount(String sheetName) {
            int count = 0;
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = getConnection();
                rs = conn.executeQuery("SELECT * FROM " + sheetName);
                count = rs.getFieldNames().size();
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return count;
        }

        // 4. getDataIntoDictionary - read ONE matching row into a Map<columnName, value>
        public Map<String, String> getDataIntoDictionary(String sheetName, String query, String admin) {
            Dictionary<Integer , HashMap<String, String>> totalSpreadsheet = new Hashtable<>();
            ArrayList<String>fieldName = new ArrayList<>();
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = ExcelUtility.getConnection(filePath);
                log.info("Executing query: " + query);
                log.info("fillo connection estibish :" + filePath + " ");
                int coloumncount = getColumnCount(sheetName);
                fieldName= rs.getFieldNames();
                int rownumber = 0;
                while (rs.next()) {
                    HashMap<String, String> rowMap = new HashMap<>();
                    for(int colcount =0 ;colcount<coloumncount;colcount++)
                    {
                        rowMap.put(fieldName.get(colcount), rs.getField(fieldName.get(colcount)));
                    }
                    totalSpreadsheet.put(rownumber, rowMap);
                    rownumber++;
                }
                log.info("data is captured from " + sheetName);
            } catch (FilloException e) {
                assert false : "Error occurred while fetching data from sheet: " + sheetName;
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return (Map<String, String>) totalSpreadsheet;
        }

        // 5. getDataIntoDictionaryWithColumnNumber - read ONE matching row into Map<columnIndex, value>
        public Map<Integer, String> getDataIntoDictionaryWithColumnNumber(String sheetName, String whereColumn, String whereValue) {
            Map<Integer, String> rowMap = new LinkedHashMap<>();
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = getConnection();
                String query = "SELECT * FROM " + sheetName + " WHERE " + whereColumn + "='" + whereValue + "'";
                rs = conn.executeQuery(query);
                if (rs.next()) {
                    List<String> columns = rs.getFieldNames();
                    for (int i = 0; i < columns.size(); i++) {
                        rowMap.put(i, rs.getField(columns.get(i)));
                    }
                }
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return rowMap;
        }

        // 6. readSpreadSheet - read the WHOLE sheet into a List of row-maps
        public List<Map<String, String>> readSpreadSheet(String sheetName) {
            List<Map<String, String>> data = new ArrayList<>();
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = getConnection();
                rs = conn.executeQuery("SELECT * FROM " + sheetName);
                List<String> columns = rs.getFieldNames();
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (String col : columns) {
                        row.put(col, rs.getField(col));
                    }
                    data.add(row);
                }
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return data;
        }

        // 7. readSpreadSheetWithExcludingColumn - read whole sheet but skip one column
        public List<Map<String, String>> readSpreadSheetWithExcludingColumn(String sheetName, String excludeColumn) {
            List<Map<String, String>> data = new ArrayList<>();
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = getConnection();
                rs = conn.executeQuery("SELECT * FROM " + sheetName);
                List<String> columns = rs.getFieldNames();
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    for (String col : columns) {
                        if (col.equalsIgnoreCase(excludeColumn)) continue;   // skip excluded column
                        row.put(col, rs.getField(col));
                    }
                    data.add(row);
                }
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return data;
        }

        // 8. getColumnNameWithColumnIndex - get a column header name by its index (0-based)
        public String getColumnNameWithColumnIndex(String sheetName, int columnIndex) {
            String columnName = null;
            Connection conn = null;
            Recordset rs = null;
            try {
                conn = getConnection();
                rs = conn.executeQuery("SELECT * FROM " + sheetName);
                List<String> columns = rs.getFieldNames();
                if (columnIndex >= 0 && columnIndex < columns.size()) {
                    columnName = columns.get(columnIndex);
                }
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return columnName;
        }

        // 9. updateExcelField - update a cell value by a WHERE condition
        public void updateExcelField(String sheetName, String setColumn, String newValue, String whereColumn, String whereValue) {
            Connection conn = null;
            try {
                conn = getConnection();
                String query = "UPDATE " + sheetName + " SET " + setColumn + "='" + newValue
                        + "' WHERE " + whereColumn + "='" + whereValue + "'";
                conn.executeUpdate(query);
                System.out.println("Field updated successfully");
            } catch (FilloException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.close();
            }
        }

        // Demo
        public static void main(String[] args) {
            ExcelMangerFillo excel = new ExcelMangerFillo("C:\\testdata\\TestData.xlsx");

            System.out.println("Password: " + excel.getData("Login", "Password", "Username", "admin"));
            System.out.println("Rows: " + excel.getRowCount("Login"));
            System.out.println("Columns: " + excel.getColumnCount("Login"));
            System.out.println("Row as dictionary: " + excel.getDataIntoDictionary("Login", "Username", "admin"));
            System.out.println("Row by index: " + excel.getDataIntoDictionaryWithColumnNumber("Login", "Username", "admin"));
            System.out.println("Column at index 0: " + excel.getColumnNameWithColumnIndex("Login", 0));
            System.out.println("Full sheet: " + excel.readSpreadSheet("Login"));
            System.out.println("Sheet without Password: " + excel.readSpreadSheetWithExcludingColumn("Login", "Password"));
            excel.updateExcelField("Login", "Password", "newPass123", "Username", "admin");
        }

        public Dictionary<Integer , HashMap<String, String>> readSpreadSheet(String completeExcelPath, String sheetName, String query) {
            ExcelMangerFillo ExcelMangerFillo = new ExcelMangerFillo(completeExcelPath);
               Dictionary<Integer, HashMap<String, String>> totalSpreadsheet = (Dictionary<Integer, HashMap<String, String>>) getDataIntoDictionary(sheetName,query, "admin");
               return  totalSpreadsheet ;

        }
}

