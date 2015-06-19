package org.fenixedu.academictreasury.services;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.tuition.TuitionInstallmentTariff;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.dto.tuition.TuitionDebitEntryBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Vat;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.CreditEntry;
import org.fenixedu.treasury.domain.document.CreditNote;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.DebitNote;
import org.fenixedu.treasury.domain.document.DocumentNumberSeries;
import org.fenixedu.treasury.domain.document.FinantialDocumentType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class TuitionServices {

    public static AcademicTreasuryEvent findAcademicTreasuryEventTuitionForRegistration(final Registration registration,
            final ExecutionYear executionYear) {
        return AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, executionYear).orElse(null);
    }

    @Atomic
    public static boolean createInferedTuitionForRegistration(final Registration registration, final ExecutionYear executionYear,
            final LocalDate when) {
        return createTuitionForRegistration(registration, executionYear, when, null);
    }

    @Atomic
    public static boolean createTuitionForRegistration(final Registration registration, final ExecutionYear executionYear,
            final LocalDate when, TuitionPaymentPlan tuitionPaymentPlan) {

        if(normalEnrolments(registration, executionYear).isEmpty()) {
            return false;
        }
        
        final Person person = registration.getPerson();
        // Read person customer

        if (!PersonCustomer.findUnique(person).isPresent()) {
            PersonCustomer.create(person);
        }

        final PersonCustomer personCustomer = PersonCustomer.findUnique(person).get();

        if (tuitionPaymentPlan == null) {
            tuitionPaymentPlan = TuitionPaymentPlan.inferTuitionPaymentPlanForRegistration(registration, executionYear);
        }

        if (tuitionPaymentPlan == null) {
            return false;
        }

        if (!DebtAccount.findUnique(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer)
                .isPresent()) {
            DebtAccount.create(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer);
        }

        final DebtAccount debtAccount =
                DebtAccount.findUnique(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer).get();

        if (!AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, executionYear).isPresent()) {
            AcademicTreasuryEvent.createForRegistrationTuition(debtAccount, tuitionPaymentPlan.getProduct(), registration,
                    executionYear);
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, executionYear).get();

        return tuitionPaymentPlan.createDebitEntriesForRegistration(academicTreasuryEvent, when);
    }

    public static TuitionPaymentPlan usedPaymentPlan(final Registration registration, final ExecutionYear executionYear,
            final LocalDate debtDate) {
        return usedPaymentPlan(registration, executionYear, debtDate, null);
    }

    public static TuitionPaymentPlan usedPaymentPlan(final Registration registration, final ExecutionYear executionYear,
            final LocalDate debtDate, final TuitionPaymentPlan tuitionPaymentPlan) {
        if (tuitionPaymentPlan != null) {
            return tuitionPaymentPlan;
        }

        return TuitionPaymentPlan.inferTuitionPaymentPlanForRegistration(registration, executionYear);
    }

    @Atomic
    public static List<TuitionDebitEntryBean> calculateInstallmentDebitEntryBeans(final Registration registration,
            final ExecutionYear executionYear, final LocalDate debtDate) {
        return calculateInstallmentDebitEntryBeans(registration, executionYear, debtDate, null);
    }

    @Atomic
    public static List<TuitionDebitEntryBean> calculateInstallmentDebitEntryBeans(final Registration registration,
            final ExecutionYear executionYear, final LocalDate debtDate, TuitionPaymentPlan tuitionPaymentPlan) {

        final Person person = registration.getPerson();
        // Read person customer
        PersonCustomer personCustomer = PersonCustomer.findUnique(person).orElse(null);

        if (personCustomer == null) {
            personCustomer = PersonCustomer.create(person);
        }

        if (tuitionPaymentPlan == null) {
            tuitionPaymentPlan = TuitionPaymentPlan.inferTuitionPaymentPlanForRegistration(registration, executionYear);
        }

        if (tuitionPaymentPlan == null) {
            return Lists.newArrayList();
        }

        if (!DebtAccount.findUnique(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer)
                .isPresent()) {
            DebtAccount.create(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer);
        }

        final DebtAccount debtAccount =
                DebtAccount.findUnique(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer).get();

        if (!AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, executionYear).isPresent()) {
            AcademicTreasuryEvent.createForRegistrationTuition(debtAccount, tuitionPaymentPlan.getProduct(), registration,
                    executionYear);
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, executionYear).get();

        final List<TuitionDebitEntryBean> entries = Lists.newArrayList();
        for (final TuitionInstallmentTariff tuitionInstallmentTariff : tuitionPaymentPlan.getTuitionInstallmentTariffsSet()) {
            final int installmentOrder = tuitionInstallmentTariff.getInstallmentOrder();
            final LocalizedString installmentName = tuitionPaymentPlan.installmentName(tuitionInstallmentTariff);
            final LocalDate dueDate = tuitionInstallmentTariff.dueDate(debtDate);
            final Vat vat = tuitionInstallmentTariff.vat(debtDate);
            final BigDecimal amount = tuitionInstallmentTariff.amountToPay(academicTreasuryEvent);

            entries.add(new TuitionDebitEntryBean(installmentOrder, installmentName, dueDate, vat.getTaxRate(), amount));
        }

        return entries.stream().sorted(new Comparator<TuitionDebitEntryBean>() {

            @Override
            public int compare(TuitionDebitEntryBean o1, TuitionDebitEntryBean o2) {
                return o1.getInstallmentOrder() - o2.getInstallmentOrder();
            }

        }).collect(Collectors.toList());
    }

    /* **********
     * Standalone 
     * **********
     */
    
    public static AcademicTreasuryEvent findAcademicTreasuryEventTuitionForStandalone(final Registration registration, final ExecutionYear executionYear) {
        return AcademicTreasuryEvent.findUniqueForStandaloneTuition(registration, executionYear).orElse(null);
    }

    public static boolean createInferedTuitionForStandalone(final Enrolment standaloneEnrolment, final LocalDate when) {
        return createInferedTuitionForStandalone(Sets.newHashSet(standaloneEnrolment), when);
    }

    public static boolean createInferedTuitionForStandalone(final Set<Enrolment> standaloneEnrolments, final LocalDate when) {

        boolean created = false;

        // Validate all enrolments are standalone

        for (final Enrolment standaloneEnrolment : standaloneEnrolments) {
            if (!standaloneEnrolment.isStandalone()) {
                throw new AcademicTreasuryDomainException("error.TuitionServices.enrolment.is.not.standalone");
            }
        }

        for (final Enrolment standaloneEnrolment : standaloneEnrolments) {
            final Registration registration = standaloneEnrolment.getRegistration();

            final Person person = registration.getPerson();
            // Read person customer

            if (!PersonCustomer.findUnique(person).isPresent()) {
                PersonCustomer.create(person);
            }

            final PersonCustomer personCustomer = PersonCustomer.findUnique(person).get();

            final ExecutionYear executionYear = standaloneEnrolment.getExecutionYear();

            if (TuitionPaymentPlan
                    .inferTuitionPaymentPlanForStandaloneEnrolment(registration, executionYear, standaloneEnrolment) == null) {
                continue;
            }

            final TuitionPaymentPlan tuitionPaymentPlan =
                    TuitionPaymentPlan.inferTuitionPaymentPlanForStandaloneEnrolment(registration, executionYear,
                            standaloneEnrolment);

            if (!DebtAccount.findUnique(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer)
                    .isPresent()) {
                DebtAccount.create(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer);
            }

            final DebtAccount debtAccount =
                    DebtAccount.findUnique(tuitionPaymentPlan.getFinantialEntity().getFinantialInstitution(), personCustomer)
                            .get();

            if (!AcademicTreasuryEvent.findUniqueForStandaloneTuition(registration, executionYear).isPresent()) {
                AcademicTreasuryEvent.createForStandaloneTuition(debtAccount, tuitionPaymentPlan.getProduct(), registration,
                        executionYear);
            }

            final AcademicTreasuryEvent academicTreasuryEvent =
                    AcademicTreasuryEvent.findUniqueForStandaloneTuition(registration, executionYear).get();

            if (academicTreasuryEvent.getDebtAccount().getFinantialInstitution() != tuitionPaymentPlan.getFinantialEntity()
                    .getFinantialInstitution()) {
                throw new AcademicTreasuryDomainException(
                        "error.TuitionServices.standalone.tuition.for.different.finantial.institutions.not.supported");
            }

            created |= tuitionPaymentPlan.createDebitEntriesForStandalone(academicTreasuryEvent, standaloneEnrolment, when);
        }

        return created;
    }

    public static boolean removeDebitEntryForStandaloneEnrolment(final Enrolment standaloneEnrolment) {
        final Registration registration = standaloneEnrolment.getRegistration();
        final ExecutionYear executionYear = standaloneEnrolment.getExecutionYear();

        if (!AcademicTreasuryEvent.findUniqueForStandaloneTuition(registration, executionYear).isPresent()) {
            return false;
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                AcademicTreasuryEvent.findUniqueForStandaloneTuition(registration, executionYear).get();

        if (!academicTreasuryEvent.isChargedWithDebitEntry(standaloneEnrolment)) {
            return false;
        }

        final DebitEntry debitEntry = academicTreasuryEvent.findActiveStandaloneDebitEntry(standaloneEnrolment).get();

        if (!debitEntry.isProcessedInDebitNote() || ((DebitNote) debitEntry.getFinantialDocument()).isPreparing()) {
            debitEntry.setCurricularCourse(null);
            debitEntry.setExecutionSemester(null);

            debitEntry.delete();

            return true;
        } else if (((DebitNote) debitEntry.getFinantialDocument()).isClosed()) {
            DebtAccount debtAccount = debitEntry.getDebtAccount();
            final CreditNote creditNote =
                    CreditNote.create(
                            debtAccount,
                            DocumentNumberSeries.findUniqueDefault(FinantialDocumentType.findForCreditNote(),
                                    debtAccount.getFinantialInstitution()).orElse(null), new DateTime(),
                            ((DebitNote) debitEntry.getFinantialDocument()), null);

            CreditEntry.create(creditNote, debitEntry.getDescription(), debitEntry.getProduct(), debitEntry.getVat(),
                    debitEntry.getAmount(), new DateTime(), debitEntry, BigDecimal.ONE);

            return true;
        }

        return false;
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
