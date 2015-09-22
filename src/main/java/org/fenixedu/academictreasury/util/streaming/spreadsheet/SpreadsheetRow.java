package org.fenixedu.academictreasury.util.streaming.spreadsheet;


public interface SpreadsheetRow {
    
    public void writeCellValues(final org.apache.poi.ss.usermodel.Row row);
}
