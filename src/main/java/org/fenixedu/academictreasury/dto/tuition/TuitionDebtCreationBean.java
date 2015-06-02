package org.fenixedu.academictreasury.dto.tuition;

import java.io.Serializable;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.joda.time.LocalDate;

public class TuitionDebtCreationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private LocalDate debtDate;
    private ExecutionYear executionYear;
    private Registration registration;
    private boolean infered;
    private TuitionPaymentPlan tuitionPaymentPlan;
    public LocalDate getDebtDate() {
        return debtDate;
    }
    public void setDebtDate(LocalDate debtDate) {
        this.debtDate = debtDate;
    }
    public ExecutionYear getExecutionYear() {
        return executionYear;
    }
    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }
    public Registration getRegistration() {
        return registration;
    }
    public void setRegistration(Registration registration) {
        this.registration = registration;
    }
    public boolean isInfered() {
        return infered;
    }
    public void setInfered(boolean infered) {
        this.infered = infered;
    }
    public TuitionPaymentPlan getTuitionPaymentPlan() {
        return tuitionPaymentPlan;
    }
    public void setTuitionPaymentPlan(TuitionPaymentPlan tuitionPaymentPlan) {
        this.tuitionPaymentPlan = tuitionPaymentPlan;
    }
    
    
    
    
}
