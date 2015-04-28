package org.fenixedu.academicTreasury.services;

import java.util.stream.Stream;

import org.fenixedu.academicTreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academicTreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;

import pt.ist.fenixframework.Atomic;

public class EmolumentServices {
    
    @Atomic
    public static Product createEmolument(final String code, final LocalizedString name) {
        if(AcademicTreasurySettings.getInstance().getEmolumentsProductGroup() == null) {
            throw new AcademicTreasuryDomainException("error.EmolumentServices.emoluments.product.group.not.defined");
        }
        
        return Product.create(AcademicTreasurySettings.getInstance().getEmolumentsProductGroup(), code, name, Product.defaultUnitOfMeasure(), true);
    }
    
    
    public static Stream<Product> findEmoluments() {
        if(AcademicTreasurySettings.getInstance().getEmolumentsProductGroup() == null) {
            throw new AcademicTreasuryDomainException("error.EmolumentServices.emoluments.product.group.not.defined");
        }
        
        return AcademicTreasurySettings.getInstance().getEmolumentsProductGroup().getProductsSet().stream();
    }
}
