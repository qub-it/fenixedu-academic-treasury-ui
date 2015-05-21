package org.fenixedu.academictreasury.domain.tuition;

import java.math.BigDecimal;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class TuitionInstallmentTariff extends TuitionInstallmentTariff_Base {

    protected TuitionInstallmentTariff() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected TuitionInstallmentTariff(final FinantialEntity finantialEntity, final Product product, final AcademicTariffBean bean) {
        this();

        this.init(finantialEntity, product, bean);
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

        super.init(finantialEntity, product, bean.getBeginDate(), bean.getEndDate(), bean.getDueDateCalculationType(), bean
                .getFixedDueDate().toLocalDate(), bean.getNumberOfDaysAfterCreationForDueDate(), bean.isApplyInterests(), bean
                .getInterestType(), bean.getNumberOfDaysAfterDueDate(), bean.isApplyInFirstWorkday(), bean
                .getMaximumDaysToApplyPenalty(), bean.getMaximumMonthsToApplyPenalty(), bean.getInterestFixedAmount(), bean
                .getRate());
        
        super.setInstallmentOrder(bean.getInstallmentOrder());
        super.setTuitionCalculationType(bean.getTuitionCalculationType());
        super.setFixedAmount(bean.getFixedAmount());
        super.setEctsCalculationType(bean.getEctsCalculationType());
        super.setAcademicalActBlockingOff(bean.isAcademicalActBlockingOff());

        checkRules();
    }

    @Override
    protected void checkRules() {
        super.checkRules();

        if (!(getTuitionCalculationType().isEcts() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && getFixedAmount() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixedAmount.required");
        }

        if (!(getTuitionCalculationType().isEcts() && getEctsCalculationType().isDefaultPaymentPlanIndexed())
                && !isPositive(getFixedAmount())) {
            throw new AcademicTreasuryDomainException("error.TuitionInstallmentTariff.fixed.must.be.positive");
        }
    }

    // @formatter:off
    /* --------
     * SERVICES
     * --------
     */
    // @formatter:on

    public static TuitionInstallmentTariff create(final FinantialEntity finantialEntity, final Product product,
            final AcademicTariffBean bean) {
        return new TuitionInstallmentTariff(finantialEntity, product, bean);
    }

    @Override
    public LocalizedString getUiTariffDescription() {
        // TODO ANIL
        return null;
    }

}
