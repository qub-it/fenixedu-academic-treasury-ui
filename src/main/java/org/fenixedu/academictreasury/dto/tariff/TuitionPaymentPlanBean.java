package org.fenixedu.academictreasury.dto.tariff;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularSemester;
import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.candidacy.Ingression;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academictreasury.domain.tuition.EctsCalculationType;
import org.fenixedu.academictreasury.domain.tuition.TuitionCalculationType;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.emory.mathcs.backport.java.util.Collections;

public class TuitionPaymentPlanBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private FinantialEntity finantialEntity;

    private Product product;
    private TuitionPaymentPlanGroup tuitionPaymentPlanGroup;
    private ExecutionYear executionYear;

    private DegreeType degreeType;
    private Set<DegreeCurricularPlan> degreeCurricularPlans = Sets.newHashSet();

    private boolean defaultPaymentPlan;
    private RegistrationRegimeType registrationRegimeType;
    private RegistrationProtocol registrationProtocol;
    private Ingression ingression;
    private CurricularYear curricularYear;
    private CurricularSemester curricularSemester;
    private boolean firstTimeStudent;
    private boolean customized;
    private LocalizedString name;
    private boolean withLaboratorialClasses;

    private List<TupleDataSourceBean> degreeTypeDataSource = null;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource = null;
    private List<TupleDataSourceBean> registrationRegimeTypeDataSource = null;
    private List<TupleDataSourceBean> registrationProtocolDataSource = null;
    private List<TupleDataSourceBean> ingressionDataSource = null;
    private List<TupleDataSourceBean> curricularYearDataSource = null;
    private List<TupleDataSourceBean> semesterDataSource = null;

    private List<TupleDataSourceBean> tuitionCalculationTypeDataSource = null;
    private List<TupleDataSourceBean> ectsCalculationTypeDataSource = null;
    private List<TupleDataSourceBean> interestTypeDataSource = null;
    private List<TupleDataSourceBean> dueDateCalculationTypeDataSource = null;

    public List<AcademicTariffBean> tuitionInstallmentBeans = Lists.newArrayList();

    /*--------------
     * TARIFF FIELDS
     * -------------
     */

    /* Tariff */
    private DateTime beginDate;
    private DateTime endDate;
    private DueDateCalculationType dueDateCalculationType;
    private DateTime fixedDueDate;
    private int numberOfDaysAfterCreationForDueDate;

    /* InterestRate */
    private boolean applyInterests;
    private InterestType interestType;
    private int numberOfDaysAfterDueDate;
    private boolean applyInFirstWorkday;
    private int maximumDaysToApplyPenalty;
    private int maximumMonthsToApplyPenalty;
    private BigDecimal interestFixedAmount;
    private BigDecimal rate;

    /* TuitionInstallment */
    private int installmentOrder;
    private TuitionCalculationType tuitionCalculationType;
    private BigDecimal fixedAmount;
    private EctsCalculationType ectsCalculationType;
    private int factor;
    private int totalEctsOrUnits;
    private boolean academicalActBlockingOff;

    /*---------------------
     * END OF TARIFF FIELDS
     * --------------------
     */

    public TuitionPaymentPlanBean(final Product product, final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final FinantialEntity finantialEntity, final ExecutionYear executionYear) {
        this.product = product;
        this.tuitionPaymentPlanGroup = tuitionPaymentPlanGroup;
        this.finantialEntity = finantialEntity;
        this.executionYear = executionYear;

        this.degreeType = FenixFramework.getDomainObject("4604204941318");

        updateData();
    }

    public void updateData() {
        this.degreeTypeDataSource = degreeTypeDataSource();
        this.degreeCurricularPlanDataSource = degreeCurricularPlanDataSource();

        this.degreeCurricularPlans.clear();

        this.registrationRegimeTypeDataSource = registrationRegimeTypeDataSource();
        this.registrationProtocolDataSource = registrationProtocolDataSource();
        this.ingressionDataSource = ingressionDataSource();
        this.curricularYearDataSource = curricularYearDataSource();
        this.semesterDataSource = semesterDataSource();

        this.tuitionCalculationTypeDataSource = tuitionCalculationTypeDataSource();

        this.ectsCalculationTypeDataSource = ectsCalculationTypeDataSource();

        this.interestTypeDataSource = interestTypeDataSource();

        this.dueDateCalculationTypeDataSource = dueDateCalculationTypeDataSource();

    }

    private List<TupleDataSourceBean> dueDateCalculationTypeDataSource() {
        return ((List<DueDateCalculationType>) Arrays.asList(DueDateCalculationType.values())).stream()
                .map(t -> new TupleDataSourceBean(t.name(), t.getDescriptionI18N().getContent())).collect(Collectors.toList());
    }

    public void addInstallment() {

        final AcademicTariffBean installmentBean =
                new AcademicTariffBean(tuitionInstallmentBeans.get(tuitionInstallmentBeans.size() - 1).getInstallmentOrder());

        installmentBean.setBeginDate(this.beginDate);
        installmentBean.setEndDate(this.endDate);
        installmentBean.setDueDateCalculationType(dueDateCalculationType);
        installmentBean.setFixedDueDate(this.fixedDueDate);
        installmentBean.setNumberOfDaysAfterCreationForDueDate(this.numberOfDaysAfterCreationForDueDate);

        installmentBean.setApplyInterests(this.applyInterests);
        installmentBean.setInterestType(this.interestType);
        installmentBean.setNumberOfDaysAfterDueDate(this.numberOfDaysAfterDueDate);
        installmentBean.setApplyInFirstWorkday(this.applyInFirstWorkday);
        installmentBean.setMaximumDaysToApplyPenalty(this.maximumDaysToApplyPenalty);
        installmentBean.setMaximumMonthsToApplyPenalty(this.maximumMonthsToApplyPenalty);
        installmentBean.setInterestFixedAmount(this.interestFixedAmount);
        installmentBean.setRate(this.rate);

        installmentBean.setTuitionCalculationType(this.tuitionCalculationType);
        installmentBean.setFixedAmount(this.fixedAmount);
        installmentBean.setEctsCalculationType(this.ectsCalculationType);
        installmentBean.setAcademicalActBlockingOff(this.academicalActBlockingOff);

        this.tuitionInstallmentBeans.add(installmentBean);
    }

    public void resetInstallmentFields() {
        this.beginDate = null;
        this.endDate = null;
        this.dueDateCalculationType = DueDateCalculationType.FIXED_DATE;
        this.fixedDueDate = null;
        this.numberOfDaysAfterCreationForDueDate = 0;

        this.applyInterests = false;
        this.interestType = null;
        this.numberOfDaysAfterDueDate = 1;
        this.applyInFirstWorkday = false;
        this.maximumDaysToApplyPenalty = 0;
        this.maximumMonthsToApplyPenalty = 0;
        this.interestFixedAmount = null;
        this.rate = null;

        this.tuitionCalculationType = TuitionCalculationType.FIXED_AMOUNT;
        this.fixedAmount = null;
        this.ectsCalculationType = EctsCalculationType.FIXED_AMOUNT;
        this.academicalActBlockingOff = false;
    }

    public FinantialEntity getFinantialEntity() {
        return finantialEntity;
    }

    public void setFinantialEntity(FinantialEntity finantialEntity) {
        this.finantialEntity = finantialEntity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public TuitionPaymentPlanGroup getTuitionPaymentPlanGroup() {
        return tuitionPaymentPlanGroup;
    }

    public void setTuitionPaymentPlanGroup(TuitionPaymentPlanGroup tuitionPaymentPlanGroup) {
        this.tuitionPaymentPlanGroup = tuitionPaymentPlanGroup;
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    public Set<DegreeCurricularPlan> getDegreeCurricularPlans() {
        return degreeCurricularPlans;
    }

    public void setDegreeCurricularPlans(Set<DegreeCurricularPlan> degreeCurricularPlans) {
        this.degreeCurricularPlans = degreeCurricularPlans;
    }

    public boolean isDefaultPaymentPlan() {
        return defaultPaymentPlan;
    }

    public void setDefaultPaymentPlan(boolean defaultPaymentPlan) {
        this.defaultPaymentPlan = defaultPaymentPlan;
    }

    public RegistrationRegimeType getRegistrationRegimeType() {
        return registrationRegimeType;
    }

    public void setRegistrationRegimeType(RegistrationRegimeType registrationRegimeType) {
        this.registrationRegimeType = registrationRegimeType;
    }

    public RegistrationProtocol getRegistrationProtocol() {
        return registrationProtocol;
    }

    public void setRegistrationProtocol(RegistrationProtocol registrationProtocol) {
        this.registrationProtocol = registrationProtocol;
    }

    public Ingression getIngression() {
        return ingression;
    }

    public void setIngression(Ingression ingression) {
        this.ingression = ingression;
    }

    public CurricularYear getCurricularYear() {
        return curricularYear;
    }

    public void setCurricularYear(CurricularYear curricularYear) {
        this.curricularYear = curricularYear;
    }

    public CurricularSemester getCurricularSemester() {
        return curricularSemester;
    }

    public void setCurricularSemester(CurricularSemester curricularSemester) {
        this.curricularSemester = curricularSemester;
    }

    public boolean isFirstTimeStudent() {
        return firstTimeStudent;
    }

    public void setFirstTimeStudent(boolean firstTimeStudent) {
        this.firstTimeStudent = firstTimeStudent;
    }

    public boolean isCustomized() {
        return customized;
    }

    public void setCustomized(boolean customized) {
        this.customized = customized;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public boolean isWithLaboratorialClasses() {
        return withLaboratorialClasses;
    }

    public void setWithLaboratorialClasses(boolean withLaboratorialClasses) {
        this.withLaboratorialClasses = withLaboratorialClasses;
    }

    public List<AcademicTariffBean> getTuitionInstallmentBeans() {
        return tuitionInstallmentBeans;
    }

    public void setTuitionInstallmentBeans(final List<AcademicTariffBean> tuitionInstallmentBeans) {
        this.tuitionInstallmentBeans = tuitionInstallmentBeans;
    }

    public List<TupleDataSourceBean> getDegreeTypeDataSource() {
        return degreeTypeDataSource;
    }

    public List<TupleDataSourceBean> getDegreeCurricularPlanDataSource() {
        return degreeCurricularPlanDataSource;
    }

    public List<TupleDataSourceBean> getRegistrationRegimeTypeDataSource() {
        return registrationRegimeTypeDataSource;
    }

    public List<TupleDataSourceBean> getRegistrationProtocolDataSource() {
        return registrationProtocolDataSource;
    }

    public List<TupleDataSourceBean> getIngressionDataSource() {
        return ingressionDataSource;
    }

    public List<TupleDataSourceBean> getCurricularYearDataSource() {
        return curricularYearDataSource;
    }

    public List<TupleDataSourceBean> getSemesterDataSource() {
        return semesterDataSource;
    }

    public List<TupleDataSourceBean> getTuitionCalculationTypeDataSource() {
        return tuitionCalculationTypeDataSource;
    }

    public List<TupleDataSourceBean> getEctsCalculationTypeDataSource() {
        return ectsCalculationTypeDataSource;
    }

    public List<TupleDataSourceBean> getInterestTypeDataSource() {
        return interestTypeDataSource;
    }

    /*
     * GETTERS & SETTERS
     */

    public DateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(DateTime beginDate) {
        this.beginDate = beginDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public DueDateCalculationType getDueDateCalculationType() {
        return dueDateCalculationType;
    }

    public void setDueDateCalculationType(DueDateCalculationType dueDateCalculationType) {
        this.dueDateCalculationType = dueDateCalculationType;
    }

    public DateTime getFixedDueDate() {
        return fixedDueDate;
    }

    public void setFixedDueDate(DateTime fixedDueDate) {
        this.fixedDueDate = fixedDueDate;
    }

    public int getNumberOfDaysAfterCreationForDueDate() {
        return numberOfDaysAfterCreationForDueDate;
    }

    public void setNumberOfDaysAfterCreationForDueDate(int numberOfDaysAfterCreationForDueDate) {
        this.numberOfDaysAfterCreationForDueDate = numberOfDaysAfterCreationForDueDate;
    }

    public boolean isApplyInterests() {
        return applyInterests;
    }

    public void setApplyInterests(boolean applyInterests) {
        this.applyInterests = applyInterests;
    }

    public boolean isApplyInFirstWorkday() {
        return applyInFirstWorkday;
    }

    public void setApplyInFirstWorkday(boolean applyInFirstWorkday) {
        this.applyInFirstWorkday = applyInFirstWorkday;
    }

    /* InterestRate */

    public InterestType getInterestType() {
        return interestType;
    }

    public void setInterestType(InterestType interestType) {
        this.interestType = interestType;
    }

    public int getNumberOfDaysAfterDueDate() {
        return numberOfDaysAfterDueDate;
    }

    public void setNumberOfDaysAfterDueDate(int numberOfDaysAfterDueDate) {
        this.numberOfDaysAfterDueDate = numberOfDaysAfterDueDate;
    }

    public int getMaximumDaysToApplyPenalty() {
        return maximumDaysToApplyPenalty;
    }

    public void setMaximumDaysToApplyPenalty(int maximumDaysToApplyPenalty) {
        this.maximumDaysToApplyPenalty = maximumDaysToApplyPenalty;
    }

    public int getMaximumMonthsToApplyPenalty() {
        return maximumMonthsToApplyPenalty;
    }

    public void setMaximumMonthsToApplyPenalty(int maximumMonthsToApplyPenalty) {
        this.maximumMonthsToApplyPenalty = maximumMonthsToApplyPenalty;
    }

    public BigDecimal getInterestFixedAmount() {
        return interestFixedAmount;
    }

    public void setInterestFixedAmount(BigDecimal interestFixedAmount) {
        this.interestFixedAmount = interestFixedAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /* TuitionInstallmentTariff */

    public int getInstallmentOrder() {
        return installmentOrder;
    }

    public void setInstallmentOrder(int installmentOrder) {
        this.installmentOrder = installmentOrder;
    }

    public TuitionCalculationType getTuitionCalculationType() {
        return tuitionCalculationType;
    }

    public void setTuitionCalculationType(TuitionCalculationType tuitionCalculationType) {
        this.tuitionCalculationType = tuitionCalculationType;
    }

    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public EctsCalculationType getEctsCalculationType() {
        return ectsCalculationType;
    }

    public void setEctsCalculationType(EctsCalculationType ectsCalculationType) {
        this.ectsCalculationType = ectsCalculationType;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public int getTotalEctsOrUnits() {
        return totalEctsOrUnits;
    }

    public void setTotalEctsOrUnits(int totalEctsOrUnits) {
        this.totalEctsOrUnits = totalEctsOrUnits;
    }

    public boolean isAcademicalActBlockingOff() {
        return academicalActBlockingOff;
    }

    public void setAcademicalActBlockingOff(boolean academicalActBlockingOff) {
        this.academicalActBlockingOff = academicalActBlockingOff;
    }

    /*
     * -------------
     * Other Methods
     * -------------
     */
    private List<TupleDataSourceBean> degreeTypeDataSource() {
        return DegreeType.all().map((dt) -> new TupleDataSourceBean(dt.getExternalId(), dt.getName().getContent()))
                .collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> degreeCurricularPlanDataSource() {
        if (getExecutionYear() == null) {
            return Collections.<TupleDataSourceBean> emptyList();
        }

        if (getDegreeType() == null) {
            return Collections.<TupleDataSourceBean> emptyList();
        }

        return DegreeCurricularPlan.getDegreeCurricularPlans((dcp) -> {
            return dcp == getDegreeType();
        }).stream().map((dcp) -> new TupleDataSourceBean(dcp.getExternalId(), dcp.getPresentationName(getExecutionYear())))
                .collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> semesterDataSource() {
        return getExecutionYear().getExecutionPeriodsSet().stream()
                .map((cs) -> new TupleDataSourceBean(cs.getExternalId(), cs.getQualifiedName())).collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> curricularYearDataSource() {
        return Bennu.getInstance().getCurricularYearsSet().stream()
                .map((cy) -> new TupleDataSourceBean(cy.getExternalId(), cy.getYear().toString())).collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> ingressionDataSource() {
        return ((List<Ingression>) Arrays.asList(Ingression.values())).stream()
                .map((i) -> new TupleDataSourceBean(i.name(), i.getFullDescription())).collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> registrationProtocolDataSource() {
        return Bennu.getInstance().getRegistrationProtocolsSet().stream()
                .map((rp) -> new TupleDataSourceBean(rp.getExternalId(), rp.getDescription().getContent()))
                .collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> registrationRegimeTypeDataSource() {
        return ((List<RegistrationRegimeType>) Arrays.asList(RegistrationRegimeType.values())).stream()
                .map((t) -> new TupleDataSourceBean(t.name(), t.getLocalizedName())).collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> interestTypeDataSource() {
        return ((List<InterestType>) Arrays.asList(InterestType.values())).stream()
                .map((it) -> new TupleDataSourceBean(it.name(), it.getDescriptionI18N().getContent()))
                .collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> ectsCalculationTypeDataSource() {
        return ((List<EctsCalculationType>) Arrays.asList(EctsCalculationType.values())).stream()
                .map((ct) -> new TupleDataSourceBean(ct.name(), ct.getDescriptionI18N().getContent()))
                .collect(Collectors.toList());
    }

    private List<TupleDataSourceBean> tuitionCalculationTypeDataSource() {
        return ((List<TuitionCalculationType>) Arrays.asList(TuitionCalculationType.values())).stream()
                .map((ct) -> new TupleDataSourceBean(ct.name(), ct.getDescriptionI18N().getContent()))
                .collect(Collectors.toList());
    }

}
