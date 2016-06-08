package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.joda.time.LocalDate;

public class MassiveDebtGenerationRowResult {

    private ExecutionYear executionYear;
    private StudentCurricularPlan studentCurricularPlan;
    private TuitionPaymentPlan tuitionPaymentPlan;
    private LocalDate debtDate;

    public MassiveDebtGenerationRowResult(final ExecutionYear executionYear, final StudentCurricularPlan studentCurricularPlan,
            final TuitionPaymentPlan tuitionPaymentPlan, final LocalDate debtDate) {
        super();
        
        this.executionYear = executionYear;
        this.studentCurricularPlan = studentCurricularPlan;
        this.tuitionPaymentPlan = tuitionPaymentPlan;
        this.debtDate = debtDate;
    }

    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        return studentCurricularPlan;
    }

    public TuitionPaymentPlan getTuitionPaymentPlan() {
        return tuitionPaymentPlan;
    }
    
    public LocalDate getDebtDate() {
        return debtDate;
    }

}
