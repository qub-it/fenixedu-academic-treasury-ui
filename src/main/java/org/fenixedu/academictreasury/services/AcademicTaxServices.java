package org.fenixedu.academictreasury.services;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class AcademicTaxServices {

    public static AcademicTreasuryEvent findAcademicTreasuryEvent(final Registration registration,
            final ExecutionYear executionYear, final AcademicTax academicTax) {
        return AcademicTreasuryEvent.findUniqueForAcademicTax(registration, executionYear, academicTax).orElse(null);
    }

    @Atomic
    public static boolean createAcademicTax(final Registration registration, final ExecutionYear executionYear, final AcademicTax academicTax) {
        if (!registration.isRegistered(executionYear)) {
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

            if (DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(),
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
        academicTariff.createDebitEntry(academicTreasuryEvent);

        return true;
    }
    
}
