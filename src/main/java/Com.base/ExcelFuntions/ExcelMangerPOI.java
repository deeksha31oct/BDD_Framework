package Com.base.ExcelFuntions;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;

public class ExcelMangerPOI {

    private final String filePath;
    private final DataFormatter formatter = new DataFormatter();

    public ExcelMangerPOI(String filePath) {
        this.filePath = filePath;
    }

    // Helper - opens the workbook
    private Workbook getWorkbook(FileInputStream fis) throws IOException {
        return new XSSFWorkbook(fis);   // XSSF = .xlsx
    }

    // 1. getData - read a cell by row index + column index (0-based)
    public String getData(String sheetName, int rowNum, int colNum) {
        String value = "";
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                Cell cell = row.getCell(colNum);
                value = formatter.formatCellValue(cell);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    // 2. getRowCount - number of data rows (excludes header row 0)
    public int getRowCount(String sheetName) {
        int count = 0;
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            count = sheet.getLastRowNum();   // last row index = data row count when header is row 0
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    // 3. getColumnCount - number of columns from the header row
    public int getColumnCount(String sheetName) {
        int count = 0;
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row header = sheet.getRow(0);
            if (header != null) {
                count = header.getLastCellNum();   // number of header cells
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    // 4. getColumnNameWithColumnIndex - get header name by column index (0-based)
    public String getColumnNameWithColumnIndex(String sheetName, int colIndex) {
        String name = "";
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row header = sheet.getRow(0);
            if (header != null) {
                name = formatter.formatCellValue(header.getCell(colIndex));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    // 5. getDataIntoDictionary - one row into Map<columnName, value>
    public Map<String, String> getDataIntoDictionary(String sheetName, int rowNum) {
        Map<String, String> map = new LinkedHashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row header = sheet.getRow(0);
            Row dataRow = sheet.getRow(rowNum);
            if (header != null && dataRow != null) {
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    String colName = formatter.formatCellValue(header.getCell(c));
                    String cellVal = formatter.formatCellValue(dataRow.getCell(c));
                    map.put(colName, cellVal);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // 6. getDataIntoDictionaryWithColumnNumber - one row into Map<columnIndex, value>
    public Map<Integer, String> getDataIntoDictionaryWithColumnNumber(String sheetName, int rowNum) {
        Map<Integer, String> map = new LinkedHashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row dataRow = sheet.getRow(rowNum);
            Row header = sheet.getRow(0);
            if (dataRow != null && header != null) {
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    map.put(c, formatter.formatCellValue(dataRow.getCell(c)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // 7. readSpreadSheet - whole sheet into List of row-maps
    public List<Map<String, String>> readSpreadSheet(String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row header = sheet.getRow(0);
            int cols = header.getLastCellNum();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {   // start at 1 to skip header
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, String> map = new LinkedHashMap<>();
                for (int c = 0; c < cols; c++) {
                    String colName = formatter.formatCellValue(header.getCell(c));
                    map.put(colName, formatter.formatCellValue(row.getCell(c)));
                }
                data.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 8. readSpreadSheetWithExcludingColumn - whole sheet but skip one column
    public List<Map<String, String>> readSpreadSheetWithExcludingColumn(String sheetName, String excludeColumn) {
        List<Map<String, String>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = getWorkbook(fis)) {
            Sheet sheet = wb.getSheet(sheetName);
            Row header = sheet.getRow(0);
            int cols = header.getLastCellNum();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Map<String, String> map = new LinkedHashMap<>();
                for (int c = 0; c < cols; c++) {
                    String colName = formatter.formatCellValue(header.getCell(c));
                    if (colName.equalsIgnoreCase(excludeColumn)) continue;   // skip
                    map.put(colName, formatter.formatCellValue(row.getCell(c)));
                }
                data.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 9. updateExcelField - change a cell then write the file back to disk
    public void updateExcelField(String sheetName, int rowNum, int colNum, String newValue) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook wb = getWorkbook(fis);
            Sheet sheet = wb.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            if (row == null) row = sheet.createRow(rowNum);
            Cell cell = row.getCell(colNum);
            if (cell == null) cell = row.createCell(colNum);
            cell.setCellValue(newValue);
            fis.close();

            FileOutputStream fos = new FileOutputStream(filePath);   // write back
            wb.write(fos);
            fos.close();
            wb.close();
            System.out.println("Field updated successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 10. convertCsvToXlsx - read a CSV and write it as a new .xlsx
    public void convertCsvToXlsx(String csvPath, String xlsxPath, String sheetName) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath));
             Workbook wb = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(xlsxPath)) {

            Sheet sheet = wb.createSheet(sheetName);
            String line;
            int rowNum = 0;
            while ((line = br.readLine()) != null) {
                Row row = sheet.createRow(rowNum++);
                String[] cells = line.split(",");        // split each CSV line on commas
                for (int c = 0; c < cells.length; c++) {
                    row.createCell(c).setCellValue(cells[c].trim());
                }
            }
            wb.write(fos);
            System.out.println("CSV converted to XLSX: " + xlsxPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 11. createSheet - add a new sheet to the existing workbook
    public void createSheet(String sheetName) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook wb = getWorkbook(fis);
            if (wb.getSheet(sheetName) == null) {
                wb.createSheet(sheetName);
            }
            fis.close();
            FileOutputStream fos = new FileOutputStream(filePath);
            wb.write(fos);
            fos.close();
            wb.close();
            System.out.println("Sheet created: " + sheetName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 12. createRow - append a new row of data at the end of the sheet
    public void createRow(String sheetName, String[] rowData) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook wb = getWorkbook(fis);
            Sheet sheet = wb.getSheet(sheetName);
            int newRowNum = sheet.getLastRowNum() + 1;     // next empty row
            Row row = sheet.createRow(newRowNum);
            for (int c = 0; c < rowData.length; c++) {
                row.createCell(c).setCellValue(rowData[c]);
            }
            fis.close();
            FileOutputStream fos = new FileOutputStream(filePath);
            wb.write(fos);
            fos.close();
            wb.close();
            System.out.println("Row added at index " + newRowNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 13. createColumn - add a new column (header + values) at the next free column
    public void createColumn(String sheetName, String columnName, String[] values) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook wb = getWorkbook(fis);
            Sheet sheet = wb.getSheet(sheetName);

            Row header = sheet.getRow(0);
            if (header == null) header = sheet.createRow(0);
            int newCol = header.getLastCellNum();          // next empty column
            if (newCol < 0) newCol = 0;

            header.createCell(newCol).setCellValue(columnName);   // header
            for (int i = 0; i < values.length; i++) {
                Row row = sheet.getRow(i + 1);
                if (row == null) row = sheet.createRow(i + 1);
                row.createCell(newCol).setCellValue(values[i]);   // data down the column
            }
            fis.close();
            FileOutputStream fos = new FileOutputStream(filePath);
            wb.write(fos);
            fos.close();
            wb.close();
            System.out.println("Column added: " + columnName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 14. createBorderForExistingCellValue - apply a thin border to one existing cell
    public void createBorderForExistingCellValue(String sheetName, int rowNum, int colNum) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            Workbook wb = getWorkbook(fis);
            Sheet sheet = wb.getSheet(sheetName);

            CellStyle style = wb.createCellStyle();          // borders live on a CellStyle
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            Row row = sheet.getRow(rowNum);
            if (row == null) row = sheet.createRow(rowNum);
            Cell cell = row.getCell(colNum);
            if (cell == null) cell = row.createCell(colNum);
            cell.setCellStyle(style);                        // attach style to the cell

            fis.close();
            FileOutputStream fos = new FileOutputStream(filePath);
            wb.write(fos);
            fos.close();
            wb.close();
            System.out.println("Border applied to cell [" + rowNum + "," + colNum + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }}
        // Demo
        public static void main (String[]args){
            ExcelMangerPOI excel = new ExcelMangerPOI("C:\\testdata\\TestData.xlsx");

            System.out.println("Cell [1,1]: " + excel.getData("Login", 1, 1));
            System.out.println("Rows: " + excel.getRowCount("Login"));
            System.out.println("Columns: " + excel.getColumnCount("Login"));
            System.out.println("Header at col 0: " + excel.getColumnNameWithColumnIndex("Login", 0));
            System.out.println("Row 1 dictionary: " + excel.getDataIntoDictionary("Login", 1));
            System.out.println("Row 1 by index: " + excel.getDataIntoDictionaryWithColumnNumber("Login", 1));
            System.out.println("Full sheet: " + excel.readSpreadSheet("Login"));
            System.out.println("Sheet without Password: " + excel.readSpreadSheetWithExcludingColumn("Login", "Password"));
            excel.updateExcelField("Login", 1, 1, "newPass123");
        }
    }
