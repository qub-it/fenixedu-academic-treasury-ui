package org.fenixedu.academictreasury.domain.debtGeneration;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.dto.debtGeneration.AcademicDebtGenerationRuleBean;
import org.fenixedu.academictreasury.dto.debtGeneration.AcademicDebtGenerationRuleBean.ProductEntry;
import org.fenixedu.academictreasury.services.AcademicTaxServices;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.DebitNote;
import org.fenixedu.treasury.domain.document.DocumentNumberSeries;
import org.fenixedu.treasury.domain.document.FinantialDocumentType;
import org.fenixedu.treasury.domain.paymentcodes.FinantialDocumentPaymentCode;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Sets;

public class AcademicDebtGenerationRule extends AcademicDebtGenerationRule_Base {

    private static class LogBean {
        private DateTime processDate = new DateTime();
        private StringBuilder log = new StringBuilder();

        private void registerException(final Registration registration, final Exception e) {
            log.append(String.format("The registration of student '%s' [%s] has the following exception: '%s'\n", registration
                    .getStudent().getNumber(), registration.getDegreeName(), e.getMessage()));
            log.append(ExceptionUtils.getStackTrace(e));
            log.append("\n");
        }

        public void registerCreatedAcademicTreasuryEvent(final Registration registration, final AcademicTax academicTax) {
            log.append(String.format("Created academic event treasury for student '%s' [%s] for academic tax: '%s'", registration
                    .getStudent().getNumber(), registration.getDegreeName(), academicTax.getProduct().getName().getContent()));
            log.append("\n");
        }

        public void registerDebitNoteCreation(Registration registration, DebitNote debitNote) {
            log.append(String.format("Created debit note for student '%s' [%s] on finantial institution: '%s'", registration
                    .getStudent().getNumber(), registration.getDegreeName(), debitNote.getDebtAccount().getFinantialInstitution()
                    .getName()));
            log.append("\n");
        }

        public void registerDebitNoteClosing(Registration registration, DebitNote debitNote) {
            log.append(String.format("Closing debit note for student '%s' [%s] on finantial institution: '%s'", registration
                    .getStudent().getNumber(), registration.getDegreeName(), debitNote.getDebtAccount().getFinantialInstitution()
                    .getName()));
            log.append("\n");
        }

        public void registerDebitEntriesOnDebitNote(Registration registration, DebitNote debitNote) {
            final String description =
                    String.join(
                            ", ",
                            debitNote.getFinantialDocumentEntriesSet().stream()
                                    .map(l -> ((DebitEntry) l).getProduct().getName().getContent()).collect(Collectors.toSet()));

            log.append(String.format("Closing debit note for student '%s' [%s] with entries: '%s'", registration.getStudent()
                    .getNumber(), registration.getDegreeName(), debitNote.getDebtAccount().getFinantialInstitution().getName(),
                    description));
            log.append("\n");
        }
    }

    protected AcademicDebtGenerationRule() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected AcademicDebtGenerationRule(final AcademicDebtGenerationRuleBean bean) {
        this();

        setActive(true);

        setAggregateOnDebitNote(bean.isAggregateOnDebitNote());
        setCloseDebitNote(bean.isCloseDebitNote());
        setCreatePaymentReferenceCode(bean.isCreatePaymentReferenceCode());

        for (final ProductEntry productEntry : bean.getEntries()) {
            AcademicDebtGenerationRuleEntry.create(this, productEntry.getProduct(), productEntry.isCreateDebt());
        }
    }

    public boolean isActive() {
        return getActive();
    }

    public boolean isAggregateOnDebitNote() {
        return super.getAggregateOnDebitNote();
    }

    public boolean isCloseDebitNote() {
        return super.getCloseDebitNote();
    }

    public boolean isCreatePaymentReferenceCode() {
        return super.getCreatePaymentReferenceCode();
    }

    @Atomic(mode = TxMode.READ)
    public void process() {
        if (!isActive()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.not.active.to.process");
        }

        final LogBean logBean = new LogBean();
        logBean.processDate = new DateTime();

        for (final Registration registration : Bennu.getInstance().getRegistrationsSet()) {

            // Discard registrations not active and with no enrolments
            if (!registration.isRegistered(getExecutionYear())) {
                continue;
            }

            try {
                processDebtsForRegistration(registration, logBean);
            } catch (final Exception e) {
                logBean.registerException(registration, e);
            }
        }

    }

    @Atomic(mode = TxMode.WRITE)
    private void processDebtsForRegistration(final Registration registration, final LogBean logBean) {
        final ExecutionYear executionYear = getExecutionYear();

        // For each product try to grab or create if requested
        final Set<DebitEntry> debitEntries = Sets.newHashSet();

        for (final AcademicDebtGenerationRuleEntry entry : getAcademicDebtGenerationRuleEntriesSet()) {
            final Product product = entry.getProduct();

            DebitEntry grabbedDebitEntry = null;
            // Check if the product is tuition kind
            if (AcademicTreasurySettings.getInstance().getTuitionProductGroup() == product.getProductGroup()) {
                grabbedDebitEntry = grabOrCreateDebitEntryForTuition(registration, entry, logBean);
            } else if (AcademicTax.findUnique(product).isPresent()) {
                // Check if the product is an academic tax
                grabbedDebitEntry = grabOrCreateDebitEntryForAcademicTax(registration, entry, logBean);
            } else {
                // Unable to handle this kind of product
                throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.unable.to.process.debt.for.product");
            }

            if (grabbedDebitEntry != null) {
                debitEntries.add(grabbedDebitEntry);
            }
        }

        if (!isCloseDebitNote()) {
            return;
        }

        if (debitEntries.isEmpty()) {
            throw new AcademicTreasuryDomainException(
                    "error.AcademicDebtGenerationRule.create.debit.note.but.empty.debit.entries");
        }

        final DebitNote debitNote = grabPreparingOrCreateDebitEntry(registration, debitEntries, logBean);

        for (final DebitEntry debitEntry : debitEntries) {
            if (debitEntry.getFinantialDocument() != null) {
                debitEntry.setFinantialDocument(debitNote);
            }
        }

        logBean.registerDebitEntriesOnDebitNote(registration, debitNote);

        if (isCloseDebitNote()) {
            debitNote.closeDocument();
            logBean.registerDebitNoteClosing(registration, debitNote);
        }

        if (FinantialDocumentPaymentCode.findNewByFinantialDocument(debitNote).count() == 0) {
            final PaymentReferenceCode paymentReferenceCode =
                    getPaymentCodePool().getReferenceCodeGenerator().generateNewCodeFor(debitNote.getDebtAccount().getCustomer(),
                            debitNote.getOpenAmount(), new LocalDate(), debitNote.getDocumentDueDate(), true);

            FinantialDocumentPaymentCode.create(debitNote, paymentReferenceCode, true);
        }
    }

    private DebitNote grabPreparingOrCreateDebitEntry(final Registration registration, final Set<DebitEntry> debitEntries,
            final LogBean logBean) {

        for (final DebitEntry debitEntry : debitEntries) {
            if (debitEntry.getFinantialDocument() != null && debitEntry.getFinantialDocument().isPreparing()) {
                return (DebitNote) debitEntry.getFinantialDocument();
            }
        }

        final DebitNote debitNote =
                DebitNote.create(
                        debitEntries.iterator().next().getDebtAccount(),
                        DocumentNumberSeries.findUniqueDefault(FinantialDocumentType.findForDebitNote(),
                                debitEntries.iterator().next().getDebtAccount().getFinantialInstitution()).get(), new DateTime());

        logBean.registerDebitNoteCreation(registration, debitNote);

        return debitNote;
    }

    private DebitEntry grabOrCreateDebitEntryForAcademicTax(final Registration registration,
            final AcademicDebtGenerationRuleEntry entry, final LogBean logBean) {
        final Product product = entry.getProduct();
        final ExecutionYear executionYear = getExecutionYear();
        final AcademicTax academicTax = AcademicTax.findUnique(product).get();

        if (AcademicTaxServices.findAcademicTreasuryEvent(registration, executionYear, academicTax) == null) {
            if (!entry.isCreateDebt()) {
                return null;
            }

            if (AcademicTaxServices.createAcademicTax(registration, executionYear, academicTax)) {
                logBean.registerCreatedAcademicTreasuryEvent(registration, academicTax);
            }
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                AcademicTaxServices.findAcademicTreasuryEvent(registration, executionYear, academicTax);

        if (academicTreasuryEvent != null && academicTreasuryEvent.isChargedWithDebitEntry()) {
            return DebitEntry.findActive(academicTreasuryEvent).findFirst().get();
        }

        return null;
    }

    private DebitEntry grabOrCreateDebitEntryForTuition(final Registration registration,
            final AcademicDebtGenerationRuleEntry entry, final LogBean logBean) {
        final Product product = entry.getProduct();
        final ExecutionYear executionYear = getExecutionYear();

        // Is of tuition kind try to catch the tuition event
        if (TuitionServices.findAcademicTreasuryEventTuitionForRegistration(registration, executionYear) == null) {

            if (!entry.isCreateDebt()) {
                return null;
            }

            TuitionServices.createInferedTuitionForRegistration(registration, executionYear, new LocalDate());
        }

        if (TuitionServices.findAcademicTreasuryEventTuitionForRegistration(registration, executionYear) == null) {
            // Did not create exit with nothing
            return null;
        }

        final AcademicTreasuryEvent academicTreasuryEvent =
                TuitionServices.findAcademicTreasuryEventTuitionForRegistration(registration, executionYear);

        if (!academicTreasuryEvent.isChargedWithDebitEntry(product)) {
            return null;
        }

        return DebitEntry.findActive(academicTreasuryEvent, product).findFirst().get();
    }
    
    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    public Stream<AcademicDebtGenerationRule> findAll() {
        return Bennu.getInstance().getAcademicDebtGenerationRuleSet().stream();
    }

    public AcademicDebtGenerationRule create(final AcademicDebtGenerationRuleBean bean) {
        return new AcademicDebtGenerationRule(bean);
    }
    
}
