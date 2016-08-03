package org.fenixedu.academictreasury.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.treasury.IAcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.academictreasury.dto.academictax.AcademicTaxDebitEntryBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Vat;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.DebitNote;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

public class AcademicTaxServices {

    public static List<IAcademicTreasuryEvent> findAllTreasuryEventsForAcademicTaxes(final Registration registration,
            final ExecutionYear executionYear) {
        return AcademicTreasuryEvent.findAllForAcademicTax(registration, executionYear)
                .collect(Collectors.<IAcademicTreasuryEvent> toList());
    }

    public static AcademicTreasuryEvent findAcademicTreasuryEvent(final Registration registration,
            final ExecutionYear executionYear, final AcademicTax academicTax) {
        return AcademicTreasuryEvent.findUniqueForAcademicTax(registration, executionYear, academicTax).orElse(null);
    }

    public static AcademicTariff findAcademicTariff(final AcademicTax academicTax, final Registration registration,
            final LocalDate debtDate) {
        return AcademicTariff.findMatch(academicTax.getProduct(), registration.getDegree(), debtDate.toDateTimeAtStartOfDay());
    }

    @Atomic
    public static AcademicTaxDebitEntryBean calculateAcademicTax(final Registration registration,
            final ExecutionYear executionYear, final AcademicTax academicTax, final LocalDate debtDate,
            final boolean forceCreation) {

        if (!forceCreation && TuitionServices.normalEnrolments(registration, executionYear).isEmpty()) {
            return null;
        }

        if (!isAppliableOnRegistration(academicTax, registration, executionYear)) {
            return null;
        }

        if (findAcademicTreasuryEvent(registration, executionYear, academicTax) == null) {

            if (!PersonCustomer.findUnique(registration.getPerson()).isPresent()) {
                PersonCustomer.create(registration.getPerson());
            }

            final AcademicTariff academicTariff = findAcademicTariff(academicTax, registration, debtDate);

            if (academicTariff == null) {
                return null;
            }

            if (!DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(),
                    PersonCustomer.findUnique(registration.getPerson()).get()).isPresent()) {

                DebtAccount.create(academicTariff.getFinantialEntity().getFinantialInstitution(),
                        PersonCustomer.findUnique(registration.getPerson()).get());
            }

            final DebtAccount debtAccount = DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(),
                    PersonCustomer.findUnique(registration.getPerson()).get()).get();

            AcademicTreasuryEvent.createForAcademicTax(debtAccount, academicTax, registration, executionYear);
        }

        final AcademicTreasuryEvent academicTreasuryEvent = findAcademicTreasuryEvent(registration, executionYear, academicTax);

        final AcademicTariff academicTariff =
                AcademicTariff.findMatch(academicTax.getProduct(), registration.getDegree(), debtDate.toDateTimeAtStartOfDay());

        final LocalizedString debitEntryName = academicTariff.academicTaxDebitEntryName(academicTreasuryEvent);
        final LocalDate dueDate = academicTariff.dueDate(debtDate);
        final Vat vat = academicTariff.vat(debtDate);
        final BigDecimal amount = academicTariff.amountToPay(academicTreasuryEvent);

        return new AcademicTaxDebitEntryBean(debitEntryName, dueDate, vat.getTaxRate(), amount);
    }

    @Atomic
    public static boolean createAcademicTax(final Registration registration, final ExecutionYear executionYear,
            final AcademicTax academicTax, final boolean forceCreation) {
        return createAcademicTax(registration, executionYear, academicTax, new LocalDate(), forceCreation);
    }

    @Atomic
    public static boolean createAcademicTax(final Registration registration, final ExecutionYear executionYear,
            final AcademicTax academicTax, final LocalDate when, final boolean forceCreation) {
        if (!forceCreation && TuitionServices.normalEnrolments(registration, executionYear).isEmpty()) {
            return false;
        }

        if (!isAppliableOnRegistration(academicTax, registration, executionYear)) {
            return false;
        }

        if (findAcademicTreasuryEvent(registration, executionYear, academicTax) == null) {

            if (!PersonCustomer.findUnique(registration.getPerson()).isPresent()) {
                PersonCustomer.create(registration.getPerson());
            }

            final AcademicTariff academicTariff = findAcademicTariff(academicTax, registration, when);

            if (academicTariff == null) {
                return false;
            }

            if (!DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(),
                    PersonCustomer.findUnique(registration.getPerson()).get()).isPresent()) {

                DebtAccount.create(academicTariff.getFinantialEntity().getFinantialInstitution(),
                        PersonCustomer.findUnique(registration.getPerson()).get());
            }

            final DebtAccount debtAccount = DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(),
                    PersonCustomer.findUnique(registration.getPerson()).get()).get();

            AcademicTreasuryEvent.createForAcademicTax(debtAccount, academicTax, registration, executionYear);
        }

        final AcademicTreasuryEvent academicTreasuryEvent = findAcademicTreasuryEvent(registration, executionYear, academicTax);

        if (academicTreasuryEvent.isChargedWithDebitEntry()) {
            return false;
        }

        final AcademicTariff academicTariff =
                AcademicTariff.findMatch(academicTax.getProduct(), registration.getDegree(), when.toDateTimeAtStartOfDay());

        if (academicTariff == null) {
            return false;
        }

        academicTariff.createDebitEntryForAcademicTax(academicTreasuryEvent, when);

        return true;
    }

    public static boolean isRegistrationFirstYear(final Registration registration, final ExecutionYear executionYear) {
        return registration.getRegistrationYear() == executionYear;
    }

    public static boolean isRegistrationSubsequentYear(final Registration registration, final ExecutionYear executionYear) {
        return registration.getRegistrationYear().isBefore(executionYear);
    }

    public static boolean isAppliableOnRegistration(final AcademicTax academicTax, final Registration registration,
            final ExecutionYear executionYear) {
        return (isRegistrationFirstYear(registration, executionYear) && academicTax.isAppliedOnRegistrationFirstYear())
                || (isRegistrationSubsequentYear(registration, executionYear)
                        && academicTax.isAppliedOnRegistrationSubsequentYears());
    }

    /* ***********
     * IMPROVEMENT
     * ***********
     */

    public static AcademicTreasuryEvent findAcademicTreasuryEventForImprovementTax(final Registration registration,
            final ExecutionYear executionYear) {
        return AcademicTreasuryEvent.findUniqueForImprovementTuition(registration, executionYear).orElse(null);
    }

    public static AcademicTariff findAcademicTariff(final EnrolmentEvaluation enrolmentEvaluation, final LocalDate debtDate) {
        final Registration registration = enrolmentEvaluation.getRegistration();

        return AcademicTariff.findMatch(AcademicTreasurySettings.getInstance().getImprovementAcademicTax().getProduct(),
                registration.getDegree(), debtDate.toDateTimeAtStartOfDay());
    }

    @Atomic
    public static AcademicTaxDebitEntryBean calculateImprovementTax(final EnrolmentEvaluation enrolmentEvaluation,
            final LocalDate debtDate) {
        if (!enrolmentEvaluation.getEvaluationSeason().isImprovement()) {
            throw new AcademicTreasuryDomainException("error.AcademicTaxServices.enrolmentEvaluation.is.not.improvement");
        }

        final AcademicTax improvementAcademicTax = AcademicTreasurySettings.getInstance().getImprovementAcademicTax();

        if (improvementAcademicTax == null) {
            return null;
        }

        final Registration registration = enrolmentEvaluation.getRegistration();
        final ExecutionYear executionYear = enrolmentEvaluation.getExecutionPeriod().getExecutionYear();

        if (findAcademicTreasuryEventForImprovementTax(registration, executionYear) == null) {
            final Person person = registration.getPerson();

            if (!PersonCustomer.findUnique(person).isPresent()) {
                PersonCustomer.create(person);
            }

            final PersonCustomer personCustomer = PersonCustomer.findUnique(person).get();

            final AcademicTariff academicTariff = findAcademicTariff(enrolmentEvaluation, debtDate);

            if (academicTariff == null) {
                return null;
            }

            if (!DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(), personCustomer)
                    .isPresent()) {
                DebtAccount.create(academicTariff.getFinantialEntity().getFinantialInstitution(), personCustomer);
            }

            final DebtAccount debtAccount =
                    DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(), personCustomer).get();

            AcademicTreasuryEvent.createForImprovementTuition(debtAccount, registration, executionYear);
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                findAcademicTreasuryEventForImprovementTax(registration, executionYear);

        if (academicTreasuryEvent.isChargedWithDebitEntry(enrolmentEvaluation)) {
            return null;
        }

        final AcademicTariff academicTariff = AcademicTariff.findMatch(improvementAcademicTax.getProduct(),
                registration.getDegree(), debtDate.toDateTimeAtStartOfDay());

        if (academicTariff == null) {
            return null;
        }

        final LocalizedString debitEntryName =
                academicTariff.improvementDebitEntryName(academicTreasuryEvent, enrolmentEvaluation);
        final LocalDate dueDate = academicTariff.dueDate(debtDate);
        final Vat vat = academicTariff.vat(debtDate);
        final BigDecimal amount = academicTariff.amountToPay(academicTreasuryEvent, enrolmentEvaluation);

        return new AcademicTaxDebitEntryBean(debitEntryName, dueDate, vat.getTaxRate(), amount);
    }

    @Atomic
    public static boolean createImprovementTax(final EnrolmentEvaluation enrolmentEvaluation, final LocalDate when) {

        if (!enrolmentEvaluation.getEvaluationSeason().isImprovement()) {
            throw new AcademicTreasuryDomainException("error.AcademicTaxServices.enrolmentEvaluation.is.not.improvement");
        }

        final Registration registration = enrolmentEvaluation.getRegistration();
        final ExecutionYear executionYear = enrolmentEvaluation.getExecutionPeriod().getExecutionYear();

        AcademicTreasurySettings instance = AcademicTreasurySettings.getInstance();

        if (instance == null) {
            return false;
        }

        final AcademicTax improvementAcademicTax = instance.getImprovementAcademicTax();

        if (improvementAcademicTax == null) {
            return false;
        }

        if (findAcademicTreasuryEventForImprovementTax(registration, executionYear) == null) {
            final Person person = registration.getPerson();

            if (!PersonCustomer.findUnique(person).isPresent()) {
                PersonCustomer.create(person);
            }

            final PersonCustomer personCustomer = PersonCustomer.findUnique(person).get();

            final AcademicTariff academicTariff = AcademicTariff.findMatch(improvementAcademicTax.getProduct(),
                    registration.getDegree(), when.toDateTimeAtStartOfDay());

            if (academicTariff == null) {
                return false;
            }

            if (!DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(), personCustomer)
                    .isPresent()) {
                DebtAccount.create(academicTariff.getFinantialEntity().getFinantialInstitution(), personCustomer);
            }

            final DebtAccount debtAccount =
                    DebtAccount.findUnique(academicTariff.getFinantialEntity().getFinantialInstitution(), personCustomer).get();

            AcademicTreasuryEvent.createForImprovementTuition(debtAccount, registration, executionYear);
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                findAcademicTreasuryEventForImprovementTax(registration, executionYear);

        if (academicTreasuryEvent.isChargedWithDebitEntry(enrolmentEvaluation)) {
            return false;
        }

        final AcademicTariff academicTariff = AcademicTariff.findMatch(improvementAcademicTax.getProduct(),
                registration.getDegree(), when.toDateTimeAtStartOfDay());

        if (academicTariff == null) {
            return false;
        }

        final DebitEntry debitEntry = academicTariff.createDebitEntryForImprovement(academicTreasuryEvent, enrolmentEvaluation);

        return debitEntry != null;
    }

    public static boolean removeDebitEntryForImprovement(final EnrolmentEvaluation improvementEnrolmentEvaluation) {
        final Registration registration = improvementEnrolmentEvaluation.getRegistration();
        final ExecutionYear executionYear = improvementEnrolmentEvaluation.getExecutionPeriod().getExecutionYear();

        if (findAcademicTreasuryEventForImprovementTax(registration, executionYear) == null) {
            return false;
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                findAcademicTreasuryEventForImprovementTax(registration, executionYear);

        if (!academicTreasuryEvent.isChargedWithDebitEntry(improvementEnrolmentEvaluation)) {
            return false;
        }

        final DebitEntry debitEntry =
                academicTreasuryEvent.findActiveEnrolmentEvaluationDebitEntry(improvementEnrolmentEvaluation).get();

        final DebitNote debitNote = (DebitNote) debitEntry.getFinantialDocument();

        if (!debitEntry.isProcessedInDebitNote()) {
            debitEntry.annulDebitEntry(Constants.bundle("label.AcademicTaxServices.removeDebitEntryForImprovement.reason"));

//            debitEntry.setCurricularCourse(null);
//            debitEntry.setExecutionSemester(null);
//            debitEntry.setEvaluationSeason(null);

            return true;
        } else if (debitEntry.getCreditEntriesSet().isEmpty()) {
            debitNote.anullDebitNoteWithCreditNote(
                    Constants.bundle("label.AcademicTaxServices.removeDebitEntryForImprovement.reason"), false);
            return true;
        }

        return false;
    }

    /* ----------
     * ENROLMENTS
     * ----------
     */
}
