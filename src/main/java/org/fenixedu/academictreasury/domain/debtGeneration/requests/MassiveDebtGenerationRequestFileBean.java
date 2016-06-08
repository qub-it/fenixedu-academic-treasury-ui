package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

public class MassiveDebtGenerationRequestFileBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private LocalDate debtDate;
    private ExecutionYear executionYear;

    private List<TupleDataSourceBean> executionYearDataSource;

    private TuitionPaymentPlanGroup tuitionPaymentPlanGroup;

    public MassiveDebtGenerationRequestFileBean(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup) {
        this.tuitionPaymentPlanGroup = tuitionPaymentPlanGroup;
        this.debtDate = new LocalDate();

        updateData();
    }

    @Atomic
    public void updateData() {
        getExecutionYearDataSource();
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        executionYearDataSource = possibleExecutionYears().stream()
                .map(e -> new TupleDataSourceBean(e.getExternalId(), e.getQualifiedName())).collect(Collectors.toList());

        return executionYearDataSource;
    }

    private List<ExecutionYear> possibleExecutionYears() {
        final List<ExecutionYear> executionYears = ExecutionYear.readNotClosedExecutionYears().stream()
                .sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).collect(Collectors.toList());

        return executionYears;
    }

    public boolean isStandaloneTuition() {
        return this.tuitionPaymentPlanGroup != null && this.tuitionPaymentPlanGroup.isForStandalone();
    }

    public boolean isExtracurricularTuition() {
        return this.tuitionPaymentPlanGroup != null && this.tuitionPaymentPlanGroup.isForExtracurricular();
    }

    public boolean isRegistrationTuition() {
        return this.tuitionPaymentPlanGroup != null && this.tuitionPaymentPlanGroup.isForRegistration();
    }
    
    // @formatter:off
    /* -----------------
     * GETTERS & SETTERS
     * -----------------
     */
    // @formatter:on

    public TuitionPaymentPlanGroup getTuitionPaymentPlanGroup() {
        return tuitionPaymentPlanGroup;
    }
    
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

}
