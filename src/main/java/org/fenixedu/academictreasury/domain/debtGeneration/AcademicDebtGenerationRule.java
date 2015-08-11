package org.fenixedu.academictreasury.domain.debtGeneration;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
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
import org.fenixedu.treasury.domain.TreasuryOperationLog;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.DebitNote;
import org.fenixedu.treasury.domain.document.DocumentNumberSeries;
import org.fenixedu.treasury.domain.document.FinantialDocument;
import org.fenixedu.treasury.domain.document.FinantialDocumentType;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.treasury.domain.paymentcodes.FinantialDocumentPaymentCode;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class AcademicDebtGenerationRule extends AcademicDebtGenerationRule_Base {

    public static final String TREASURY_OPERATION_LOG_TYPE = "AcademicDebtGenerationRuleLog";

    public static Comparator<AcademicDebtGenerationRule> COMPARATOR_BY_EXECUTION_YEAR =
            new Comparator<AcademicDebtGenerationRule>() {

                @Override
                public int compare(AcademicDebtGenerationRule o1, AcademicDebtGenerationRule o2) {
                    int c = o1.getExecutionYear().compareTo(o2.getExecutionYear());

                    return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
                }
            };

    private static class LogBean {
        private DateTime processDate = new DateTime();
        private final StringBuilder log = new StringBuilder();

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

        public void registerCreatedTuition(Registration registration) {
            log.append(String.format("Created tuition for student '%s' [%s]", registration.getStudent().getNumber(),
                    registration.getDegreeName()));
            log.append("\n");
        }

        public void registerCreatedPaymentReference(Registration registration, FinantialDocumentPaymentCode paymentCode) {
            log.append(String.format("Created payment code '%s' [%s] with reference: '%s'",
                    registration.getStudent().getNumber(), registration.getDegreeName(), paymentCode.getPaymentReferenceCode()
                            .getReferenceCode()));
            log.append("\n");
        }

        public void registerAllWithClosedDebitNote(Registration registration) {
            log.append(String.format("All are in the same debit note on student '%s' [%s]",
                    registration.getStudent().getNumber(), registration.getDegreeName()));
            log.append("\n");
        }

        public void registerDebitEntriesWithDifferentClosedDebitNotes(Registration registration, DebitNote debitNote) {
            log.append(String.format("Debit entries with different debit notes on student '%s' [%s]", registration.getStudent()
                    .getNumber(), registration.getDegreeName()));
            log.append("\n");
        }

        public void registerWithoutDebitEntriesToProcess(Registration registration) {
            log.append(String.format("Without debit entries to process on student '%s' [%s]", registration.getStudent()
                    .getNumber(), registration.getDegreeName()));
            log.append("\n");
        }

        public void registerStudentNotActiveInExecutionYear(final Registration registration, final ExecutionYear executionYear) {
            log.append(String.format("Student not active '%s' [%s - %s]", registration.getStudent()
                    .getNumber(), registration.getDegreeName(), executionYear.getQualifiedName()));
            log.append("\n");            
        }

        public void registerStudentWithNoEnrolments(final Registration registration, final ExecutionYear executionYear) {
            log.append(String.format("Student with no enrolments '%s' [%s - %s]", registration.getStudent()
                    .getNumber(), registration.getDegreeName(), executionYear.getQualifiedName()));
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

        setExecutionYear(bean.getExecutionYear());
        setAggregateOnDebitNote(bean.isAggregateOnDebitNote());
        setAggregateAllOrNothing(bean.isAggregateAllOrNothing());
        setCloseDebitNote(bean.isCloseDebitNote());
        setCreatePaymentReferenceCode(bean.isCreatePaymentReferenceCode());
        setPaymentCodePool(bean.getPaymentCodePool());

        for (final ProductEntry productEntry : bean.getEntries()) {
            AcademicDebtGenerationRuleEntry.create(this, productEntry.getProduct(), productEntry.isCreateDebt());
        }
        
        getDegreeCurricularPlansSet().addAll((bean.getDegreeCurricularPlans()));

        checkRules();
    }

    private void checkRules() {
        if (getBennu() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.bennu.required");
        }

        if (getExecutionYear() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.executionYear.required");
        }

        if (isCloseDebitNote() && !isAggregateOnDebitNote()) {
            throw new AcademicTreasuryDomainException(
                    "error.AcademicDebtGenerationRule.closeDebitNote.requires.aggregateOnDebitNote");
        }

        if (isAggregateAllOrNothing() && !isAggregateOnDebitNote()) {
            throw new AcademicTreasuryDomainException(
                    "error.AcademicDebtGenerationRule.aggregateAllOrNothing.requires.aggregateOnDebitNote");
        }

        if (isCreatePaymentReferenceCode() && !isCloseDebitNote()) {
            throw new AcademicTreasuryDomainException(
                    "error.AcademicDebtGenerationRule.createPaymentReferenceCode.requires.closeDebitNote");
        }

        if (getAcademicDebtGenerationRuleEntriesSet().isEmpty()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.entries.required");
        }

        if (isCreatePaymentReferenceCode() && getPaymentCodePool() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.paymentCodePool.required");
        }
        
        if(getDegreeCurricularPlansSet().isEmpty()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.degreeCurricularPlans.required");
        }
        
        for (final DegreeCurricularPlan degreeCurricularPlan : getDegreeCurricularPlansSet()) {
            if(ExecutionDegree.getByDegreeCurricularPlanAndExecutionYear(degreeCurricularPlan, getExecutionYear()) == null) {
                throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.degreeCurricularPlan.not.active", degreeCurricularPlan.getName());
            }
        }
        
    }

    public boolean isActive() {
        return getActive();
    }

    public boolean isAggregateOnDebitNote() {
        return super.getAggregateOnDebitNote();
    }

    public boolean isAggregateAllOrNothing() {
        return super.getAggregateAllOrNothing();
    }

    public boolean isCloseDebitNote() {
        return super.getCloseDebitNote();
    }

    public boolean isCreatePaymentReferenceCode() {
        return super.getCreatePaymentReferenceCode();
    }

    private boolean isDeletable() {
        return true;
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        TreasuryDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        if (!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.delete.impossible");
        }

        setBennu(null);
        getDegreeCurricularPlansSet().clear();
        setExecutionYear(null);
        setPaymentCodePool(null);
        while (getAcademicDebtGenerationRuleEntriesSet().size() > 0) {
            getAcademicDebtGenerationRuleEntriesSet().iterator().next().delete();
        }

        super.deleteDomainObject();
    }

    @Atomic
    public void activate() {
        setActive(true);

        checkRules();
    }

    @Atomic
    public void inactivate() {
        setActive(false);

        checkRules();
    }

    @Atomic(mode = TxMode.READ)
    public void process() {
        if (!isActive()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.not.active.to.process");
        }

        System.out.println("AcademicDebtGenerationRule: Start");

        final LogBean logBean = new LogBean();
        logBean.processDate = new DateTime();

        for (final DegreeCurricularPlan degreeCurricularPlan : getDegreeCurricularPlansSet()) {
            for (final Registration registration : degreeCurricularPlan.getRegistrations()) {
                
                if(registration.getStudentCurricularPlan(getExecutionYear()) == null) {
                    continue;
                }
                
                if(!getDegreeCurricularPlansSet().contains(registration.getStudentCurricularPlan(getExecutionYear()).getDegreeCurricularPlan())) {
                    continue;
                }

                // Discard registrations not active and with no enrolments
                if (!registration.hasAnyActiveState(getExecutionYear()) || !registration.hasAnyEnrolmentsIn(getExecutionYear())) {
                    continue;
                }
                
                try {
                    processDebtsForRegistration(registration, logBean);
                } catch (final Exception e) {
                    e.printStackTrace();
                    logBean.registerException(registration, e);
                }
            }
        }
        
        writeLog(logBean);
    }
    
    @Atomic(mode = TxMode.READ)
    public void process(final Registration registration) {
        if (!isActive()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.not.active.to.process");
        }
        
        if(registration.getStudentCurricularPlan(getExecutionYear()) == null) {
            return;
        }
        
        if(!getDegreeCurricularPlansSet().contains(registration.getStudentCurricularPlan(getExecutionYear()).getDegreeCurricularPlan())) {
            return;
        }

        System.out.println("AcademicDebtGenerationRule: Start");

        final LogBean logBean = new LogBean();
        logBean.processDate = new DateTime();

        // Discard registrations not active and with no enrolments
        if(!registration.hasAnyActiveState(getExecutionYear())) {
            logBean.registerStudentNotActiveInExecutionYear(registration, getExecutionYear());
            writeLog(logBean);
            return;
        }
        
        if (!registration.hasAnyEnrolmentsIn(getExecutionYear())) {
            logBean.registerStudentWithNoEnrolments(registration, getExecutionYear());
            writeLog(logBean);
            return;
        }
        
        try {
            processDebtsForRegistration(registration, logBean);
        } catch (final Exception e) {
            e.printStackTrace();
            logBean.registerException(registration, e);
        }

    }

    @Atomic(mode = TxMode.WRITE)
    private void writeLog(final LogBean logBean) {
        TreasuryOperationLog.create(logBean.log.toString(), this.getExternalId(), TREASURY_OPERATION_LOG_TYPE);
    }

    @Atomic(mode = TxMode.WRITE)
    private void processDebtsForRegistration(final Registration registration, final LogBean logBean) {
        _processDebtsForRegistration(registration, logBean);
        registration.setBennuForPendingRegistrationsDebtCreation(null);
    }

    private void _processDebtsForRegistration(final Registration registration, final LogBean logBean) {
        final ExecutionYear executionYear = getExecutionYear();

        System.out.println(String.format("[AcademicDebtGenerationRule] processDebtsForRegistration for student '%d'",
                registration.getStudent().getNumber()));

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

        if (!isAggregateOnDebitNote()) {
            return;
        }

        if (debitEntries.isEmpty()) {
            logBean.registerWithoutDebitEntriesToProcess(registration);
            return;
        }

        DebitNote debitNote = null;
        if (isAllWithClosedDebitNote(debitEntries)) {
            logBean.registerAllWithClosedDebitNote(registration);

            if (!allWithTheSameClosedDebitNote(debitEntries)) {
                logBean.registerDebitEntriesWithDifferentClosedDebitNotes(registration, debitNote);
                return;
            }

            debitNote = (DebitNote) debitEntries.iterator().next().getFinantialDocument();
        } else {

            debitNote = grabPreparingOrCreateDebitEntry(registration, debitEntries, logBean);

            for (final DebitEntry debitEntry : debitEntries) {
                if (debitEntry.getFinantialDocument() == null) {
                    debitEntry.setFinantialDocument(debitNote);
                }
            }

            if (debitNote.getFinantialDocumentEntriesSet().isEmpty()) {
                throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.debit.note.without.debit.entries");
            }

            logBean.registerDebitEntriesOnDebitNote(registration, debitNote);

            if (debitNote.isPreparing() && isCloseDebitNote()) {
                debitNote.setDocumentDueDate(maxDebitEntryDueDate(debitNote));
                debitNote.closeDocument();
                logBean.registerDebitNoteClosing(registration, debitNote);
            }

            if (isAggregateAllOrNothing()) {
                for (final DebitEntry debitEntry : debitEntries) {
                    if (debitEntry.getFinantialDocument() != debitNote) {
                        throw new AcademicTreasuryDomainException(
                                "error.AcademicDebtGenerationRule.debit.entries.not.aggregated.on.same.debit.note");
                    }
                }

                // Check if all configured produts are in debitNote
                for (final Product product : getAcademicDebtGenerationRuleEntriesSet().stream()
                        .map(AcademicDebtGenerationRuleEntry::getProduct).collect(Collectors.toSet())) {
                    if (debitNote.getDebitEntries().filter(l -> l.getProduct() == product).count() == 0) {
                        throw new AcademicTreasuryDomainException(
                                "error.AcademicDebtGenerationRule.debit.entries.not.aggregated.on.same.debit.note");
                    }
                }
            }
        }

        if (debitNote.isClosed() && isCreatePaymentReferenceCode()
                && FinantialDocumentPaymentCode.findNewByFinantialDocument(debitNote).count() == 0) {
            final PaymentReferenceCode paymentReferenceCode =
                    getPaymentCodePool().getReferenceCodeGenerator().generateNewCodeFor(debitNote.getDebtAccount().getCustomer(),
                            debitNote.getOpenAmount(), new LocalDate(), debitNote.getDocumentDueDate(), true);

            FinantialDocumentPaymentCode paymentCode = FinantialDocumentPaymentCode.create(debitNote, paymentReferenceCode, true);

            logBean.registerCreatedPaymentReference(registration, paymentCode);
        }
    }

    private LocalDate maxDebitEntryDueDate(final DebitNote debitNote) {
        final LocalDate maxDate = debitNote.getDebitEntries().max(DebitEntry.COMPARE_BY_DUE_DATE).map(DebitEntry::getDueDate).orElse(new LocalDate());
        return maxDate.isAfter(new LocalDate()) ? maxDate : new LocalDate();
    }

    private boolean allWithTheSameClosedDebitNote(final Set<DebitEntry> debitEntries) {
        FinantialDocument finantialDocument = debitEntries.iterator().next().getFinantialDocument();

        if (finantialDocument == null || !finantialDocument.isClosed()) {
            throw new RuntimeException("Should not be here");
        }

        for (final DebitEntry debitEntry : debitEntries) {
            if (debitEntry.getFinantialDocument() != finantialDocument) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllWithClosedDebitNote(Set<DebitEntry> debitEntries) {
        for (final DebitEntry debitEntry : debitEntries) {
            if (debitEntry.getFinantialDocument() == null || !debitEntry.getFinantialDocument().isClosed()) {
                return false;
            }
        }

        return true;
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

        {
            AcademicTreasuryEvent t = AcademicTaxServices.findAcademicTreasuryEvent(registration, executionYear, academicTax);
            if (t == null || !t.isChargedWithDebitEntry()) {
                if (!entry.isCreateDebt()) {
                    return null;
                }
    
                if (AcademicTaxServices.createAcademicTax(registration, executionYear, academicTax)) {
                    logBean.registerCreatedAcademicTreasuryEvent(registration, academicTax);
                }
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
        boolean createdTuition = false;
        {
            AcademicTreasuryEvent t = TuitionServices.findAcademicTreasuryEventTuitionForRegistration(registration, executionYear);
            
            if (t == null || !t.isChargedWithDebitEntry(product)) {
    
                if (!entry.isCreateDebt()) {
                    return null;
                }
    
                final LocalDate enrolmentDate = TuitionServices.enrolmentDate(registration, executionYear, false);
                createdTuition = TuitionServices.createInferedTuitionForRegistration(registration, executionYear, enrolmentDate, false);
            }
        }

        if (TuitionServices.findAcademicTreasuryEventTuitionForRegistration(registration, executionYear) == null) {
            // Did not create exit with nothing
            return null;
        }

        if (createdTuition) {
            logBean.registerCreatedTuition(registration);
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

    public static Stream<AcademicDebtGenerationRule> findAll() {
        return Bennu.getInstance().getAcademicDebtGenerationRuleSet().stream();
    }

    public static Stream<AcademicDebtGenerationRule> findActive() {
        return findAll().filter(AcademicDebtGenerationRule::isActive);
    }

    @Atomic
    public static AcademicDebtGenerationRule create(final AcademicDebtGenerationRuleBean bean) {
        return new AcademicDebtGenerationRule(bean);
    }

    
    public static void runAllActive() {
        for (final AcademicDebtGenerationRule academicDebtGenerationRule : AcademicDebtGenerationRule.findActive().collect(Collectors.toSet())) {
            final RuleExecutor exec = new RuleExecutor(academicDebtGenerationRule);

            try {
                exec.start();
                exec.join();
            } catch (InterruptedException e) {
            }
        }
    }
    
    public static void runAllActiveForRegistration(final Registration registration) {
        for (final AcademicDebtGenerationRule academicDebtGenerationRule : AcademicDebtGenerationRule.findActive().collect(Collectors.toSet())) {
            final RuleExecutor exec = new RuleExecutor(academicDebtGenerationRule, registration);

            try {
                exec.start();
                exec.join();
            } catch (InterruptedException e) {
            }
        }        
    }
    
    // @formatter: off
    /**********
     * EXECUTOR
     **********
     */
    // @formatter: on

    
    public static final class RuleExecutor extends Thread {
        
        private String academicDebtGenerationRuleId;
        private String registrationId;
        
        public RuleExecutor(final AcademicDebtGenerationRule rule) {
            this.academicDebtGenerationRuleId = rule.getExternalId();
        }
        
        public RuleExecutor(final AcademicDebtGenerationRule rule, final Registration registration) {
            this.academicDebtGenerationRuleId = rule.getExternalId();
            this.registrationId = registration.getExternalId();
        }
        
        @Override
        public void run() {
            executeRule();
        }

        @Atomic(mode=TxMode.READ)
        private void executeRule() {
            final AcademicDebtGenerationRule rule = FenixFramework.getDomainObject(academicDebtGenerationRuleId);
            
            if(!Strings.isNullOrEmpty(registrationId)) {
                final Registration registration = FenixFramework.getDomainObject(registrationId);
                rule.process(registration);
            } else {
                rule.process();
            }
        }
    }
}
