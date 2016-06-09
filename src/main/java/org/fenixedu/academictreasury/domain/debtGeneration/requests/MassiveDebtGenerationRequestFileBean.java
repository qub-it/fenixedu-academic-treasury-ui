package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
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

    private List<TupleDataSourceBean> academicTaxesDataSource;

    private boolean forAcademicTax = false;

    private TuitionPaymentPlanGroup tuitionPaymentPlanGroup;

    private AcademicTax academicTax;

    public MassiveDebtGenerationRequestFileBean() {
        this.debtDate = new LocalDate();

        updateData();
    }

    @Atomic
    public void updateData() {
        getExecutionYearDataSource();
        getAcademicTaxesDataSource();

        if (isForAcademicTax()) {
            this.tuitionPaymentPlanGroup = null;
        } else {
            this.tuitionPaymentPlanGroup = TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get();
            this.academicTax = null;
        }
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        executionYearDataSource = possibleExecutionYears().stream()
                .map(e -> new TupleDataSourceBean(e.getExternalId(), e.getQualifiedName())).collect(Collectors.toList());

        return executionYearDataSource;
    }

    public List<TupleDataSourceBean> getAcademicTaxesDataSource() {
        academicTaxesDataSource =
                AcademicTax.findAll().map(e -> new TupleDataSourceBean(e.getExternalId(), e.getProduct().getName().getContent()))
                        .collect(Collectors.toList());

        return academicTaxesDataSource;
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

    public boolean isForAcademicTax() {
        return forAcademicTax;
    }
    
    public void setForAcademicTax(boolean forAcademicTax) {
        this.forAcademicTax = forAcademicTax;
    }
    
    public TuitionPaymentPlanGroup getTuitionPaymentPlanGroup() {
        return tuitionPaymentPlanGroup;
    }

    public void setTuitionPaymentPlanGroup(TuitionPaymentPlanGroup tuitionPaymentPlanGroup) {
        this.tuitionPaymentPlanGroup = tuitionPaymentPlanGroup;
    }

    public AcademicTax getAcademicTax() {
        return academicTax;
    }

    public void setAcademicTax(AcademicTax academicTax) {
        this.academicTax = academicTax;
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
