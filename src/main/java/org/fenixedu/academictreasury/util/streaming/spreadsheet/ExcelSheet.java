package org.fenixedu.academictreasury.util.streaming.spreadsheet;

import java.util.stream.Stream;

public interface ExcelSheet {
    
    public String getName();
    public String[] getHeaders();
    public Stream<? extends SpreadsheetRow> getRows();
    
    public static ExcelSheet create(final String name, final String[] headers, final Stream<? extends SpreadsheetRow> rows) {
        return new ExcelSheet() {

            @Override
            public String getName() {
                return name;
            }

            @Override
            public String[] getHeaders() {
                return headers;
            }

            @Override
            public Stream<? extends SpreadsheetRow> getRows() {
                return rows;
            }
            
        };
    }
    
}
