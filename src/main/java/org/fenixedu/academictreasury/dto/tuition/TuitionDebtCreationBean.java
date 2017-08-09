package org.fenixedu.academictreasury.dto.tuition;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
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
import com.google.common.collect.Sets;

public class TuitionDebtCreationBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private LocalDate debtDate;
    private ExecutionYear executionYear;
    private Registration registration;
    private TuitionPaymentPlan tuitionPaymentPlan;
    private Enrolment enrolment;

    private DebtAccount debtAccount;

    private List<TupleDataSourceBean> registrationDataSource;
    private List<TupleDataSourceBean> executionYearDataSource;
    private List<TupleDataSourceBean> tuitionPaymentPlansDataSource;

    private List<TupleDataSourceBean> standaloneEnrolmentsDataSource;
    private List<TupleDataSourceBean> extracurricularEnrolmentsDataSource;
    private List<TupleDataSourceBean> improvementEnrolmentEvaluationsDataSource;

    private String errorMessage;

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
        getErrorMessage();

        getStandaloneEnrolmentsDataSource();
        getExtracurricularEnrolmentsDataSource();
        getImprovementEnrolmentEvaluationsDataSource();

        if (registration != null && executionYear != null && isRegistrationTuition()
                && !TuitionServices.normalEnrolments(registration, executionYear).isEmpty()) {
            debtDate = TuitionServices.enrolmentDate(registration, executionYear, false);
        } else {
            debtDate = new LocalDate();
        }
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        executionYearDataSource = Lists.newArrayList();

        for (final ExecutionYear executionYear : possibleExecutionYears()) {
            final String id = executionYear.getExternalId();
            String text = executionYear.getQualifiedName();

            if(isRegistrationTuition() && registration != null) {
                final Set<Enrolment> normalEnrolments = TuitionServices.normalEnrolments(registration, executionYear);
                
                if(normalEnrolments.size() == 1) {
                    text += " " + Constants.bundle("label.TuitionDebtCreationBean.enrolments.one");
                } else if (normalEnrolments.size() > 1) {
                    text += " " + Constants.bundle("label.TuitionDebtCreationBean.enrolments", String.valueOf(normalEnrolments.size()));
                }
            }
            
            executionYearDataSource.add(new TupleDataSourceBean(id, text));
        }

        return executionYearDataSource;
    }

    private List<ExecutionYear> possibleExecutionYears() {
        final List<ExecutionYear> executionYears = ExecutionYear.readNotClosedExecutionYears().stream()
                .sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).collect(Collectors.toList());

        return executionYears;
    }

    public List<TupleDataSourceBean> getRegistrationDataSource() {
        if (!isStudent()) {
            registrationDataSource = Lists.newArrayList();
            return registrationDataSource;
        }

        registrationDataSource =
                ((PersonCustomer) debtAccount.getCustomer()).getPerson().getStudent().getRegistrationsSet().stream()
                        .map(r -> new TupleDataSourceBean(r.getExternalId(),
                                String.format("[%s] %s", r.getDegree().getCode(), 
                                        r.getDegree().getPresentationNameI18N(getExecutionYear()).getContent())))
                .collect(Collectors.toList());

        return registrationDataSource;
    }

    public List<TupleDataSourceBean> getTuitionPaymentPlansDataSource() {
        tuitionPaymentPlansDataSource = Lists.newArrayList();

        if (!isStudent()) {
            return tuitionPaymentPlansDataSource;
        }

        if (getRegistration() == null || getExecutionYear() == null) {
            return tuitionPaymentPlansDataSource;
        }

        if ((isStandaloneTuition() || isExtracurricularTuition()) && enrolment == null) {
            return tuitionPaymentPlansDataSource;
        }

        if (isRegistrationTuition()) {
            final StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(getExecutionYear());
            
            if(studentCurricularPlan == null) {
                return tuitionPaymentPlansDataSource;
            }
            
            tuitionPaymentPlansDataSource = TuitionPaymentPlan
                    .find(tuitionPaymentPlanGroup,
                            studentCurricularPlan.getDegreeCurricularPlan(),
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

        standaloneEnrolmentsDataSource = TuitionServices.standaloneEnrolments(getRegistration(), getExecutionYear()).stream()
                .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).collect(Collectors.toList()).stream()
                .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName().getContent())).collect(Collectors.toList());

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

        extracurricularEnrolmentsDataSource = TuitionServices.extracurricularEnrolments(getRegistration(), getExecutionYear())
                .stream().sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).collect(Collectors.toList()).stream()
                .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName().getContent())).collect(Collectors.toList());

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
                TuitionServices.improvementEnrolments(getRegistration(), getExecutionYear()).stream()
                        .map(l -> new TupleDataSourceBean(l.getExternalId(),
                                l.getEnrolment().getName().getContent() + " - " + l.getExecutionPeriod().getQualifiedName()))
                .collect(Collectors.toList()).stream().sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());

        return improvementEnrolmentEvaluationsDataSource;
    }
    
    public boolean isTuitionCharged() {
        if (registration == null || executionYear == null) {
            return false;
        }
        
        if ((isStandaloneTuition() || isExtracurricularTuition()) && enrolment == null) {
            return false;
        }
        
        if (isRegistrationTuition()) {
            return TuitionServices.isTuitionForRegistrationCharged(registration, executionYear);
        } else if (isStandaloneTuition()) {
            return TuitionServices.isTuitionForStandaloneCharged(registration, executionYear, enrolment);
        } else if (isExtracurricularTuition()) {
            return TuitionServices.isTuitionForExtracurricularCharged(registration, executionYear, enrolment);
        }

        return false;
    }

    public String getErrorMessage() {
        this.errorMessage = "";
        this.tuitionPaymentPlan = null;

        if (registration == null || executionYear == null) {
            errorMessage = Constants.bundle("label.TuitionDebtCreationBean.infer.select.registration.and.executionYear");
            return errorMessage;
        }

        if ((isStandaloneTuition() || isExtracurricularTuition()) && enrolment == null) {
            errorMessage = Constants.bundle("label.TuitionDebtCreationBean.infer.select.enrolment");
            return errorMessage;
        }

        if (isRegistrationTuition()) {
            if (TuitionServices.isTuitionForRegistrationCharged(registration, executionYear)) {
                errorMessage = Constants.bundle("error.TuitionDebtCreationBean.tuition.registration.already.charged");
                return errorMessage;
            }
            
            this.tuitionPaymentPlan = TuitionPaymentPlan.inferTuitionPaymentPlanForRegistration(registration, executionYear);
            
            if (tuitionPaymentPlan == null) {
                errorMessage = Constants.bundle("label.TuitionDebtCreationBean.infer.impossible");
            }

        } else if (isStandaloneTuition()) {
            if (TuitionServices.isTuitionForStandaloneCharged(registration, executionYear, enrolment)) {
                errorMessage = Constants.bundle("error.TuitionDebtCreationBean.tuition.registration.already.charged");
                return errorMessage;
            }
            
            this.tuitionPaymentPlan =
                    TuitionPaymentPlan.inferTuitionPaymentPlanForStandaloneEnrolment(registration, executionYear, enrolment);
            
            if (this.tuitionPaymentPlan == null) {
                errorMessage = Constants.bundle("label.TuitionDebtCreationBean.infer.impossible");
            }

        } else if (isExtracurricularTuition()) {
            if (TuitionServices.isTuitionForExtracurricularCharged(registration, executionYear, enrolment)) {
                errorMessage = Constants.bundle("error.TuitionDebtCreationBean.tuition.registration.already.charged");
                return errorMessage;
            }
            
            this.tuitionPaymentPlan =
                    TuitionPaymentPlan.inferTuitionPaymentPlanForExtracurricularEnrolment(registration, executionYear, enrolment);
            
            if (this.tuitionPaymentPlan == null) {
                errorMessage = Constants.bundle("label.TuitionDebtCreationBean.infer.impossible");
            }
        }

        return errorMessage;
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
