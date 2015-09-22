package org.fenixedu.academictreasury.util.streaming.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface Spreadsheet {

    public static final int ROWS_IN_MEMORY = 100;

    public String[] getHeaders();
    public Stream<? extends SpreadsheetRow> getRows();
    
    public static byte[] buildSpreadsheetContent(final Spreadsheet spreadsheet) {
        final SXSSFWorkbook wb = new SXSSFWorkbook(ROWS_IN_MEMORY);
        
        final Sheet sh = wb.createSheet();
        final Row row = sh.createRow(0);
        
        final Font headerFont = wb.createFont();
        headerFont.setBold(true);
        
        if(row.getRowStyle() == null) {
            row.setRowStyle(wb.createCellStyle());
        }
        
        row.getRowStyle().setFont(headerFont);
        
        final String[] headers = spreadsheet.getHeaders();
        for (int i = 0; i < headers.length; i++) {
            final Cell cell = row.createCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(headers[i]);
        }
        
        spreadsheet.getRows().forEach(r -> r.writeCellValues(sh.createRow(sh.getLastRowNum() + 1)));
        
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        try {
            wb.write(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        return output.toByteArray();
    }
    
    
}
