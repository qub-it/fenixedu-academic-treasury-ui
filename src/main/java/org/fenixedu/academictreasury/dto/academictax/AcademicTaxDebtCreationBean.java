package org.fenixedu.academictreasury.dto.academictax;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AcademicTaxDebtCreationBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private LocalDate debtDate;
    private ExecutionYear executionYear;
    private Registration registration;
    private EnrolmentEvaluation improvementEvaluation;

    private DebtAccount debtAccount;

    private List<TupleDataSourceBean> registrationDataSource;
    private List<TupleDataSourceBean> executionYearDataSource;
    private List<TupleDataSourceBean> academicTaxesDataSource;
    private List<TupleDataSourceBean> improvementEnrolmentEvaluationsDataSource;

    private AcademicTax academicTax;
    
    private boolean improvementTaxSelected;

    private boolean forceCreation = false;

    public AcademicTaxDebtCreationBean(final DebtAccount debtAccount) {
        this.debtAccount = debtAccount;

        updateData();
    }

    @Atomic
    public void updateData() {
        if (executionYear != null && (registration == null || !possibleExecutionYears().contains(executionYear))) {
            executionYear = null;
            improvementEvaluation = null;
        }

        getAcademicTaxesDataSource();
        getRegistrationDataSource();
        getExecutionYearDataSource();
        getImprovementEnrolmentEvaluationsDataSource();
        
        isImprovementTaxSelected();

        if (registration != null && executionYear != null) {
            debtDate =
                    RegistrationDataByExecutionYear.getOrCreateRegistrationDataByYear(registration, executionYear)
                            .getEnrolmentDate();
            
            if(debtDate == null) {
                debtDate = new LocalDate();                
            }
            
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
        if(academicTax == null || registration == null) {
            return Lists.newArrayList();
        }
        
        if(isImprovementTax()) {
            return TuitionServices.orderedEnrolledAndImprovementExecutionYears(registration);
        } else {
            if(isForceCreation()) {
                return Sets.newHashSet(ExecutionYear.readNotClosedExecutionYears()).stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR)
                .collect(Collectors.toList());
            }
            
            return TuitionServices.orderedEnrolledExecutionYears(registration);
        }
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

    public List<TupleDataSourceBean> getAcademicTaxesDataSource() {
        academicTaxesDataSource =
                AcademicTax.findAll().map(l -> new TupleDataSourceBean(l.getExternalId(), l.getProduct().getName().getContent()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());

        return academicTaxesDataSource;
    }

    public List<TupleDataSourceBean> getImprovementEnrolmentEvaluationsDataSource() {
        if (!isStudent()) {
            improvementEnrolmentEvaluationsDataSource = Lists.newArrayList();
            return improvementEnrolmentEvaluationsDataSource;
        }

        if (academicTax == null) {
            improvementEnrolmentEvaluationsDataSource = Lists.newArrayList();
            return improvementEnrolmentEvaluationsDataSource;
        }

        if (!academicTax.isImprovementTax()) {
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

    public boolean isImprovementTax() {
        return this.academicTax != null && this.academicTax.isImprovementTax();
    }

    
    public boolean isStudent() {
        return debtAccount.getCustomer().isPersonCustomer()
                && ((PersonCustomer) debtAccount.getCustomer()).getPerson().getStudent() != null;
    }
    
    public boolean isImprovementTaxSelected() {
        this.improvementTaxSelected = isImprovementTax();
        
        return this.improvementTaxSelected;
    }
    
    /* -----------------
     * GETTERS & SETTERS
     * -----------------
     */
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

    public AcademicTax getAcademicTax() {
        return academicTax;
    }
    
    public void setAcademicTax(AcademicTax academicTax) {
        this.academicTax = academicTax;
    }
    
    public EnrolmentEvaluation getImprovementEvaluation() {
        return improvementEvaluation;
    }
    
    public void setImprovementEvaluation(EnrolmentEvaluation improvementEvaluation) {
        this.improvementEvaluation = improvementEvaluation;
    }
    
    public boolean isForceCreation() {
        return forceCreation;
    }
    
    public void setForceCreation(boolean forceCreation) {
        this.forceCreation = forceCreation;
    }
    
}
