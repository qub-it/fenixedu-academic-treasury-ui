package org.fenixedu.academictreasury.services;

import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.Ingression;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;

import pt.ist.fenixframework.Atomic;

public class TuitionServices {

    @Atomic
    public boolean createInferedTuitionForRegistration(final Registration registration, final ExecutionYear executionYear) {

        final Person person = registration.getPerson();
        // Read person customer
        PersonCustomer personCustomer = PersonCustomer.findUnique(person).orElse(null);

        if (personCustomer == null) {
            personCustomer = PersonCustomer.create(person);
        }

        final TuitionPaymentPlan inferedTuitionPaymentPlan = inferTuitionPaymentPlan(registration, executionYear);

        if (inferedTuitionPaymentPlan == null) {
            return false;
        }

        if (!AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, executionYear).isPresent()) {
            AcademicTreasuryEvent.createForRegistrationTuition(inferedTuitionPaymentPlan.getProduct(), registration,
                    executionYear);
        };

        final AcademicTreasuryEvent academicTreasuryEvent =
                AcademicTreasuryEvent.createForRegistrationTuition(inferedTuitionPaymentPlan.getProduct(), registration,
                        executionYear);

        return inferedTuitionPaymentPlan.createDebitEntries(personCustomer, academicTreasuryEvent);

    }

    private TuitionPaymentPlan inferTuitionPaymentPlan(Registration registration, ExecutionYear executionYear) {
        final DegreeCurricularPlan degreeCurricularPlan =
                registration.getStudentCurricularPlan(executionYear).getDegreeCurricularPlan();

        final RegistrationRegimeType regimeType = registration.getRegimeType(executionYear);
        final RegistrationProtocol registrationProtocol = registration.getRegistrationProtocol();
        final Ingression ingression = registration.getIngression();
        final int semesterWithFirstEnrolments = semesterWithFirstEnrolments(registration, executionYear);
        final CurricularYear curricularYear = CurricularYear.readByYear(curricularYear(registration, executionYear));
        final boolean firstTimeStudent = firstTimeStudent(registration, executionYear);

        final Stream<TuitionPaymentPlan> stream =
                TuitionPaymentPlan.findSortedByPaymentPlanOrder(TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration()
                        .get(), degreeCurricularPlan, executionYear);

        stream.filter(t -> t.getRegistrationRegimeType() == null || t.getRegistrationRegimeType() == regimeType);
        stream.filter(t -> t.getRegistrationProtocol() == null || t.getRegistrationProtocol() == registrationProtocol);
        stream.filter(t -> t.getIngression() == null || t.getIngression() == ingression);
        stream.filter(t -> t.getSemester() == null || t.getSemester() == semesterWithFirstEnrolments);
        stream.filter(t -> t.getCurricularYear() == null || t.getCurricularYear() == curricularYear);
        stream.filter(t -> t.getFirstTimeStudent() == firstTimeStudent);

        return stream.findFirst().orElse(null);
    }

    private boolean firstTimeStudent(Registration registration, ExecutionYear executionYear) {
        return registration.isFirstTime(executionYear);
    }

    private Integer curricularYear(Registration registration, ExecutionYear executionYear) {
        return registration.getCurricularYear(executionYear);
    }

    private int semesterWithFirstEnrolments(final Registration registration, final ExecutionYear executionYear) {
        return registration.getEnrolments(executionYear).stream().map(e -> e.getExecutionPeriod().getSemester()).sorted()
                .findFirst().get();
    }
}
