package org.fenixedu.academictreasury.domain.reports;

public enum DebtReportRequestType {

    INVOICE_ENTRIES,
    SETTLEMENT_ENTRIES;
    
    
    public boolean isRequestForInvoiceEntries() {
        return this == INVOICE_ENTRIES;
    }
    
    public boolean isRequestForSettlementEntries() {
        return this == SETTLEMENT_ENTRIES;
    }
    
}
