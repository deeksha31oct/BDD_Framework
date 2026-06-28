package Com.base.FunctionLibarary;

import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.codoid.products.exception.FilloException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtility {

    // ---------- FILLO (SQL-style read/update) ----------

    /**
     * Returns a Fillo Connection to the given Excel file.
     * Use connection.executeQuery("SELECT ... FROM Sheet WHERE ...")
     */
    public static Connection getConnection(String filePath) throws FilloException {
        Fillo fillo = new Fillo();
        return fillo.getConnection(filePath);   // opens the workbook for Fillo
    }

    /**
     * Convenience: run a SELECT and return the Recordset.
     * Caller loops:  while(rs.next()) rs.getField("colName");
     */
    public static Recordset executeQuery(String filePath, String query) throws FilloException {
        Connection conn = getConnection(filePath);
        return conn.executeQuery(query);
    }

    // ---------- APACHE POI (full cell control) ----------

    /**
     * Returns a POI Workbook for the given Excel file.
     * Works for both .xls and .xlsx via WorkbookFactory.
     * Use for creating/formatting or cell-by-cell reads Fillo can't do.
     */
    public static Workbook getWorkbook(String filePath) throws IOException, InvalidFormatException {
        FileInputStream fis = new FileInputStream(filePath);
        return WorkbookFactory.create(fis);
    }
}