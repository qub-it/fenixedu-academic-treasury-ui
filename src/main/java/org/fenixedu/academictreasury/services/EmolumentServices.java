package org.fenixedu.academictreasury.services;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.VatType;

import pt.ist.fenixframework.Atomic;

public class EmolumentServices {

    @Atomic
    public static Product createEmolument(final String code, final LocalizedString name, final VatType vatType) {
        if (AcademicTreasurySettings.getInstance().getEmolumentsProductGroup() == null) {
            throw new AcademicTreasuryDomainException("error.EmolumentServices.emoluments.product.group.not.defined");
        }

        return Product.create(AcademicTreasurySettings.getInstance().getEmolumentsProductGroup(), code, name,
                Product.defaultUnitOfMeasure(), true, vatType);
    }

    public static Stream<Product> findEmoluments() {
        if (AcademicTreasurySettings.getInstance().getEmolumentsProductGroup() == null) {
            throw new AcademicTreasuryDomainException("error.EmolumentServices.emoluments.product.group.not.defined");
        }

        return AcademicTreasurySettings.getInstance().getEmolumentsProductGroup().getProductsSet().stream();
    }
}
