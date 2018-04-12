package org.fenixedu.academictreasury.dto.tuition;

import java.math.BigDecimal;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Currency;
import org.joda.time.LocalDate;

public class TuitionDebitEntryBean {

    private int installmentOrder;
    private LocalizedString description;
    private LocalDate dueDate;
    private BigDecimal vatRate;
    private BigDecimal amount;
    private Currency currency;

    public TuitionDebitEntryBean(final int installmentOrder, final LocalizedString description, final LocalDate dueDate,
            final BigDecimal vatRate, final BigDecimal amount, final Currency currency) {
        super();
        this.installmentOrder = installmentOrder;
        this.description = description;
        this.dueDate = dueDate;
        this.vatRate = vatRate;
        this.amount = amount;
        this.currency = currency;
    }

    public int getInstallmentOrder() {
        return installmentOrder;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    
    public Currency getCurrency() {
        return currency;
    }

}
