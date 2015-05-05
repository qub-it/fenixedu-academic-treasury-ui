package org.fenixedu.academictreasury.domain.tariff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.VatType;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.tariff.Tariff;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import pt.ist.fenixframework.Atomic;

public class AcademicTariff extends AcademicTariff_Base {

    protected AcademicTariff(final FinantialEntity finantialEntity, final Product product, final VatType vatType,
            final AcademicTariffBean bean) {
        super();

        init(finantialEntity, product, vatType, bean);
    }

    protected void init(final FinantialEntity finantialEntity, final Product product, final VatType vatType,
            final AcademicTariffBean bean) {

        super.init(finantialEntity, product, vatType, bean.getBeginDate(), bean.getEndDate(), bean.getDueDateCalculationType(),
                bean.getFixedDueDate() != null ? bean.getFixedDueDate().toLocalDate() : null,
                bean.getNumberOfDaysAfterCreationForDueDate(), bean.isApplyInterests(), bean.getInterestType(),
                bean.getNumberOfDaysAfterDueDate(), bean.isApplyInFirstWorkday(), bean.getMaximumDaysToApplyPenalty(),
                bean.getMaximumMonthsToApplyPenalty(), bean.getInterestFixedAmount(), bean.getRate());

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
        super.delete();
    }

    public BigDecimal amountToPay(final AcademicServiceRequest academicServiceRequest) {
        BigDecimal amount = BigDecimal.ZERO;

        if (isApplyUnitsAmount()) {
            int remainingUnits = academicServiceRequest.getNumberOfUnits() - getUnitsForBase();

            if (remainingUnits > 0) {
                amount = amount.add(getUnitAmount().multiply(new BigDecimal(remainingUnits)));
            }
        }

        if (isApplyPagesAmount()) {
            amount = amount.add(getPageAmount().multiply(new BigDecimal(academicServiceRequest.getNumberOfPages())));
        }

        if (isApplyMaximumAmount() && isGreaterThan(amount, getMaximumAmount())) {
            amount = getMaximumAmount();
        }

        if (isApplyUrgencyRate() && academicServiceRequest.isUrgentRequest()) {
            amount =
                    amount.multiply(BigDecimal.ONE.add(getUrgencyRate().setScale(20, RoundingMode.HALF_EVEN)
                            .divide(Constants.HUNDRED_PERCENT).setScale(2, RoundingMode.HALF_EVEN)));
        }

        if (isApplyLanguageTranslationRate() && Constants.isForeignLanguage(academicServiceRequest.getLanguage())) {
            amount =
                    amount.multiply(BigDecimal.ONE.add(getLanguageTranslationRate().setScale(20, RoundingMode.HALF_EVEN)
                            .divide(Constants.HUNDRED_PERCENT).setScale(2, RoundingMode.HALF_EVEN)));
        }
        return amount;
    }
    
    public DebitEntry createDebitEntry(final DebtAccount debtAccount, final AcademicTreasuryEvent academicTreasuryEvent) {
        final AcademicServiceRequest academicServiceRequest = academicTreasuryEvent.getAcademicServiceRequest();

        final BigDecimal amount = amountToPay(academicServiceRequest);
        
        return DebitEntry.create(debtAccount, academicTreasuryEvent, amount);
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    @Atomic
    public static AcademicTariff create(final FinantialEntity finantialEntity, final Product product, final VatType vatType,
            final AcademicTariffBean bean) {
        return new AcademicTariff(finantialEntity, product, vatType, bean);
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

    public static Optional<? extends AcademicTariff> findMatch(final Product product, final Degree degree, final DateTime when) {
        if (degree == null) {
            throw new RuntimeException("degree is null. wrong findMatch call");
        }

        final AdministrativeOffice administrativeOffice = degree.getAdministrativeOffice();
        final DegreeType degreeType = degree.getDegreeType();

        // With the most specific conditions tariff was not found. Fallback to degree
        Stream<? extends AcademicTariff> stream = findActive(product, administrativeOffice, degreeType, degree, when);
        if (stream.count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (stream.count() == 1) {
            return stream.findFirst();
        }

        // Fallback to degreeType
        stream = findActive(product, administrativeOffice, degreeType, when);
        if (stream.count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (stream.count() == 1) {
            return stream.findFirst();
        }

        // Fallback to administrativeOffice and return
        stream = findActive(product, administrativeOffice, when);
        if (stream.count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        }

        return stream.findFirst();
    }

    public static Optional<? extends AcademicTariff> findMatch(final Product product, final Degree degree,
            final CycleType cycleType, final DateTime when) {
        if (degree == null || cycleType == null) {
            throw new RuntimeException("degree or cycle type is null. wrong findMatch call");
        }

        final AdministrativeOffice administrativeOffice = degree.getAdministrativeOffice();
        final DegreeType degreeType = degree.getDegreeType();

        Stream<? extends AcademicTariff> stream = findActive(product, administrativeOffice, degreeType, degree, cycleType, when);
        if (stream.count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (stream.count() == 1) {
            return stream.findFirst();
        }

        // With the most specific conditions tariff was not found. Fallback to degree
        stream = findActive(product, administrativeOffice, degreeType, degree, when);
        if (stream.count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (stream.count() == 1) {
            return stream.findFirst();
        }

        // Fallback to degreeType
        stream = findActive(product, administrativeOffice, degreeType, when);
        if (stream.count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        } else if (stream.count() == 1) {
            return stream.findFirst();
        }

        // Fallback to administrativeOffice and return
        stream = findActive(product, administrativeOffice, when);
        if (stream.count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTariff.findActive.more.than.one");
        }

        return stream.findFirst();
    }

}
