package org.fenixedu.academictreasury.dto.academicservicerequest;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.services.EmolumentServices;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Lists;

public class AcademicServiceRequestDebtCreationBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    private LocalDate debtDate;
    private Registration registration;

    private DebtAccount debtAccount;

    private List<TupleDataSourceBean> registrationDataSource;
    private List<TupleDataSourceBean> academicServiceRequestesDataSource;

    private AcademicServiceRequest academicServiceRequest;

    public AcademicServiceRequestDebtCreationBean(final DebtAccount debtAccount) {
        this.debtAccount = debtAccount;

        updateData();
    }

    @Atomic
    public void updateData() {
        if (registration == null) {
            academicServiceRequest = null;
        }

        getRegistrationDataSource();
        getAcademicServiceRequestesDataSource();

        if (registration != null && academicServiceRequest != null) {
            debtDate = EmolumentServices.possibleDebtDateOnAcademicService(academicServiceRequest);

            if (debtDate == null) {
                debtDate = new LocalDate();
            }
        } else {
            debtDate = new LocalDate();
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
                        .map(r -> new TupleDataSourceBean(r.getExternalId(), r.getDegree().getPresentationNameI18N().getContent()))
                        .collect(Collectors.toList());

        return registrationDataSource;
    }

    public List<TupleDataSourceBean> getAcademicServiceRequestesDataSource() {
        if (registration == null) {
            academicServiceRequestesDataSource = Lists.newArrayList();
            return academicServiceRequestesDataSource;
        }

        academicServiceRequestesDataSource =
                registration.getAcademicServiceRequestsSet().stream()
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getDescription()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());

        return academicServiceRequestesDataSource;
    }

    public boolean isStudent() {
        return debtAccount.getCustomer().isPersonCustomer()
                && ((PersonCustomer) debtAccount.getCustomer()).getPerson().getStudent() != null;
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

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public AcademicServiceRequest getAcademicServiceRequest() {
        return academicServiceRequest;
    }

    public void setAcademicServiceRequest(AcademicServiceRequest academicServiceRequest) {
        this.academicServiceRequest = academicServiceRequest;
    }

}
