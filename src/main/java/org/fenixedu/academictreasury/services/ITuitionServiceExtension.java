package org.fenixedu.academictreasury.services;

import java.util.List;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.dto.tuition.TuitionDebitEntryBean;
import org.joda.time.LocalDate;

public interface ITuitionServiceExtension {

    public boolean applyExtension(final Registration registration, final ExecutionYear executionYear);
    
    public boolean createTuitionForRegistration(final Registration registration, final ExecutionYear executionYear,
            final LocalDate debtDate, final boolean forceCreationIfNotEnrolled, TuitionPaymentPlan tuitionPaymentPlan);
    
    public List<TuitionDebitEntryBean> calculateInstallmentDebitEntryBeans(final Registration registration,
            final ExecutionYear executionYear, final LocalDate debtDate, TuitionPaymentPlan tuitionPaymentPlan);
    
}
