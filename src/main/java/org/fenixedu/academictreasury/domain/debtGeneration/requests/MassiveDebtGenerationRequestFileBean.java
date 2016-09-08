package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

public class MassiveDebtGenerationRequestFileBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private MassiveDebtGenerationType massiveDebtGenerationType;
    private LocalDate debtDate;
    private ExecutionYear executionYear;
    private TuitionPaymentPlanGroup tuitionPaymentPlanGroup;
    private AcademicTax academicTax;
    private String reason;
    private FinantialInstitution finantialInstitution;

    private List<TupleDataSourceBean> massiveDebtGenerationTypeDataSource;
    private List<TupleDataSourceBean> executionYearDataSource;
    private List<TupleDataSourceBean> academicTaxesDataSource;
    private List<TupleDataSourceBean> finantialInstitutionDataSource;

    private boolean forAcademicTax = false;

    private boolean executionYearRequired;
    private boolean academicTaxRequired;
    private boolean debtDateRequired;
    private boolean reasonRequired;
    private boolean finantialInstitutionRequired;

    public MassiveDebtGenerationRequestFileBean() {
        this.debtDate = new LocalDate();

        if (FinantialInstitution.findAll().count() == 1) {
            this.finantialInstitution = FinantialInstitution.findAll().findFirst().get();
        }

        updateData();
    }

    public MassiveDebtGenerationRequestFileBean(final MassiveDebtGenerationRequestFile file) {
        setMassiveDebtGenerationType(file.getMassiveDebtGenerationType());
        setTuitionPaymentPlanGroup(file.getTuitionPaymentPlanGroup());
        setAcademicTax(file.getAcademicTax());
        setExecutionYear(file.getExecutionYear());
        setDebtDate(file.getDebtDate());
        setReason(file.getReason());
        setFinantialInstitution(file.getFinantialInstitution());
    }

    @Atomic
    public void updateData() {
        getMassiveDebtGenerationTypeDataSource();
        getExecutionYearDataSource();
        getAcademicTaxesDataSource();
        getMassiveDebtGenerationTypeDataSource();
        getFinantialInstitutionDataSource();

        if (isForAcademicTax()) {
            this.tuitionPaymentPlanGroup = null;
        } else {
            this.tuitionPaymentPlanGroup = TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get();
            this.academicTax = null;
        }

        setDebtDateRequired(getMassiveDebtGenerationType() != null ? getMassiveDebtGenerationType().isDebtDateRequired() : false);

        setAcademicTaxRequired(
                getMassiveDebtGenerationType() != null ? getMassiveDebtGenerationType().isForAcademicTaxRequired() : false);

        setExecutionYearRequired(
                getMassiveDebtGenerationType() != null ? getMassiveDebtGenerationType().isExecutionRequired() : false);

        setReasonRequired(getMassiveDebtGenerationType() != null ? getMassiveDebtGenerationType().isReasonRequired() : false);

        setFinantialInstitutionRequired(
                getMassiveDebtGenerationType() != null ? getMassiveDebtGenerationType().isFinantialInstitutionRequired() : false);
    }

    public List<TupleDataSourceBean> getMassiveDebtGenerationTypeDataSource() {
        return massiveDebtGenerationTypeDataSource = MassiveDebtGenerationType.findAllActive()
                .map(e -> new TupleDataSourceBean(e.getExternalId(), e.getName())).collect(Collectors.toList());
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

    public List<TupleDataSourceBean> getFinantialInstitutionDataSource() {
        finantialInstitutionDataSource = FinantialInstitution.findAll()
                .map(e -> new TupleDataSourceBean(e.getExternalId(), e.getName())).collect(Collectors.toList());
        
        return finantialInstitutionDataSource;
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

    public MassiveDebtGenerationType getMassiveDebtGenerationType() {
        return massiveDebtGenerationType;
    }

    public void setMassiveDebtGenerationType(MassiveDebtGenerationType massiveDebtGenerationType) {
        this.massiveDebtGenerationType = massiveDebtGenerationType;
    }

    public boolean isDebtDateRequired() {
        return debtDateRequired;
    }

    public void setDebtDateRequired(boolean debtDateRequired) {
        this.debtDateRequired = debtDateRequired;
    }

    public boolean isExecutionYearRequired() {
        return executionYearRequired;
    }

    public void setExecutionYearRequired(boolean executionYearRequired) {
        this.executionYearRequired = executionYearRequired;
    }

    public void setExecutionYearDataSource(List<TupleDataSourceBean> executionYearDataSource) {
        this.executionYearDataSource = executionYearDataSource;
    }

    public boolean isAcademicTaxRequired() {
        return this.academicTaxRequired;
    }

    public void setAcademicTaxRequired(boolean forAcademicTaxRequired) {
        this.academicTaxRequired = forAcademicTaxRequired;
    }

    public boolean isReasonRequired() {
        return reasonRequired;
    }

    public void setReasonRequired(boolean reasonRequired) {
        this.reasonRequired = reasonRequired;
    }

    public boolean isFinantialInstitutionRequired() {
        return finantialInstitutionRequired;
    }

    public void setFinantialInstitutionRequired(boolean finantialInstitutionRequired) {
        this.finantialInstitutionRequired = finantialInstitutionRequired;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public FinantialInstitution getFinantialInstitution() {
        return finantialInstitution;
    }

    public void setFinantialInstitution(FinantialInstitution finantialInstitution) {
        this.finantialInstitution = finantialInstitution;
    }
}
