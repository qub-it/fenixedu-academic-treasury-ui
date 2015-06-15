package org.fenixedu.academictreasury.domain.tuition;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.Vat;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;

public class TuitionInstallmentTariff extends TuitionInstallmentTariff_Base {

    public static final Comparator<? super TuitionInstallmentTariff> COMPARATOR_BY_INSTALLMENT_NUMBER =
            new Comparator<TuitionInstallmentTariff>() {

                @Override
                public int compare(final TuitionInstallmentTariff o1, final TuitionInstallmentTariff o2) {
                    int c = Integer.compare(o1.getInstallmentOrder(), o2.getInstallmentOrder());
                    return c != 0 ? c : DomainObjectUtil.COMPARATOR_BY_ID.compare(o1, o2);
                }
            };

    protected TuitionInstallmentTariff() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected TuitionInstallmentTariff(final FinantialEntity finantialEntity, final TuitionPaymentPlan tuitionPaymentPlan,
            final AcademicTariffBean bean) {
        this();

        this.init(finantialEntity, tuitionPaymentPlan, bean);
    }

    @Override
    protected void init(final FinantialEntity finantialEntity, final Product product, final DateTime beginDate,
            final DateTime endDate, final DueDateCalculationType dueDateCalculationType, final LocalDate fixedDueDate,
            final int numberOfDaysAfterCreationForDueDate, final boolean applyInterests, final InterestType interestType,
            final int numberOfDaysAfterDueDate, final boolean applyInFirstWorkday, final int maximumDaysToApplyPenalty,
            final int maximumMonthsToApplyPenalty, final BigDecimal interestFixedAmount, final BigDecimal rate) {
        throw new RuntimeException("wrong call");
    }

    protected void init(final FinantialEntity finantialEntity, final TuitionPaymentPlan tuitionPaymentPlan,
            final AcademicTariffBean bean) {

        final Product product =
                tuitionPaymentPlan.getTuitionPaymentPlanGroup().isForStandalone()
                        || tuitionPaymentPlan.getTuitionPaymentPlanGroup().isForExtracurricular() ? tuitionPaymentPlan
                        .getProduct() : bean.getTuitionInstallmentProduct();

        super.init(finantialEntity, product, bean.getBeginDate().toDateTimeAtStartOfDay(), bean.getEndDate() != null ? bean
                .getEndDate().toDateTimeAtStartOfDay() : null, bean.getDueDateCalculationType(), bean.getFixedDueDate(), bean
                .getNumberOfDaysAfterCreationForDueDate(), bean.isApplyInterests(), bean.getInterestType(), bean
                .getNumberOfDaysAfterDueDate(), bean.isApplyInFirstWorkday(), bean.getMaximumDaysToApplyPenalty(), bean
                .getMaximumMonthsToApplyPenalty(), bean.getInterestFixedAmount(), bean.getRate());

        super.setTuitionPaymentPlan(tuitionPaymentPlan);
        super.setInstallmentOrder(bean.getInstallmentOrder());
        super.setTuitionCalculationType(bean.getTuitionCalculationType());
        super.setFixedAmount(bean.getFixedAmount());
        super.setEctsCalculationType(bean.getEctsCalculationType());
        super.setFactor(bean.getFactor());
        super.setTotalEctsOrUnits(bean.getTotalEctsOrUnits());
        super.setAcademicalActBlockingOff(bean.isAcademicalActBlockingOff());

        checkRules();
    }

    @Override
    protected void checkRules() {
        super.checkRules();

        if (getTuitionPaymentPlan() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.tuitionPaymentPlan.required");
        }

        if (getFinantialEntity() != getTuitionPaymentPlan().getFinantialEntity()) {
            throw new AcademicTreasuryDomainException(
                    "error.TuitionInstallmentTariff.finantialEntity.different.from.payment.plan");
        }

        if (getInstallmentOrder() <= 0) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.installmentOrder.must.be.positive");
        }

        if (find(getTuitionPaymentPlan(), getInstallmentOrder()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.tariff.installment.order.already.exists");
        }

        if (getTuitionCalculationType() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.tuitionCalculationType.required");
        }

        if ((isTuitionCalculationByEctsOrUnits() || getTuitionCalculationType().isUnits()) && getEctsCalculationType() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.ectsCalculationType.required");
        }

        if (!(isTuitionCalculationByEctsOrUnits() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && getFixedAmount() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixedAmount.required");
        }

        if (!(isTuitionCalculationByEctsOrUnits() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && !isPositive(getFixedAmount())) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixedAmount.must.be.positive");
        }

        if (isTuitionCalculationByEctsOrUnits() && getEctsCalculationType().isDefaultPaymentPlanIndexed()) {

            if (getFactor() == null) {
                throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.factor.required",
                        getTuitionCalculationType().getDescriptionI18N().getContent());
            }

            if (getTotalEctsOrUnits() == null) {
                throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.totalEctsOrUnits.required",
                        getTuitionCalculationType().getDescriptionI18N().getContent());
            }

            if (!isPositive(getFactor())) {
                throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.factor.must.be.positive",
                        getTuitionCalculationType().getDescriptionI18N().getContent());
            }

            if (!isPositive(getTotalEctsOrUnits())) {
                throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.totalEctsOrUnits.must.be.positive",
                        getTuitionCalculationType().getDescriptionI18N().getContent());
            }
            
            if(!TuitionPaymentPlan.isDefaultPaymentPlanDefined(getTuitionPaymentPlan().getDegreeCurricularPlan(),
                    getTuitionPaymentPlan().getExecutionYear())) {
                throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.default.payment.plan.not.defined");
            }
        }
    }

    private boolean isTuitionCalculationByEctsOrUnits() {
        return getTuitionCalculationType().isEcts() || getTuitionCalculationType().isUnits();
    }

    public BigDecimal getAmountPerEctsOrUnit() {
        if (getTuitionCalculationType().isFixedAmount()) {
            throw new RuntimeException("invalid call");
        }

        if (getEctsCalculationType().isFixedAmount()) {
            return getFixedAmount();
        }

        if (!TuitionPaymentPlan.isDefaultPaymentPlanDefined(getTuitionPaymentPlan().getDegreeCurricularPlan(),
                getTuitionPaymentPlan().getExecutionYear())) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.default.payment.plan.not.defined");
        }

        final TuitionPaymentPlan defaultPaymentPlan =
                TuitionPaymentPlan.findUniqueDefaultPaymentPlan(getTuitionPaymentPlan().getDegreeCurricularPlan(),
                        getTuitionPaymentPlan().getExecutionYear()).get();

        return Constants.divide(Constants.defaultScale(defaultPaymentPlan.tuitionTotalAmount()).multiply(getFactor()),
                getTotalEctsOrUnits());
    }

    public BigDecimal amountToPay(final AcademicTreasuryEvent academicTreasuryEvent) {
        if (!getTuitionPaymentPlan().getTuitionPaymentPlanGroup().isForRegistration()) {
            throw new RuntimeException("wrong call");
        }

        if (getTuitionCalculationType().isFixedAmount()) {
            return getFixedAmount();
        }

        if (getTuitionCalculationType().isEcts()) {
            return academicTreasuryEvent.getEnrolledEctsUnits().multiply(getAmountPerEctsOrUnit());
        }

        if (getTuitionCalculationType().isEcts()) {
            return academicTreasuryEvent.getEnrolledCoursesCount().multiply(getAmountPerEctsOrUnit());
        }

        throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.unknown.amountToPay");
    }

    private BigDecimal amountToPay(final AcademicTreasuryEvent academicTreasuryEvent, final Enrolment standaloneEnrolment) {
        if (!(getTuitionPaymentPlan().getTuitionPaymentPlanGroup().isForStandalone() || getTuitionPaymentPlan()
                .getTuitionPaymentPlanGroup().isForExtracurricular())) {
            throw new RuntimeException("wrong call");
        }

        if (getTuitionCalculationType().isFixedAmount() || getTuitionCalculationType().isUnits()) {
            return getAmountPerEctsOrUnit();
        }

        if (getTuitionCalculationType().isEcts()) {
            return new BigDecimal(standaloneEnrolment.getEctsCredits()).multiply(getAmountPerEctsOrUnit());
        }

        throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.unknown.amountToPay");
    }

    public DebitEntry createDebitEntryForRegistration(final DebtAccount debtAccount,
            final AcademicTreasuryEvent academicTreasuryEvent, final LocalDate when) {

        if (!getTuitionPaymentPlan().getTuitionPaymentPlanGroup().isForRegistration()) {
            throw new RuntimeException("wrong call");
        }

        final BigDecimal amount = amountToPay(academicTreasuryEvent);
        final LocalDate dueDate = dueDate(when != null ? when : new LocalDate());

        updatePriceValuesInEvent(academicTreasuryEvent);

        final Map<String, String> fillPriceProperties = fillPriceProperties(academicTreasuryEvent, dueDate);

        return DebitEntry.create(null, debtAccount, academicTreasuryEvent, vat(when), amount, dueDate, fillPriceProperties,
                getProduct(), installmentName().getContent(), Constants.DEFAULT_QUANTITY, this, new DateTime());
    }

    public DebitEntry createDebitEntryForStandalone(final DebtAccount debtAccount,
            final AcademicTreasuryEvent academicTreasuryEvent, final Enrolment standaloneEnrolment, final LocalDate when) {

        if (!getTuitionPaymentPlan().getTuitionPaymentPlanGroup().isForStandalone()) {
            throw new RuntimeException("wrong call");
        }

        if (!standaloneEnrolment.isStandalone()) {
            throw new RuntimeException("error.TuitionPaymentPlan.enrolment.not.standalone");
        }

        final BigDecimal amount = amountToPay(academicTreasuryEvent, standaloneEnrolment);
        final LocalDate dueDate = dueDate(when != null ? when : new LocalDate());

        updatePriceValuesInEvent(academicTreasuryEvent);

        final Map<String, String> fillPriceProperties = fillPriceProperties(academicTreasuryEvent, standaloneEnrolment, dueDate);

        final DebitEntry debitEntry =
                DebitEntry.create(null, debtAccount, academicTreasuryEvent, vat(when), amount, dueDate, fillPriceProperties,
                        getProduct(), standaloneDebitEntryName(standaloneEnrolment).getContent(), Constants.DEFAULT_QUANTITY,
                        this, new DateTime());

        academicTreasuryEvent.associateEnrolment(debitEntry, standaloneEnrolment);

        return debitEntry;
    }

    private void updatePriceValuesInEvent(final AcademicTreasuryEvent academicTreasuryEvent) {

    }

    public LocalizedString installmentName() {
        return getTuitionPaymentPlan().installmentName(this);
    }

    private LocalizedString standaloneDebitEntryName(final Enrolment standaloneEnrolment) {
        LocalizedString result = new LocalizedString();
        for (final Locale locale : CoreConfiguration.supportedLocales()) {
            result =
                    result.with(locale, BundleUtil.getString(Constants.BUNDLE, locale,
                            "label.TuitionPaymentPlan.standalone.debit.entry.name",
                            standaloneEnrolment.getName().getContent(locale)));
        }

        return result;
    }

    public Vat vat(final LocalDate when) {
        return Vat.findActiveUnique(getProduct().getVatType(), getFinantialEntity().getFinantialInstitution(),
                when.toDateTimeAtStartOfDay()).get();
    }

    private Map<String, String> fillPriceProperties(final AcademicTreasuryEvent academicTreasuryEvent, final Enrolment enrolment,
            final LocalDate dueDate) {

        if (!(getTuitionPaymentPlan().getTuitionPaymentPlanGroup().isForStandalone() || getTuitionPaymentPlan()
                .getTuitionPaymentPlanGroup().isForExtracurricular())) {
            throw new RuntimeException("wrong call");
        }

        final Map<String, String> propertiesMap = Maps.newHashMap();

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.ENROLMENT.getDescriptionI18N().getContent(), enrolment
                .getName().getContent());

        if (getTuitionCalculationType().isFixedAmount()) {
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.FIXED_AMOUNT.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getFixedAmount()));
        } else if (getTuitionCalculationType().isEcts()) {
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.ECTS_CREDITS.getDescriptionI18N().getContent(),
                    new BigDecimal(enrolment.getEctsCredits()).toString());
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.AMOUNT_PER_ECTS.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getAmountPerEctsOrUnit()));
            propertiesMap.put(
                    AcademicTreasuryEvent.AcademicTreasuryEventKeys.FINAL_AMOUNT.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency()
                            .getValueFor(amountToPay(academicTreasuryEvent, enrolment)));
        } else if (getTuitionCalculationType().isUnits()) {
            propertiesMap.put(
                    AcademicTreasuryEvent.AcademicTreasuryEventKeys.AMOUNT_PER_COURSE.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getAmountPerEctsOrUnit()));
            propertiesMap.put(
                    AcademicTreasuryEvent.AcademicTreasuryEventKeys.FINAL_AMOUNT.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency()
                            .getValueFor(amountToPay(academicTreasuryEvent, enrolment)));
        }

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.DUE_DATE.getDescriptionI18N().getContent(),
                dueDate.toString(Constants.DATE_FORMAT));

        return propertiesMap;
    }

    private Map<String, String> fillPriceProperties(final AcademicTreasuryEvent academicTreasuryEvent, final LocalDate dueDate) {
        final Map<String, String> propertiesMap = Maps.newHashMap();

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.TUITION_CALCULATION_TYPE.getDescriptionI18N()
                .getContent(), getTuitionCalculationType().getDescriptionI18N().getContent());

        if (getTuitionPaymentPlan().getTuitionPaymentPlanGroup().isForRegistration()) {
            if (getTuitionCalculationType().isFixedAmount()) {
                propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.FIXED_AMOUNT.getDescriptionI18N().getContent(),
                        getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getFixedAmount()));
            } else if (getTuitionCalculationType().isEcts()) {
                propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.ECTS_CREDITS.getDescriptionI18N().getContent(),
                        academicTreasuryEvent.getEnrolledEctsUnits().toString());
                propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.AMOUNT_PER_ECTS.getDescriptionI18N()
                        .getContent(),
                        getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getAmountPerEctsOrUnit()));
                propertiesMap.put(
                        AcademicTreasuryEvent.AcademicTreasuryEventKeys.FINAL_AMOUNT.getDescriptionI18N().getContent(),
                        getFinantialEntity().getFinantialInstitution().getCurrency()
                                .getValueFor(amountToPay(academicTreasuryEvent)));
            } else if (getTuitionCalculationType().isUnits()) {
                propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.ENROLLED_COURSES.getDescriptionI18N()
                        .getContent(), academicTreasuryEvent.getEnrolledEctsUnits().toString());
                propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.AMOUNT_PER_COURSE.getDescriptionI18N()
                        .getContent(),
                        getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getAmountPerEctsOrUnit()));
                propertiesMap.put(
                        AcademicTreasuryEvent.AcademicTreasuryEventKeys.FINAL_AMOUNT.getDescriptionI18N().getContent(),
                        getFinantialEntity().getFinantialInstitution().getCurrency()
                                .getValueFor(amountToPay(academicTreasuryEvent)));
            }
        } else if (getTuitionPaymentPlan().getTuitionPaymentPlanGroup().isForStandalone()) {

        }

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.DUE_DATE.getDescriptionI18N().getContent(),
                dueDate.toString(Constants.DATE_FORMAT));

        return propertiesMap;
    }

    @Override
    public void delete() {
        super.setTuitionPaymentPlan(null);

        super.delete();
    }

    // @formatter:off
    /* --------
     * SERVICES
     * --------
     */
    // @formatter:on

    protected static Stream<TuitionInstallmentTariff> find(final TuitionPaymentPlan tuitionPaymentPlan, final int installmentOrder) {
        return tuitionPaymentPlan.getTuitionInstallmentTariffsSet().stream()
                .filter(t -> t.getInstallmentOrder() == installmentOrder);
    }

    public static Optional<TuitionInstallmentTariff> findUnique(final TuitionPaymentPlan tuitionPaymentPlan,
            final int installmentOrder) {
        return find(tuitionPaymentPlan, installmentOrder).findFirst();
    }

    public static TuitionInstallmentTariff create(final FinantialEntity finantialEntity,
            final TuitionPaymentPlan tuitionPaymentPlan, final AcademicTariffBean bean) {
        return new TuitionInstallmentTariff(finantialEntity, tuitionPaymentPlan, bean);
    }

    @Override
    public LocalizedString getUiTariffDescription() {
        // TODO ANIL
        return null;
    }

}
