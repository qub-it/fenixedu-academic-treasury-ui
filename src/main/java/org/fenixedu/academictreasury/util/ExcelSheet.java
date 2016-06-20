package org.fenixedu.academictreasury.util;

import java.util.List;

public class ExcelSheet {

    private String name;
    private List<List<String>> rows;
    
    public ExcelSheet(final String name, final List<List<String>> rows) {
        super();
        this.name = name;
        this.rows = rows;
    }
    
    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on
    
    public String getName() {
        return name;
    }
    
    public List<List<String>> getRows() {
        return rows;
    }
}
