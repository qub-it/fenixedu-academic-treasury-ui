package org.fenixedu.academictreasury.domain.settings;

import java.util.Optional;

import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.ProductGroup;

import pt.ist.fenixframework.Atomic;

public class AcademicTreasurySettings extends AcademicTreasurySettings_Base {

    protected AcademicTreasurySettings() {
        super();
        setBennu(Bennu.getInstance());
    }

    @Atomic
    public void edit(final ProductGroup emolumentsProductGroup, final ProductGroup tuitionProductGroup, final AcademicTax improvementAcademicTax) {
        setEmolumentsProductGroup(emolumentsProductGroup);
        setTuitionProductGroup(tuitionProductGroup);
        setImprovementAcademicTax(improvementAcademicTax);
    }
    
    @Atomic
    public void addAcademicalActBlockingProduct(final Product product) {
        addAcademicalActBlockingProducts(product);
    }
    
    @Atomic
    public void removeAcademicalActBlockingProduct(final Product product) {
        removeAcademicalActBlockingProducts(product);
    }

    public boolean isAcademicalActBlocking(final Product product) {
        return getAcademicalActBlockingProductsSet().contains(product);
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    protected static Optional<AcademicTreasurySettings> find() {
        return Bennu.getInstance().getAcademicTreasurySettingsSet().stream().findFirst();
    }

    @Atomic
    public static AcademicTreasurySettings getInstance() {
        if (!find().isPresent()) {
            return new AcademicTreasurySettings();
        }

        return find().get();
    }

}
