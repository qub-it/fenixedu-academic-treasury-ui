package org.fenixedu.academictreasury.domain.tuition;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.Bennu;
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

        super.init(finantialEntity, tuitionPaymentPlan.getProduct(), bean.getBeginDate().toDateTimeAtStartOfDay(),
                bean.getEndDate() != null ? bean.getEndDate().toDateTimeAtStartOfDay() : null, bean.getDueDateCalculationType(),
                bean.getFixedDueDate(), bean.getNumberOfDaysAfterCreationForDueDate(), bean.isApplyInterests(),
                bean.getInterestType(), bean.getNumberOfDaysAfterDueDate(), bean.isApplyInFirstWorkday(),
                bean.getMaximumDaysToApplyPenalty(), bean.getMaximumMonthsToApplyPenalty(), bean.getInterestFixedAmount(),
                bean.getRate());

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

        if (getInstallmentOrder() <= 0) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.installmentOrder.must.be.positive");
        }

        if (find(getTuitionPaymentPlan(), getInstallmentOrder()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.tariff.installment.order.already.exists");
        }

        if (getTuitionCalculationType() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.tuitionCalculationType.required");
        }

        if ((getTuitionCalculationType().isEcts() || getTuitionCalculationType().isUnits()) && getEctsCalculationType() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.ectsCalculationType.required");
        }

        if (!(getTuitionCalculationType().isEcts() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && getFixedAmount() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixedAmount.required");
        }

        if (!(getTuitionCalculationType().isEcts() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && !isPositive(getFixedAmount())) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixedAmount.must.be.positive");
        }

        if (!(getTuitionCalculationType().isUnits() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && getFixedAmount() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixedAmount.required");
        }

        if (!(getTuitionCalculationType().isUnits() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && !isPositive(getFixedAmount())) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixedAmount.must.be.positive");
        }

        if ((getTuitionCalculationType().isEcts() || getTuitionCalculationType().isUnits())
                && getEctsCalculationType().isDefaultPaymentPlanIndexed()) {

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
        }
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

    private BigDecimal amountToPay(final AcademicTreasuryEvent academicTreasuryEvent) {
        if (getTuitionCalculationType().isFixedAmount()) {
            return getFixedAmount();
        }

        if (getTuitionCalculationType().isEcts() && getEctsCalculationType().isFixedAmount()) {
            return academicTreasuryEvent.getEnrolledEctsUnits().multiply(getFixedAmount());
        }

        if (getTuitionCalculationType().isEcts() && getEctsCalculationType().isDefaultPaymentPlanIndexed()) {
            return academicTreasuryEvent.getEnrolledEctsUnits().multiply(getAmountPerEctsOrUnit());
        }

        if (getTuitionCalculationType().isUnits() && getEctsCalculationType().isFixedAmount()) {
            return academicTreasuryEvent.getEnrolledCoursesCount().multiply(getFixedAmount());
        }

        if (getTuitionCalculationType().isEcts() && getEctsCalculationType().isDefaultPaymentPlanIndexed()) {
            return academicTreasuryEvent.getEnrolledCoursesCount().multiply(getAmountPerEctsOrUnit());
        }

        throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.unknown.amountToPay");
    }

    private LocalDate dueDate(final AcademicTreasuryEvent academicTreasuryEvent) {
        return dueDate(new LocalDate());
    }

    public DebitEntry createDebitEntry(final PersonCustomer personCustomer, final AcademicTreasuryEvent academicTreasuryEvent) {

        if (!DebtAccount.findUnique(getFinantialEntity().getFinantialInstitution(), personCustomer).isPresent()) {
            DebtAccount.create(getFinantialEntity().getFinantialInstitution(), personCustomer);
        }

        final DebtAccount debtAccount =
                DebtAccount.findUnique(getFinantialEntity().getFinantialInstitution(), personCustomer).get();

        final BigDecimal amount = amountToPay(academicTreasuryEvent);
        final LocalDate dueDate = dueDate(academicTreasuryEvent);
        final Map<String, String> fillPriceProperties = fillPriceProperties(academicTreasuryEvent);

        return DebitEntry.create(null, debtAccount, academicTreasuryEvent, Vat.findActiveUnique(getProduct().getVatType(),
                getFinantialEntity().getFinantialInstitution(), new DateTime()).get(), amount, dueDate, fillPriceProperties,
                academicTreasuryEvent.getProduct(), getTuitionPaymentPlan().installmentName(this).getContent(),
                Constants.DEFAULT_QUANTITY, this, new DateTime());
    }

    private Map<String, String> fillPriceProperties(final AcademicTreasuryEvent academicTreasuryEvent) {
        final Map<String, String> propertiesMap = Maps.newHashMap();

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.TUITION_CALCULATION_TYPE.getDescriptionI18N()
                .getContent(), getTuitionCalculationType().getDescriptionI18N().getContent());
        if (getTuitionCalculationType().isFixedAmount()) {
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.FIXED_AMOUNT.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getFixedAmount()));
        } else if (getTuitionCalculationType().isEcts()) {
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.ECTS_CREDITS.getDescriptionI18N().getContent(),
                    academicTreasuryEvent.getEnrolledEctsUnits().toString());
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.AMOUNT_PER_ECTS.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getAmountPerEctsOrUnit()));
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.FINAL_AMOUNT.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(amountToPay(academicTreasuryEvent)));
        } else if (getTuitionCalculationType().isUnits()) {
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.ENROLLED_COURSES.getDescriptionI18N().getContent(),
                    academicTreasuryEvent.getEnrolledEctsUnits().toString());
            propertiesMap.put(
                    AcademicTreasuryEvent.AcademicTreasuryEventKeys.AMOUNT_PER_COURSE.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(getAmountPerEctsOrUnit()));
            propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.FINAL_AMOUNT.getDescriptionI18N().getContent(),
                    getFinantialEntity().getFinantialInstitution().getCurrency().getValueFor(amountToPay(academicTreasuryEvent)));
        }

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.DUE_DATE.getDescriptionI18N().getContent(),
                dueDate(academicTreasuryEvent).toString("dd/MM/yyyy"));

        return propertiesMap;
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
