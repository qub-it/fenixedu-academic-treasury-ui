package org.fenixedu.academictreasury.domain.exemptions.requests;

import java.math.BigDecimal;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.event.TreasuryEvent;

public class ExemptionsGenerationRowResult {

    private int rowNum;
    private Registration registration;
    private ExecutionYear executionYear;
    private TreasuryEvent treasuryEvent;
    private DebitEntry debitEntry;
    private BigDecimal amountToExempt;

    private String reason;

    private SortedSet<Integer> tuitionInstallmentsOrderSet;

    public ExemptionsGenerationRowResult(final int rowNum, final Registration registration, final ExecutionYear executionYear,
            final TreasuryEvent treasuryEvent, final DebitEntry debitEntry, final BigDecimal amountToExempt,
            final String reason, final SortedSet<Integer> tuitionInstallmentsOrderSet) {

        this.rowNum = rowNum;
        this.registration = registration;
        this.executionYear = executionYear;
        this.treasuryEvent = treasuryEvent;
        this.debitEntry = debitEntry;
        this.amountToExempt = amountToExempt;
        this.reason = reason;
        this.tuitionInstallmentsOrderSet = tuitionInstallmentsOrderSet;
    }

    public BigDecimal getDiscountAmount() {
        if (treasuryEvent instanceof AcademicTreasuryEvent && getAcademicTreasuryEvent().isForRegistrationTuition()) {
            throw new RuntimeException("invalid call");
        }

        if (debitEntry == null) {
            throw new RuntimeException("debit entry must not be null");
        }

        return calculateDebitEntryDiscount(debitEntry);
    }

    public BigDecimal getDiscountAmount(final int tuitionInstallmentOrder) {
        final DebitEntry tuitionDebitEntry = getTuitionDebitEntry(tuitionInstallmentOrder);
        
        if(tuitionDebitEntry == null) {
            throw new RuntimeException("debit entry must not be null");
        }
        
        return calculateDebitEntryDiscount(tuitionDebitEntry);
    }
    
    public DebitEntry getTuitionDebitEntry(final int tuitionInstallmentOrder) {
        final Set<? extends DebitEntry> debitEntriesSet = DebitEntry.findActive(getTreasuryEvent())
                .filter(d -> d.getProduct().getTuitionInstallmentOrder() == tuitionInstallmentOrder).collect(Collectors.<DebitEntry> toSet());

        if (debitEntriesSet.size() == 0) {
            return null;
        } else if (debitEntriesSet.size() > 1) {
            throw new AcademicTreasuryDomainException(
                    "error.ExemptionsGenerationRequestFile.installmentOrder.debit.entries.found.more.than.one",
                    String.valueOf(rowNum), String.valueOf(tuitionInstallmentOrder));
        }

        return debitEntriesSet.iterator().next();
    }

    public boolean isTreasuryEventForRegistrationTuition() {
        return  treasuryEvent instanceof AcademicTreasuryEvent && ((AcademicTreasuryEvent) treasuryEvent).isTuitionEvent();
    }
    
    private BigDecimal calculateDebitEntryDiscount(final DebitEntry debitEntry) {
        return amountToExempt;
    }

    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on

    
    
    public AcademicTreasuryEvent getAcademicTreasuryEvent() {
        return (AcademicTreasuryEvent) getTreasuryEvent();
    }
    
    public int getRowNum() {
        return rowNum;
    }

    public Registration getRegistration() {
        return registration;
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public TreasuryEvent getTreasuryEvent() {
        return treasuryEvent;
    }

    public DebitEntry getDebitEntry() {
        return debitEntry;
    }

    public BigDecimal getAmountToExempt() {
        return amountToExempt;
    }

    public String getReason() {
        return reason;
    }

    public SortedSet<Integer> getTuitionInstallmentsOrderSet() {
        return tuitionInstallmentsOrderSet;
    }
}
