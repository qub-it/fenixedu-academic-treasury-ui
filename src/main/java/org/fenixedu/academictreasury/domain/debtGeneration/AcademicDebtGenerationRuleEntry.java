package org.fenixedu.academictreasury.domain.debtGeneration;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;

import pt.ist.fenixframework.Atomic;

public class AcademicDebtGenerationRuleEntry extends AcademicDebtGenerationRuleEntry_Base {

    protected AcademicDebtGenerationRuleEntry() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected AcademicDebtGenerationRuleEntry(final AcademicDebtGenerationRule rule, final Product product,
            final boolean createDebt, final boolean toCreateAfterLastRegistrationStateDate) {
        this();

        setAcademicDebtGenerationRule(rule);
        setProduct(product);
        setCreateDebt(createDebt);
        setToCreateAfterLastRegistrationStateDate(toCreateAfterLastRegistrationStateDate);

        checkRules();
    }

    public boolean isCreateDebt() {
        return super.getCreateDebt();
    }

    private void checkRules() {
        if (getBennu() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleEntry.bennu.required");
        }

        if (getAcademicDebtGenerationRule() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleEntry.academicDebtGenerationRule.required");
        }

        if (getProduct() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleEntry.product.required");
        }

        if (find(getAcademicDebtGenerationRule(), getProduct()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleEntry.product.duplicated.in.rule");
        }
    }

    private boolean isDeletable() {
        return true;
    }

    @Atomic
    public void delete() {
        if (!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRuleEntry.delete.impossible");
        }

        setBennu(null);

        setAcademicDebtGenerationRule(null);
        setProduct(null);

        super.deleteDomainObject();
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    public static Stream<AcademicDebtGenerationRuleEntry> findAll() {
        return Bennu.getInstance().getAcademicDebtGenerationRuleEntriesSet().stream();
    }

    public static Stream<AcademicDebtGenerationRuleEntry> find(final AcademicDebtGenerationRule academicDebtGenerationRule) {
        return findAll().filter(l -> l.getAcademicDebtGenerationRule() == academicDebtGenerationRule);
    }

    public static Stream<AcademicDebtGenerationRuleEntry> find(final AcademicDebtGenerationRule academicDebtGenerationRule,
            final Product product) {
        return find(academicDebtGenerationRule).filter(l -> l.getProduct() == product);
    }

    public static AcademicDebtGenerationRuleEntry create(final AcademicDebtGenerationRule rule, final Product product,
            final boolean createDebt, final boolean toCreateAfterLastRegistrationStateDate) {
        return new AcademicDebtGenerationRuleEntry(rule, product, createDebt, toCreateAfterLastRegistrationStateDate);
    }

}
