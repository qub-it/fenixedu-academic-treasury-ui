package org.fenixedu.academictreasury.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class AcademicTaxServices {

    public static AcademicTreasuryEvent findAcademicTreasuryEvent(final Registration registration,
            final ExecutionYear executionYear, final AcademicTax academicTax) {
        return AcademicTreasuryEvent.findUniqueForAcademicTax(registration, executionYear, academicTax).orElse(null);
    }

    @Atomic
    public static boolean createAcademicTax(final Registration registration, final ExecutionYear executionYear, final AcademicTax academicTax) {
        if (normalEnrolments(registration, executionYear).isEmpty()) {
            return false;
        }

        if (academicTax.isAppliedOnRegistrationFirstYear() && !academicTax.isAppliedOnRegistrationSubsequentYears()
                && registration.getStartExecutionYear() != executionYear) {
            return false;
        }

        if (!academicTax.isAppliedOnRegistrationFirstYear() && academicTax.isAppliedOnRegistrationSubsequentYears()
                && !registration.getStartExecutionYear().isBefore(executionYear)) {
            return false;
        }

        if(findAcademicTreasuryEvent(registration, executionYear, academicTax) == null) {

            if (!PersonCustomer.findUnique(registration.getPerson()).isPresent()) {
                PersonCustomer.create(registration.getPerson());
            }

            final AcademicTariff academicTariff = AcademicTariff.findMatch(academicTax.getProduct(), registration.getDegree(), new DateTime());

            if (academicTariff == null) {
                return false;
            }

            if (!DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(),
                    PersonCustomer.findUnique(registration.getPerson()).get()).isPresent()) {

                DebtAccount.create(academicTariff.getFinantialEntity().getFinantialInstitution(),
                        PersonCustomer.findUnique(registration.getPerson()).get());
            }

            final DebtAccount debtAccount =
                    DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(),
                            PersonCustomer.findUnique(registration.getPerson()).get()).get();

            AcademicTreasuryEvent.createForAcademicTax(debtAccount, academicTax, registration, executionYear);
        }
        

        final AcademicTreasuryEvent academicTreasuryEvent = findAcademicTreasuryEvent(registration, executionYear, academicTax);
        
        if (academicTreasuryEvent.isChargedWithDebitEntry()) {
            return false;
        }
        
        final AcademicTariff academicTariff = AcademicTariff.findMatch(academicTax.getProduct(), registration.getDegree(), new DateTime());
        
        if (academicTariff == null) {
            return false;
        }
        
        academicTariff.createDebitEntry(academicTreasuryEvent);

        return true;
    }
    
    public static Set<Enrolment> normalEnrolments(final Registration registration, final ExecutionYear executionYear) {
        final Set<Enrolment> result = Sets.newHashSet(registration.getEnrolments(executionYear));

        result.removeAll(registration.getStudentCurricularPlan(executionYear).getStandaloneCurriculumLines().stream()
                .filter(l -> l.getExecutionYear() == executionYear && l.isEnrolment()).collect(Collectors.toList()));

        result.removeAll(registration.getStudentCurricularPlan(executionYear).getExtraCurricularCurriculumLines().stream()
                .filter(l -> l.getExecutionYear() == executionYear && l.isEnrolment()).collect(Collectors.toList()));

        return result;
    }

    public static Set<Enrolment> standaloneEnrolments(final Registration registration, final ExecutionYear executionYear) {
        return registration.getStudentCurricularPlan(executionYear).getStandaloneCurriculumLines().stream()
                .filter(l -> l.getExecutionYear() == executionYear && l.isEnrolment()).map(l -> (Enrolment) l)
                .collect(Collectors.<Enrolment> toSet());
    }

    public static Set<Enrolment> extracurricularEnrolments(final Registration registration, final ExecutionYear executionYear) {
        return registration.getStudentCurricularPlan(executionYear).getExtraCurricularCurriculumLines().stream()
                .filter(l -> l.getExecutionYear() == executionYear && l.isEnrolment()).map(l -> (Enrolment) l)
                .collect(Collectors.<Enrolment> toSet());
    }

    
}
