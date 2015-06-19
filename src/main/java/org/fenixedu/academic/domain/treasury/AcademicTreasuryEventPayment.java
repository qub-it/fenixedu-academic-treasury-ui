package org.fenixedu.academic.domain.treasury;

import java.math.BigDecimal;

import org.fenixedu.treasury.domain.document.SettlementEntry;
import org.joda.time.LocalDate;

public class AcademicTreasuryEventPayment implements IAcademicTreasuryEventPayment {

    private SettlementEntry settlementEntry;
    
    public AcademicTreasuryEventPayment(final SettlementEntry settlementEntry) {
        this.settlementEntry = settlementEntry;
    }
    
    @Override
    public LocalDate getPaymentDate() {
        return settlementEntry.getEntryDateTime().toLocalDate();
    }

    @Override
    public BigDecimal getPayedAmount() {
        return settlementEntry.getAmount();
    }

}
