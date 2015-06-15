package org.fenixedu.academictreasury.domain.tariff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.Vat;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.fenixedu.treasury.domain.tariff.Tariff;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Maps;

public class AcademicTariff extends AcademicTariff_Base {

    protected AcademicTariff(final FinantialEntity finantialEntity, final Product product, final AcademicTariffBean bean) {
        super();

        init(finantialEntity, product, bean);
    }

    @Override
    protected void init(final FinantialEntity finantialEntity, final Product product, final DateTime beginDate,
            final DateTime endDate, final DueDateCalculationType dueDateCalculationType, final LocalDate fixedDueDate,
            final int numberOfDaysAfterCreationForDueDate, final boolean applyInterests, final InterestType interestType,
            final int numberOfDaysAfterDueDate, final boolean applyInFirstWorkday, final int maximumDaysToApplyPenalty,
            final int maximumMonthsToApplyPenalty, final BigDecimal interestFixedAmount, final BigDecimal rate) {
        throw new RuntimeException("wrong call");
    }

    protected void init(final FinantialEntity finantialEntity, final Product product, final AcademicTariffBean bean) {

        super.init(finantialEntity, product, bean.getBeginDate().toDateTimeAtStartOfDay(), bean.getEndDate() != null ? bean
                .getEndDate().toDateTimeAtStartOfDay() : null, bean.getDueDateCalculationType(),
                bean.getFixedDueDate() != null ? bean.getFixedDueDate() : null, bean.getNumberOfDaysAfterCreationForDueDate(),
                bean.isApplyInterests(), bean.getInterestType(), bean.getNumberOfDaysAfterDueDate(),
                bean.isApplyInFirstWorkday(), bean.getMaximumDaysToApplyPenalty(), bean.getMaximumMonthsToApplyPenalty(), bean
                        .getInterestFixedAmount(), bean.getRate());

        setBaseAmount(bean.getBaseAmount());
        setUnitsForBase(bean.getUnitsForBase());
        setUnitAmount(bean.getUnitAmount());
        setPageAmount(bean.getPageAmount());
        setMaximumAmount(bean.getMaximumAmount());
        setUrgencyRate(bean.getUrgencyRate());
        setLanguageTranslationRate(bean.getLanguageTranslationRate());

        setAdministrativeOffice(bean.getAdministrativeOffice());
        setDegreeType(bean.getDegreeType());
        setDegree(bean.getDegree());
        setCycleType(bean.getCycleType());

        checkRules();
    }

    public boolean isApplyUnitsAmount() {
        return isPositive(getUnitAmount());
    }

    public boolean isApplyPagesAmount() {
        return isPositive(getPageAmount());
    }

    public boolean isApplyMaximumAmount() {
        return isPositive(getMaximumAmount());
    }

    public boolean isApplyUrgencyRate() {
        return isPositive(getUrgencyRate());
    }

    public boolean isApplyLanguageTranslationRate() {
        return isPositive(getLanguageTranslationRate());
    }

    public boolean isApplyBaseAmount() {
        return isPositive(getBaseAmount());
    }

    protected void checkRules() {
        super.checkRules();

        if (getAdministrativeOffice() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.administrativeOffice.required");
        }

        if (getCycleType() != null && getDegree() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.degree.required");
        }

        if (getDegree() != null && getDegreeType() != getDegree().getDegreeType()) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.degreeType.required");
        }

        if (getCycleType() != null && !getDegreeType().getCycleTypes().contains(getCycleType())) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.cycleType.does.not.belong.degree.type");
        }

        if (getBaseAmount() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.baseAmount.required");
        }

        if (isNegative(getBaseAmount())) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.baseAmount.negative");
        }

        if (getUnitsForBase() < 0) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.unitsForBase.negative");
        }

        if (getUnitAmount() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.unitAmount.required");
        }

        if (isNegative(getUnitAmount())) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.unitAmount.negative");
        }

        if (getPageAmount() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.pageAmount.required");
        }

        if (isNegative(getPageAmount())) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.pageAmount.negative");
        }

        if (getUrgencyRate() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.urgencyRate.required");
        }

        if (isNegative(getUrgencyRate())) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.urgencyRate.negative");
        }

        if (Constants.HUNDRED_PERCENT.compareTo(getUrgencyRate()) < 0) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.urgencyRate.greater.than.hundred");
        }

        if (getLanguageTranslationRate() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.languageTranslationRate.required");
        }

        if (isNegative(getLanguageTranslationRate())) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.languageTranslationRate.negative");
        }

        if (Constants.HUNDRED_PERCENT.compareTo(getLanguageTranslationRate()) < 0) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.languageTranslationRate.greater.than.hundred");
        }

        /*
         * The following checkings aims to garantee that are not created overlapping tariffs
         * in all finantial entities.
         */

        if (getCycleType() != null) {
            if (findInInterval(getProduct(), getAdministrativeOffice(), getDegreeType(), getDegree(), getCycleType(),
                    getInterval()).count() > 1) {
                throw new AcademicTreasuryDomainException("error.AcademicTariff.overlaps.with.other");
            };
        } else if (getDegree() != null) {
            if (findInInterval(getProduct(), getAdministrativeOffice(), getDegreeType(), getDegree(), getInterval()).count() > 1) {
                throw new AcademicTreasuryDomainException("error.AcademicTariff.overlaps.with.other");
            }
        } else if (getDegreeType() != null) {
            if (findInInterval(getProduct(), getAdministrativeOffice(), getDegreeType(), getInterval()).count() > 1) {
                throw new AcademicTreasuryDomainException("error.AcademicTariff.overlaps.with.other");
            };
        } else {
            if (findInInterval(getProduct(), getAdministrativeOffice(), getInterval()).count() > 1) {
                throw new AcademicTreasuryDomainException("error.AcademicTariff.overlaps.with.other");
            };
        }
    }

    @Override
    public boolean isDeletable() {
        return super.isDeletable();
    }

    @Atomic
    public void delete() {
        setAdministrativeOffice(null);
        setDegreeType(null);
        setDegree(null);

        super.delete();
    }

    public BigDecimal amountToPay(final AcademicTreasuryEvent academicTreasuryEvent) {
        BigDecimal amount = amountWithLanguageRate(academicTreasuryEvent);

        if (isApplyUrgencyRate() && academicTreasuryEvent.isUrgentRequest()) {
            amount = amount.add(amountForUrgencyRate(academicTreasuryEvent));
        }

        return amount;
    }

    public BigDecimal amountForUrgencyRate(final AcademicTreasuryEvent academicTreasuryEvent) {
        BigDecimal amount = amountWithLanguageRate(academicTreasuryEvent);

        return amount.multiply(getUrgencyRate().setScale(20, RoundingMode.HALF_EVEN).divide(Constants.HUNDRED_PERCENT)
                .setScale(2, RoundingMode.HALF_EVEN));
    }

    public BigDecimal amountForLanguageTranslationRate(final AcademicTreasuryEvent academicTreasuryEvent) {
        final BigDecimal amount = amountToPayWithoutRates(academicTreasuryEvent);

        final BigDecimal result =
                amount.multiply(getLanguageTranslationRate().setScale(20, RoundingMode.HALF_EVEN)
                        .divide(Constants.HUNDRED_PERCENT).setScale(2, RoundingMode.HALF_EVEN));

        return isPositive(result) ? result : BigDecimal.ZERO;
    }

    public BigDecimal amountToPayWithoutRates(final AcademicTreasuryEvent academicTreasuryEvent) {
        BigDecimal amount = getBaseAmount();

        if (isApplyUnitsAmount()) {
            int remainingUnits = numberOfAdditionalUnits(academicTreasuryEvent);

            if (remainingUnits > 0) {
                amount = amount.add(amountForAdditionalUnits(academicTreasuryEvent));
            }
        }

        if (isApplyPagesAmount()) {
            amount = amount.add(amountForPages(academicTreasuryEvent));
        }

        if (isApplyMaximumAmount() && isGreaterThan(amount, getMaximumAmount())) {
            amount = getMaximumAmount();
        }
        return amount;
    }

    public BigDecimal amountForPages(final AcademicTreasuryEvent academicTreasuryEvent) {
        final BigDecimal result = getPageAmount().multiply(new BigDecimal(academicTreasuryEvent.getNumberOfPages()));

        return isPositive(result) ? result : BigDecimal.ZERO;
    }

    public BigDecimal amountForAdditionalUnits(final AcademicTreasuryEvent academicTreasuryEvent) {
        final int remainingUnits = numberOfAdditionalUnits(academicTreasuryEvent);
        final BigDecimal result = getUnitAmount().multiply(new BigDecimal(remainingUnits));

        return isPositive(result) ? result : BigDecimal.ZERO;
    }

    public int numberOfAdditionalUnits(final AcademicTreasuryEvent academicTreasuryEvent) {
        return academicTreasuryEvent.getNumberOfUnits() - getUnitsForBase();
    }

    public DebitEntry createDebitEntry(final DebtAccount debtAccount, final AcademicTreasuryEvent academicTreasuryEvent) {
        final BigDecimal amount = amountToPay(academicTreasuryEvent);
        final LocalDate dueDate = dueDate(academicTreasuryEvent.getRequestDate());

        updatePriceValuesInEvent(academicTreasuryEvent);

        final Map<String, String> fillPriceProperties = fillPriceProperties(academicTreasuryEvent);

        return DebitEntry.create(null, debtAccount, academicTreasuryEvent,
                Vat.findActiveUnique(getProduct().getVatType(), getFinantialEntity().getFinantialInstitution(), new DateTime())
                        .get(), amount, dueDate, fillPriceProperties, getProduct(), getProduct().getName().getContent(),
                Constants.DEFAULT_QUANTITY, this, new DateTime());
    }

    private void updatePriceValuesInEvent(final AcademicTreasuryEvent academicTreasuryEvent) {

        final BigDecimal baseAmount = getBaseAmount();
        final BigDecimal amountForAdditionalUnits = amountForAdditionalUnits(academicTreasuryEvent);
        final BigDecimal amountForPages = amountForPages(academicTreasuryEvent);
        final BigDecimal maximumAmount = getMaximumAmount();
        final BigDecimal amountForLanguageTranslationRate = amountForLanguageTranslationRate(academicTreasuryEvent);
        final BigDecimal amountForUrgencyRate = amountForUrgencyRate(academicTreasuryEvent);

        academicTreasuryEvent.updatePricingFields(baseAmount, amountForAdditionalUnits, amountForPages, maximumAmount,
                amountForLanguageTranslationRate, amountForUrgencyRate);
    }

    private Map<String, String> fillPriceProperties(final AcademicTreasuryEvent academicTreasuryEvent) {
        final Map<String, String> propertiesMap = Maps.newHashMap();

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.BASE_AMOUNT.getDescriptionI18N().getContent(),
                getBaseAmount().toString());
        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.UNITS_FOR_BASE.getDescriptionI18N().getContent(),
                String.valueOf(getUnitsForBase()));

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.UNIT_AMOUNT.getDescriptionI18N().getContent(),
                getUnitAmount().toString());
        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.ADDITIONAL_UNITS.getDescriptionI18N().getContent(),
                String.valueOf(numberOfAdditionalUnits(academicTreasuryEvent)));
        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.CALCULATED_UNITS_AMOUNT.getDescriptionI18N()
                .getContent(), amountForAdditionalUnits(academicTreasuryEvent).toString());

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.PAGE_AMOUNT.getDescriptionI18N().getContent(),
                getPageAmount().toString());
        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.NUMBER_OF_PAGES.getDescriptionI18N().getContent(),
                String.valueOf(academicTreasuryEvent.getNumberOfPages()));
        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.CALCULATED_PAGES_AMOUNT.getDescriptionI18N()
                .getContent(), amountForPages(academicTreasuryEvent).toString());

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.MAXIMUM_AMOUNT.getDescriptionI18N().getContent(),
                getMaximumAmount().toString());

        propertiesMap.put(
                AcademicTreasuryEvent.AcademicTreasuryEventKeys.FOREIGN_LANGUAGE_RATE.getDescriptionI18N().getContent(),
                getLanguageTranslationRate().toString());
        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.CALCULATED_FOREIGN_LANGUAGE_RATE.getDescriptionI18N()
                .getContent(), amountForLanguageTranslationRate(academicTreasuryEvent).toString());

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.URGENT_PERCENTAGE.getDescriptionI18N().getContent(),
                getUrgencyRate().toString());
        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.CALCULATED_URGENT_AMOUNT.getDescriptionI18N()
                .getContent(), amountForUrgencyRate(academicTreasuryEvent).toString());

        propertiesMap.put(AcademicTreasuryEvent.AcademicTreasuryEventKeys.FINAL_AMOUNT.getDescriptionI18N().getContent(),
                amountToPay(academicTreasuryEvent).toString());

        return propertiesMap;
    }

    private BigDecimal amountWithLanguageRate(final AcademicTreasuryEvent academicTreasuryEvent) {
        BigDecimal amount = amountToPayWithoutRates(academicTreasuryEvent);

        if (isApplyLanguageTranslationRate() && Constants.isForeignLanguage(academicTreasuryEvent.getLanguage())) {
            amount = amount.add(amountForLanguageTranslationRate(academicTreasuryEvent));
        }

        return amount;
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    @Atomic
    public static AcademicTariff create(final FinantialEntity finantialEntity, final Product product,
            final AcademicTariffBean bean) {
        return new AcademicTariff(finantialEntity, product, bean);
    }

    public static Stream<? extends AcademicTariff> findAll() {
        return Tariff.findAll().filter(t -> t instanceof AcademicTariff).map(AcademicTariff.class::cast);
    }

    public static Stream<? extends AcademicTariff> find(final Product product) {
        return AcademicTariff.findAll().filter(t -> t.getProduct() == product);
    }

    public static Stream<? extends AcademicTariff> find(final FinantialEntity finantialEntity) {
        return AcademicTariff.findAll().filter(t -> t.getFinantialEntity() == finantialEntity);
    }

    public static Stream<? extends AcademicTariff> find(final FinantialEntity finantialEntity, final Product product) {
        return AcademicTariff.find(product).filter(t -> t.getFinantialEntity() == finantialEntity);
    }

    private static Stream<? extends AcademicTariff> find(final Product product, final AdministrativeOffice administrativeOffice) {
        return AcademicTariff.find(product).filter(i -> administrativeOffice == i.getAdministrativeOffice());
    }

    private static Stream<? extends AcademicTariff> find(final Product product, final AdministrativeOffice administrativeOffice,
            final DegreeType degreeType) {
        if (degreeType == null) {
            throw new RuntimeException("degree type is null. wrong find call");
        }

        return AcademicTariff.find(product, administrativeOffice).filter(i -> degreeType == i.getDegreeType());
    }

    private static Stream<? extends AcademicTariff> find(final Product product, final AdministrativeOffice administrativeOffice,
            final DegreeType degreeType, final Degree degree) {
        if (degree == null) {
            throw new RuntimeException("degree is null. wrong find call");
        }

        return AcademicTariff.find(product, administrativeOffice, degreeType).filter(t -> t.getDegree() == degree);
    }

    private static Stream<? extends AcademicTariff> find(final Product product, final AdministrativeOffice administrativeOffice,
            final DegreeType degreeType, final Degree degree, final CycleType cycleType) {
        if (cycleType == null) {
            throw new RuntimeException("cycle is null. wrong find call");
        }

        return AcademicTariff.find(product, administrativeOffice, degreeType, degree).filter(t -> t.getCycleType() == cycleType);
    }

    public static Stream<? extends AcademicTariff> findActive(final DateTime when) {
        return AcademicTariff.findAll().filter(t -> t.isActive(when));
    }

    public static Stream<? extends AcademicTariff> findActive(final FinantialEntity finantialEntity, final DateTime when) {
        return AcademicTariff.find(finantialEntity).filter(t -> t.isActive(when));
    }

    public static Stream<? extends AcademicTariff> findActive(final FinantialEntity finantialEntity, final Product product,
            final DateTime when) {
        return AcademicTariff.find(finantialEntity, product).filter(t -> t.isActive(when));
    }

    public static Stream<? extends AcademicTariff> findActive(final Product product,
            final AdministrativeOffice administrativeOffice, final DateTime when) {
        return AcademicTariff.find(product, administrativeOffice).filter(t -> t.isActive(when));
    }

    public static Stream<? extends AcademicTariff> findActive(final Product product,
            final AdministrativeOffice administrativeOffice, final DegreeType degreeType, final DateTime when) {
        return AcademicTariff.find(product, administrativeOffice, degreeType).filter(t -> t.isActive(when));
    }

    public static Stream<? extends AcademicTariff> findActive(final Product product,
            final AdministrativeOffice administrativeOffice, final DegreeType degreeType, final Degree degree, final DateTime when) {
        return AcademicTariff.find(product, administrativeOffice, degreeType, degree).filter(t -> t.isActive(when));
    }

    public static Stream<? extends AcademicTariff> findActive(final Product product,
            final AdministrativeOffice administrativeOffice, final DegreeType degreeType, final Degree degree,
            final CycleType cycleType, final DateTime when) {
        return AcademicTariff.find(product, administrativeOffice, degreeType, degree, cycleType).filter(t -> t.isActive(when));
    }

    public static Stream<? extends AcademicTariff> findInInterval(final Interval interval) {
        return AcademicTariff.findAll().filter(t -> t.isActive(interval));
    }

    public static Stream<? extends AcademicTariff> findInInterval(final FinantialEntity finantialEntity, final Interval interval) {
        return AcademicTariff.find(finantialEntity).filter(t -> t.isActive(interval));
    }

    public static Stream<? extends AcademicTariff> findInInterval(final FinantialEntity finantialEntity, final Product product,
            final Interval interval) {
        return AcademicTariff.find(finantialEntity, product).filter(t -> t.isActive(interval));
    }

    public static Stream<? extends AcademicTariff> findInInterval(final Product product,
            final AdministrativeOffice administrativeOffice, final Interval interval) {
        return AcademicTariff.find(product, administrativeOffice).filter(t -> t.isActive(interval));
    }

    public static Stream<? extends AcademicTariff> findInInterval(final Product product,
            final AdministrativeOffice administrativeOffice, final DegreeType degreeType, final Interval interval) {
        return AcademicTariff.find(product, administrativeOffice, degreeType).filter(t -> t.isActive(interval));
    }

    public static Stream<? extends AcademicTariff> findInInterval(final Product product,
            final AdministrativeOffice administrativeOffice, final DegreeType degreeType, final Degree degree,
            final Interval interval) {
        return AcademicTariff.find(product, administrativeOffice, degreeType, degree).filter(t -> t.isActive(interval));
    }

    public static Stream<? extends AcademicTariff> findInInterval(final Product product,
            final AdministrativeOffice administrativeOffice, final DegreeType degreeType, final Degree degree,
            final CycleType cycleType, final Interval interval) {
        return AcademicTariff.find(product, administrativeOffice, degreeType, degree, cycleType)
                .filter(t -> t.isActive(interval));
    }

    public static AcademicTariff findMatch(final Product product, final AdministrativeOffice administrativeOffice,
            final DateTime when) {
        return null;
    }

    public static AcademicTariff findMatch(final Product product, final AdministrativeOffice administrativeOffice,
            final DegreeType degreeType) {
        return null;
    }

    public static AcademicTariff findMatch(final Product product, final Degree degree, final DateTime when) {
        if (degree == null) {
            throw new RuntimeException("degree is null. wrong findMatch call");
        }

        final AdministrativeOffice administrativeOffice = degree.getAdministrativeOffice();
        final DegreeType degreeType = degree.getDegreeType();

        // With the most specific conditions tariff was not found. Fallback to degree
        Set<? extends AcademicTariff> activeTariffs =
                findActive(product, administrativeOffice, degreeType, degree, when).collect(Collectors.<AcademicTariff> toSet());
        if (activeTariffs.size() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (activeTariffs.size() == 1) {
            return activeTariffs.iterator().next();
        }

        // Fallback to degreeType
        activeTariffs = findActive(product, administrativeOffice, degreeType, when).collect(Collectors.<AcademicTariff> toSet());
        if (activeTariffs.size() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (activeTariffs.size() == 1) {
            return activeTariffs.iterator().next();
        }

        // Fallback to administrativeOffice and return
        activeTariffs = findActive(product, administrativeOffice, when).collect(Collectors.<AcademicTariff> toSet());
        if (activeTariffs.size() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        }

        return !activeTariffs.isEmpty() ? activeTariffs.iterator().next() : null;
    }

    public static AcademicTariff findMatch(final Product product, final Degree degree, final CycleType cycleType,
            final DateTime when) {
        if (degree == null || cycleType == null) {
            throw new RuntimeException("degree or cycle type is null. wrong findMatch call");
        }

        final AdministrativeOffice administrativeOffice = degree.getAdministrativeOffice();
        final DegreeType degreeType = degree.getDegreeType();

        Set<? extends AcademicTariff> activeTariffs =
                findActive(product, administrativeOffice, degreeType, degree, cycleType, when).collect(
                        Collectors.<AcademicTariff> toSet());
        if (activeTariffs.size() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (activeTariffs.size() == 1) {
            return activeTariffs.iterator().next();
        }

        // With the most specific conditions tariff was not found. Fallback to degree
        activeTariffs =
                findActive(product, administrativeOffice, degreeType, degree, when).collect(Collectors.<AcademicTariff> toSet());
        if (activeTariffs.size() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (activeTariffs.size() == 1) {
            return activeTariffs.iterator().next();
        }

        // Fallback to degreeType
        activeTariffs = findActive(product, administrativeOffice, degreeType, when).collect(Collectors.<AcademicTariff> toSet());
        if (activeTariffs.size() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (activeTariffs.size() == 1) {
            return activeTariffs.iterator().next();
        }

        // Fallback to administrativeOffice and return
        activeTariffs = findActive(product, administrativeOffice, when).collect(Collectors.<AcademicTariff> toSet());
        if (activeTariffs.size() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        }

        return !activeTariffs.isEmpty() ? activeTariffs.iterator().next() : null;
    }

    /* ----
     * UTIL
     * ----
     */

    @Override
    public LocalizedString getUiTariffDescription() {
        // TODO ANIL
        return null;
    }

}
