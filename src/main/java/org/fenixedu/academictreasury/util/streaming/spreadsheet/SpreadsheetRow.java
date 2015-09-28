package org.fenixedu.academictreasury.util.streaming.spreadsheet;

import org.fenixedu.academictreasury.domain.reports.ErrorsLog;


public interface SpreadsheetRow {
    
    public void writeCellValues(final org.apache.poi.ss.usermodel.Row row, final ErrorsLog errorsLog);
}
