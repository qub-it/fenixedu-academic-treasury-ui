package org.fenixedu.academictreasury.dto.tuition;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;

public class TuitionDebtCreationBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private LocalDate debtDate;
    private ExecutionYear executionYear;
    private Registration registration;
    private boolean infered = true;
    private TuitionPaymentPlan tuitionPaymentPlan;

    private DebtAccount debtAccount;

    private List<TupleDataSourceBean> registrationDataSource;
    private List<TupleDataSourceBean> executionYearDataSource;
    private List<TupleDataSourceBean> tuitionPaymentPlansDataSource;

    private String inferedPaymentPlanName;

    public TuitionDebtCreationBean(final DebtAccount debtAccount) {
        this.debtAccount = debtAccount;

        updateData();
    }

    public void updateData() {
        getRegistrationDataSource();
        getExecutionYearDataSource();
        getTuitionPaymentPlansDataSource();
        getInferedPaymentPlanName();
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
                registration.getEnrolmentsExecutionYears().stream()
                        .map(e -> new TupleDataSourceBean(e.getExternalId(), e.getQualifiedName())).collect(Collectors.toList());

        return executionYearDataSource;
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

        tuitionPaymentPlansDataSource =
                TuitionPaymentPlan
                        .find(TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get(),
                                registration.getStudentCurricularPlan(getExecutionYear()).getDegreeCurricularPlan(),
                                getExecutionYear())
                        .map(t -> new TupleDataSourceBean(t.getExternalId(), t.getConditionsDescription().getContent()))
                        .collect(Collectors.toList());

        return tuitionPaymentPlansDataSource;
    }

    public String getInferedPaymentPlanName() {
        if (registration == null || executionYear == null) {
            inferedPaymentPlanName =
                    BundleUtil.getString(Constants.BUNDLE,
                            "label.TuitionDebtCreationBean.infer.select.registration.and.executionYear");
            return inferedPaymentPlanName;
        }

        if(TuitionPaymentPlan.inferTuitionPaymentPlan(registration, executionYear) == null) {
            inferedPaymentPlanName =
                    BundleUtil.getString(Constants.BUNDLE,
                            "label.TuitionDebtCreationBean.infer.impossible");
            return inferedPaymentPlanName;
        }
        
        inferedPaymentPlanName = TuitionPaymentPlan.inferTuitionPaymentPlan(registration, executionYear).getConditionsDescription().getContent();
        return inferedPaymentPlanName;
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

}
