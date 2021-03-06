package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.joda.time.LocalDate;

public class MassiveDebtGenerationRowResult {

    private int rowNum;
    private ExecutionYear executionYear;
    private StudentCurricularPlan studentCurricularPlan;
    private TuitionPaymentPlan tuitionPaymentPlan;
    private LocalDate debtDate;
    private AcademicTax academicTax;
    private DebitEntry debitEntry;

    public MassiveDebtGenerationRowResult(final int rowNum, final ExecutionYear executionYear, final StudentCurricularPlan studentCurricularPlan,
            final TuitionPaymentPlan tuitionPaymentPlan, final LocalDate debtDate) {
        super();

        this.rowNum = rowNum;
        this.executionYear = executionYear;
        this.studentCurricularPlan = studentCurricularPlan;
        this.tuitionPaymentPlan = tuitionPaymentPlan;
        this.debtDate = debtDate;
    }

    public MassiveDebtGenerationRowResult(final int rowNum, final ExecutionYear executionYear, final StudentCurricularPlan studentCurricularPlan,
            final AcademicTax academicTax, final LocalDate debtDate) {
        super();
        
        this.rowNum = rowNum;
        this.executionYear = executionYear;
        this.studentCurricularPlan = studentCurricularPlan;
        this.academicTax = academicTax;
        this.debtDate = debtDate;
    }

    public MassiveDebtGenerationRowResult(final int rowNum, final DebitEntry debitEntry) {
        super();
        
        this.rowNum = rowNum;
        this.debitEntry = debitEntry;
    }
    
    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on

    public int getRowNum() {
        return rowNum;
    }
    
    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        return studentCurricularPlan;
    }

    public TuitionPaymentPlan getTuitionPaymentPlan() {
        return tuitionPaymentPlan;
    }
    
    public AcademicTax getAcademicTax() {
        return academicTax;
    }

    public LocalDate getDebtDate() {
        return debtDate;
    }
    
    public DebitEntry getDebitEntry() {
        return debitEntry;
    }
}
