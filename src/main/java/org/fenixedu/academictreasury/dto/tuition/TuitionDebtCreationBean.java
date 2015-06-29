package org.fenixedu.academictreasury.dto.tuition;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Lists;

public class TuitionDebtCreationBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private LocalDate debtDate;
    private ExecutionYear executionYear;
    private Registration registration;
    private boolean infered = true;
    private TuitionPaymentPlan tuitionPaymentPlan;
    private Enrolment enrolment;

    private DebtAccount debtAccount;

    private List<TupleDataSourceBean> registrationDataSource;
    private List<TupleDataSourceBean> executionYearDataSource;
    private List<TupleDataSourceBean> tuitionPaymentPlansDataSource;

    private List<TupleDataSourceBean> standaloneEnrolmentsDataSource;
    private List<TupleDataSourceBean> extracurricularEnrolmentsDataSource;
    private List<TupleDataSourceBean> improvementEnrolmentEvaluationsDataSource;

    private String inferedPaymentPlanName;

    private TuitionPaymentPlanGroup tuitionPaymentPlanGroup;
    private AcademicTax academicTax;

    public TuitionDebtCreationBean(final DebtAccount debtAccount, final TuitionPaymentPlanGroup tuitionPaymentPlanGroup) {
        this.debtAccount = debtAccount;
        this.tuitionPaymentPlanGroup = tuitionPaymentPlanGroup;

        updateData();
    }

    public TuitionDebtCreationBean(final DebtAccount debtAccount, final AcademicTax academicTax) {
        this.debtAccount = debtAccount;
        this.academicTax = academicTax;

        updateData();
    }

    @Atomic
    public void updateData() {
        if (executionYear != null && (registration == null || !possibleExecutionYears().contains(executionYear))) {
            executionYear = null;
            tuitionPaymentPlan = null;
        }

        getRegistrationDataSource();
        getExecutionYearDataSource();
        getTuitionPaymentPlansDataSource();
        getInferedPaymentPlanName();

        getStandaloneEnrolmentsDataSource();
        getExtracurricularEnrolmentsDataSource();
        getImprovementEnrolmentEvaluationsDataSource();

        if (registration != null && executionYear != null && isRegistrationTuition()) {
            debtDate =
                    RegistrationDataByExecutionYear.getOrCreateRegistrationDataByYear(registration, executionYear)
                            .getEnrolmentDate();
        } else {
            debtDate = new LocalDate();
        }
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        if (!isStudent()) {
            executionYearDataSource = Lists.newArrayList();
            return executionYearDataSource;
        }

        if (registration == null) {
            executionYearDataSource = Lists.newArrayList();
            return executionYearDataSource;
        }

        executionYearDataSource =
                possibleExecutionYears().stream().map(e -> new TupleDataSourceBean(e.getExternalId(), e.getQualifiedName()))
                        .collect(Collectors.toList());

        return executionYearDataSource;
    }

    private List<ExecutionYear> possibleExecutionYears() {
        return registration.getEnrolmentsExecutionYears().stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR)
                .collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getRegistrationDataSource() {
        if (!isStudent()) {
            registrationDataSource = Lists.newArrayList();
            return registrationDataSource;
        }

        registrationDataSource =
                ((PersonCustomer) debtAccount.getCustomer())
                        .getPerson()
                        .getStudent()
                        .getRegistrationsSet()
                        .stream()
                        .map(r -> new TupleDataSourceBean(r.getExternalId(), r.getDegree()
                                .getPresentationNameI18N(getExecutionYear()).getContent())).collect(Collectors.toList());

        return registrationDataSource;
    }

    public List<TupleDataSourceBean> getTuitionPaymentPlansDataSource() {
        if (!isStudent()) {
            tuitionPaymentPlansDataSource = Lists.newArrayList();
            return tuitionPaymentPlansDataSource;
        }

        if (getRegistration() == null || getExecutionYear() == null) {
            tuitionPaymentPlansDataSource = Lists.newArrayList();
            return tuitionPaymentPlansDataSource;
        }

        if ((isStandaloneTuition() || isExtracurricularTuition()) && enrolment == null) {
            tuitionPaymentPlansDataSource = Lists.newArrayList();
            return tuitionPaymentPlansDataSource;
        }

        if (isRegistrationTuition()) {
            tuitionPaymentPlansDataSource =
                    TuitionPaymentPlan
                            .find(tuitionPaymentPlanGroup,
                                    registration.getStudentCurricularPlan(getExecutionYear()).getDegreeCurricularPlan(),
                                    getExecutionYear())
                            .map(t -> new TupleDataSourceBean(t.getExternalId(), t.getConditionsDescription().getContent()))
                            .collect(Collectors.toList());
        } else if (isStandaloneTuition() || isExtracurricularTuition()) {
            tuitionPaymentPlansDataSource =
                    TuitionPaymentPlan
                            .find(tuitionPaymentPlanGroup, enrolment.getCurricularCourse().getDegreeCurricularPlan(),
                                    getExecutionYear())
                            .map(t -> new TupleDataSourceBean(t.getExternalId(), t.getConditionsDescription().getContent()))
                            .collect(Collectors.toList());
        }

        return tuitionPaymentPlansDataSource;
    }

    public List<TupleDataSourceBean> getStandaloneEnrolmentsDataSource() {
        if (!isStudent()) {
            standaloneEnrolmentsDataSource = Lists.newArrayList();
            return standaloneEnrolmentsDataSource;
        }

        if (getRegistration() == null || getExecutionYear() == null) {
            standaloneEnrolmentsDataSource = Lists.newArrayList();
            return standaloneEnrolmentsDataSource;
        }

        standaloneEnrolmentsDataSource =
                TuitionServices.standaloneEnrolments(getRegistration(), getExecutionYear()).stream()
                        .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).collect(Collectors.toList()).stream()
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName().getContent()))
                        .collect(Collectors.toList());

        return standaloneEnrolmentsDataSource;
    }

    public List<TupleDataSourceBean> getExtracurricularEnrolmentsDataSource() {
        if (!isStudent()) {
            extracurricularEnrolmentsDataSource = Lists.newArrayList();
            return extracurricularEnrolmentsDataSource;
        }

        if (getRegistration() == null || getExecutionYear() == null) {
            extracurricularEnrolmentsDataSource = Lists.newArrayList();
            return extracurricularEnrolmentsDataSource;
        }

        extracurricularEnrolmentsDataSource =
                TuitionServices.extracurricularEnrolments(getRegistration(), getExecutionYear()).stream()
                        .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).collect(Collectors.toList()).stream()
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName().getContent()))
                        .collect(Collectors.toList());

        return extracurricularEnrolmentsDataSource;
    }

    public List<TupleDataSourceBean> getImprovementEnrolmentEvaluationsDataSource() {
        if (!isStudent()) {
            improvementEnrolmentEvaluationsDataSource = Lists.newArrayList();
            return improvementEnrolmentEvaluationsDataSource;
        }

        if (getRegistration() == null || getExecutionYear() == null) {
            improvementEnrolmentEvaluationsDataSource = Lists.newArrayList();
            return improvementEnrolmentEvaluationsDataSource;
        }

        improvementEnrolmentEvaluationsDataSource =
                TuitionServices
                        .improvementEnrolments(getRegistration(), getExecutionYear())
                        .stream()
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getEnrolment().getName().getContent() + " - "
                                + l.getExecutionPeriod().getQualifiedName())).collect(Collectors.toList()).stream()
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());

        return improvementEnrolmentEvaluationsDataSource;
    }

    public String getInferedPaymentPlanName() {
        if (registration == null || executionYear == null) {
            inferedPaymentPlanName =
                    BundleUtil.getString(Constants.BUNDLE,
                            "label.TuitionDebtCreationBean.infer.select.registration.and.executionYear");
            return inferedPaymentPlanName;
        }

        if ((isStandaloneTuition() || isExtracurricularTuition()) && enrolment == null) {
            inferedPaymentPlanName =
                    BundleUtil.getString(Constants.BUNDLE, "label.TuitionDebtCreationBean.infer.select.enrolment");
            return inferedPaymentPlanName;
        }

        if (isRegistrationTuition()) {
            if (TuitionPaymentPlan.inferTuitionPaymentPlanForRegistration(registration, executionYear) == null) {
                inferedPaymentPlanName = BundleUtil.getString(Constants.BUNDLE, "label.TuitionDebtCreationBean.infer.impossible");
                return inferedPaymentPlanName;
            }

            inferedPaymentPlanName =
                    TuitionPaymentPlan.inferTuitionPaymentPlanForRegistration(registration, executionYear)
                            .getConditionsDescription().getContent();
        } else if (isStandaloneTuition()) {
            if (TuitionPaymentPlan.inferTuitionPaymentPlanForStandaloneEnrolment(registration, executionYear, enrolment) == null) {
                inferedPaymentPlanName = BundleUtil.getString(Constants.BUNDLE, "label.TuitionDebtCreationBean.infer.impossible");
                return inferedPaymentPlanName;
            }

            inferedPaymentPlanName =
                    TuitionPaymentPlan.inferTuitionPaymentPlanForStandaloneEnrolment(registration, executionYear, enrolment)
                            .getConditionsDescription().getContent();
        } else if (isExtracurricularTuition()) {
            if (TuitionPaymentPlan.inferTuitionPaymentPlanForExtracurricularEnrolment(registration, executionYear, enrolment) == null) {
                inferedPaymentPlanName = BundleUtil.getString(Constants.BUNDLE, "label.TuitionDebtCreationBean.infer.impossible");
                return inferedPaymentPlanName;
            }

            inferedPaymentPlanName =
                    TuitionPaymentPlan.inferTuitionPaymentPlanForExtracurricularEnrolment(registration, executionYear, enrolment)
                            .getConditionsDescription().getContent();
        }

        return inferedPaymentPlanName;
    }

    public boolean isImprovementTax() {
        return this.academicTax != null && this.academicTax == AcademicTreasurySettings.getInstance().getImprovementAcademicTax();
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

    /* -----------------
     * GETTERS & SETTERS
     * -----------------
     */

    public boolean isStudent() {
        return debtAccount.getCustomer().isPersonCustomer()
                && ((PersonCustomer) debtAccount.getCustomer()).getPerson().getStudent() != null;
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

    public Enrolment getEnrolment() {
        return enrolment;
    }

    public void setEnrolment(Enrolment enrolment) {
        this.enrolment = enrolment;
    }

}
