package org.fenixedu.academictreasury.domain.event;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.accounting.CreditNoteEntry;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.treasury.IAcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.tuition.TuitionInstallmentTariff;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.CreditEntry;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.event.TreasuryEvent;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AcademicTreasuryEvent extends AcademicTreasuryEvent_Base implements IAcademicTreasuryEvent {

    protected AcademicTreasuryEvent(final DebtAccount debtAccount, final AcademicServiceRequest academicServiceRequest) {
        init(debtAccount, academicServiceRequest, ServiceRequestMapEntry.findProduct(academicServiceRequest));

        checkRules();
    }

    public AcademicTreasuryEvent(final DebtAccount debtAccount, final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final Product product, final Registration registration, final ExecutionYear executionYear) {
        init(debtAccount, tuitionPaymentPlanGroup, product, registration, executionYear);

        checkRules();
    }

    @Override
    protected void init(final DebtAccount debtAccount, final Product product) {
        throw new RuntimeException("wrong call");
    }

    protected void init(final DebtAccount debtAccount, final AcademicServiceRequest academicServiceRequest, final Product product) {
        super.init(debtAccount, product);

        setAcademicServiceRequest(academicServiceRequest);
        setPropertiesJsonMap(propertiesMapToJson(fillPropertiesMap()));

        checkRules();
    }

    protected void init(final DebtAccount debtAccount, final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final Product product, final Registration registration, final ExecutionYear executionYear) {
        super.init(debtAccount, product);

        setTuitionPaymentPlanGroup(tuitionPaymentPlanGroup);
        setRegistration(registration);
        setExecutionYear(executionYear);
        setPropertiesJsonMap(propertiesMapToJson(fillPropertiesMap()));

        checkRules();
    }

    @Override
    protected void checkRules() {
        super.checkRules();

        if (!isForAcademicServiceRequest() && !isForRegistrationTuition() && !isForStandaloneTuition()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.not.for.service.request.nor.tuition");
        }

        if ((isForRegistrationTuition() || isForStandaloneTuition()) && getRegistration() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.registration.required");
        }

        if ((isForRegistrationTuition() || isForStandaloneTuition()) && getExecutionYear() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.executionYear.required");
        }

        if (isForAcademicServiceRequest() && find(getAcademicServiceRequest()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.event.for.academicServiceRequest.duplicate");
        }

        if (isForRegistrationTuition() && findForRegistrationTuition(getRegistration(), getExecutionYear()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.for.registration.tuition.duplicate");
        }

        if (isForStandaloneTuition() && findForStandaloneTuition(getRegistration(), getExecutionYear()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.for.standalone.registration.duplicate");
        }

    }

    @Override
    public boolean isWithDebitEntry() {
        return isChargedWithDebitEntry();
    }

    public boolean isForAcademicServiceRequest() {
        return getAcademicServiceRequest() != null;
    }

    public boolean isForRegistrationTuition() {
        return getTuitionPaymentPlanGroup() != null && getTuitionPaymentPlanGroup().isForRegistration();
    }

    public boolean isForStandaloneTuition() {
        return getTuitionPaymentPlanGroup() != null && getTuitionPaymentPlanGroup().isForStandalone();
    }

    public int getNumberOfUnits() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getNumberOfUnits();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.numberOfUnits.not.applied");
    }

    public int getNumberOfPages() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getNumberOfPages();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.numberOfPages.not.applied");
    }

    public boolean isUrgentRequest() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().isUrgentRequest();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.urgentRequest.not.applied");
    }

    public LocalDate getRequestDate() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getRequestDate().toLocalDate();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.requestDate.not.applied");
    }

    public Locale getLanguage() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getLanguage();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.language.not.applied");
    }

    public boolean isChargedWithDebitEntry(final TuitionInstallmentTariff tariff) {
        return DebitEntry.findActive(this).filter(d -> d.getTariff() == tariff).count() > 0;
    }

    @Override
    public Set<Product> getPossibleProductsToExempt() {
        if (isForRegistrationTuition()) {
            return TuitionPaymentPlan
                    .find(getTuitionPaymentPlanGroup(),
                            getRegistration().getStudentCurricularPlan(getExecutionYear()).getDegreeCurricularPlan(),
                            getExecutionYear()).map(t -> t.getTuitionInstallmentTariffsSet()).reduce((a, b) -> Sets.union(a, b))
                    .orElse(Sets.newHashSet()).stream().map(i -> i.getProduct()).collect(Collectors.toSet());
        }

        return Sets.newHashSet(getProduct());
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    public static Stream<? extends AcademicTreasuryEvent> findAll() {
        return TreasuryEvent.findAll().filter(e -> e instanceof AcademicTreasuryEvent).map(AcademicTreasuryEvent.class::cast);
    }

    /* --- Academic Service Requests --- */

    public static Stream<? extends AcademicTreasuryEvent> find(final AcademicServiceRequest academicServiceRequest) {
        if (academicServiceRequest == null) {
            throw new RuntimeException("wrong call");
        }

        return findAll().filter(e -> e.getAcademicServiceRequest() == academicServiceRequest);
    }

    public static Optional<? extends AcademicTreasuryEvent> findUnique(final AcademicServiceRequest academicServiceRequest) {
        return find(academicServiceRequest).findFirst();
    }

    protected static Stream<? extends AcademicTreasuryEvent> findForRegistrationTuition(final Registration registration,
            final ExecutionYear executionYear) {
        return findAll()
                .filter(e -> e.isForRegistrationTuition() && e.getRegistration() == registration
                        && e.getExecutionYear() == executionYear);
    }

    public static Optional<? extends AcademicTreasuryEvent> findUniqueForRegistrationTuition(final Registration registration,
            final ExecutionYear executionYear) {
        return findForRegistrationTuition(registration, executionYear).findFirst();
    }

    protected static Stream<? extends AcademicTreasuryEvent> findForStandaloneTuition(final Registration registration,
            final ExecutionYear executionYear) {
        return findAll().filter(
                e -> e.getTuitionPaymentPlanGroup().isForStandalone() && e.getRegistration() == registration
                        && e.getExecutionYear() == executionYear);
    }

    public static Optional<? extends AcademicTreasuryEvent> findUniqueForStandaloneTuition(final Registration registration,
            final ExecutionYear executionYear) {
        return findForStandaloneTuition(registration, executionYear).findFirst();
    }

    public static AcademicTreasuryEvent createForAcademicServiceRequest(final DebtAccount debtAccount,
            final AcademicServiceRequest academicServiceRequest) {
        return new AcademicTreasuryEvent(debtAccount, academicServiceRequest);
    }

    public static AcademicTreasuryEvent createForRegistrationTuition(final DebtAccount debtAccount, final Product product,
            final Registration registration, final ExecutionYear executionYear) {
        return new AcademicTreasuryEvent(debtAccount, TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get(),
                product, registration, executionYear);
    }

    public static AcademicTreasuryEvent createForStandaloneTuition(final DebtAccount debtAccount, final Product product,
            final Registration registration, final ExecutionYear executionYear) {
        return new AcademicTreasuryEvent(debtAccount, TuitionPaymentPlanGroup.findUniqueDefaultGroupForStandalone().get(),
                product, registration, executionYear);
    }

    /* -----
     * UTILS
     * -----
     */

    // @formatter:off
    public static enum AcademicTreasuryEventKeys {
        ACADEMIC_SERVICE_REQUEST_NAME, 
        EXECUTION_YEAR, 
        DETAILED, 
        URGENT, 
        LANGUAGE, 
        BASE_AMOUNT, 
        UNITS_FOR_BASE, 
        UNIT_AMOUNT,
        ADDITIONAL_UNITS, 
        CALCULATED_UNITS_AMOUNT,
        PAGE_AMOUNT,
        NUMBER_OF_PAGES,
        CALCULATED_PAGES_AMOUNT,
        MAXIMUM_AMOUNT,
        AMOUNT_WITHOUT_RATES,
        FOREIGN_LANGUAGE_RATE,
        CALCULATED_FOREIGN_LANGUAGE_RATE,
        URGENT_PERCENTAGE,
        CALCULATED_URGENT_AMOUNT,
        FINAL_AMOUNT, 
        TUITION_CALCULATION_TYPE, 
        FIXED_AMOUNT, 
        ECTS_CREDITS, 
        AMOUNT_PER_ECTS, 
        ENROLLED_COURSES, 
        AMOUNT_PER_COURSE, 
        DUE_DATE,
        DEGREE,
        DEGREE_CURRICULAR_PLAN;

        public LocalizedString getDescriptionI18N() {
            return BundleUtil
                    .getLocalizedString(Constants.BUNDLE, "label." + AcademicTreasuryEventKeys.class.getSimpleName() + "." + name());
        }

    }
    // @formatter:on

    private Map<String, String> fillPropertiesMap() {
        final Map<String, String> propertiesMap = Maps.newHashMap();

        if (isForAcademicServiceRequest()) {
            propertiesMap.put(AcademicTreasuryEventKeys.ACADEMIC_SERVICE_REQUEST_NAME.getDescriptionI18N().getContent(),
                    ServiceRequestType.findUnique(getAcademicServiceRequest()).getName().getContent());

            if (getAcademicServiceRequest().hasExecutionYear()) {
                propertiesMap.put(AcademicTreasuryEventKeys.EXECUTION_YEAR.getDescriptionI18N().getContent(),
                        getAcademicServiceRequest().getExecutionYear().getQualifiedName());
            }

            propertiesMap.put(AcademicTreasuryEventKeys.DETAILED.getDescriptionI18N().getContent(),
                    booleanLabel(getAcademicServiceRequest().isDetailed()).getContent());
            propertiesMap.put(AcademicTreasuryEventKeys.URGENT.getDescriptionI18N().getContent(),
                    booleanLabel(getAcademicServiceRequest().isUrgentRequest()).getContent());
            propertiesMap.put(AcademicTreasuryEventKeys.LANGUAGE.getDescriptionI18N().getContent(), getAcademicServiceRequest()
                    .getLanguage().getLanguage());
            propertiesMap.put(AcademicTreasuryEventKeys.BASE_AMOUNT.getDescriptionI18N().getContent(),
                    getAcademicServiceRequest().getLanguage().getLanguage());
        } else if (isForRegistrationTuition() || isForStandaloneTuition()) {
            propertiesMap.put(AcademicTreasuryEventKeys.EXECUTION_YEAR.getDescriptionI18N().getContent(), getExecutionYear()
                    .getQualifiedName());
            propertiesMap.put(AcademicTreasuryEventKeys.DEGREE.getDescriptionI18N().getContent(), getRegistration().getDegree()
                    .getPresentationNameI18N(getExecutionYear()).getContent());
            propertiesMap.put(AcademicTreasuryEventKeys.DEGREE_CURRICULAR_PLAN.getDescriptionI18N().getContent(),
                    getRegistration().getDegreeCurricularPlanName());
        }

        return propertiesMap;
    }

    private LocalizedString booleanLabel(final boolean detailed) {
        return BundleUtil.getLocalizedString(Constants.BUNDLE, detailed ? "label.yes" : "label.no");
    }

    @Override
    public BigDecimal getBaseAmount() {
        if (!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.baseAmount.unavailable");
        }

        return super.getBaseAmount();
    }

    @Override
    public BigDecimal getAdditionalUnitsAmount() {
        if (!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.additionalUnitsAmount.unavailable");
        }

        return super.getAmountForAdditionalUnits();
    }

    @Override
    public BigDecimal getMaximumAmount() {
        if (!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.maximumAmount.unavailable");
        }

        return super.getMaximumAmount();
    }

    @Override
    public BigDecimal getPagesAmount() {
        if (!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.pagesAmount.unavailable");
        }

        return super.getAmountForPages();
    }

    @Override
    public BigDecimal getAmountForLanguageTranslationRate() {
        if (!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.amountForLanguageTranslationRate.unavailable");
        }

        return super.getAmountForLanguageTranslationRate();
    }

    @Override
    public BigDecimal getAmountForUrgencyRate() {
        if (!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.amountForUrgencyRate.unavailable");
        }

        return super.getAmountForUrgencyRate();
    }

    public BigDecimal getEnrolledEctsUnits() {
        if (getTuitionPaymentPlanGroup().isForRegistration()) {
            final Set<Enrolment> normalEnrolments =
                    Sets.newHashSet(getRegistration().getStudentCurricularPlan(getExecutionYear()).getRoot()
                            .getEnrolmentsBy(getExecutionYear()));

            normalEnrolments.removeAll(getRegistration().getStandaloneCurriculumLines().stream()
                    .filter(l -> l.isEnrolment() && l.getExecutionYear() == getExecutionYear()).collect(Collectors.toSet()));

            normalEnrolments.removeAll(getRegistration().getExtraCurricularCurriculumLines().stream()
                    .filter(l -> l.isEnrolment() && l.getExecutionYear() == getExecutionYear()).collect(Collectors.toSet()));

            return normalEnrolments.stream().map(e -> new BigDecimal(e.getEctsCredits())).reduce((a, b) -> a.add(b))
                    .orElse(BigDecimal.ZERO);

        } else if (getTuitionPaymentPlanGroup().isForStandalone()) {
            return getRegistration().getStandaloneCurriculumLines().stream()
                    .filter(l -> l.isEnrolment() && l.getExecutionYear() == getExecutionYear())
                    .map(e -> new BigDecimal(e.getEctsCredits())).reduce((a, c) -> a.add(c)).orElse(BigDecimal.ZERO);
        } else if (getTuitionPaymentPlanGroup().isForExtracurricular()) {
            return getRegistration().getExtraCurricularCurriculumLines().stream()
                    .filter(l -> l.isEnrolment() && l.getExecutionYear() == getExecutionYear())
                    .map(e -> new BigDecimal(e.getEctsCredits())).reduce((a, c) -> a.add(c)).orElse(BigDecimal.ZERO);
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.unknown.tuition.group");
    }

    public BigDecimal getEnrolledCoursesCount() {
        if (getTuitionPaymentPlanGroup().isForRegistration()) {
            return new BigDecimal(getRegistration().getEnrolments(getExecutionYear()).size());
        } else if (getTuitionPaymentPlanGroup().isForStandalone()) {
            return new BigDecimal(getRegistration().getStandaloneCurriculumLines().stream()
                    .filter(l -> l.isEnrolment() && l.getExecutionYear() == getExecutionYear()).count());
        } else if (getTuitionPaymentPlanGroup().isForExtracurricular()) {
            return new BigDecimal(getRegistration().getExtraCurricularCurriculumLines().stream()
                    .filter(l -> l.isEnrolment() && l.getExecutionYear() == getExecutionYear()).count());
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.unknown.tuition.group");
    }

    public void updatePricingFields(final BigDecimal baseAmount, final BigDecimal amountForAdditionalUnits,
            final BigDecimal amountForPages, final BigDecimal maximumAmount, final BigDecimal amountForLanguageTranslationRate,
            final BigDecimal amountForUrgencyRate) {

        super.setBaseAmount(baseAmount);
        super.setAmountForAdditionalUnits(amountForAdditionalUnits);
        super.setAmountForPages(amountForPages);
        super.setMaximumAmount(maximumAmount);
        super.setAmountForLanguageTranslationRate(amountForLanguageTranslationRate);
        super.setAmountForUrgencyRate(amountForUrgencyRate);
    }

}
