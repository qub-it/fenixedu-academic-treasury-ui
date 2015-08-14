package org.fenixedu.academictreasury.domain.listeners;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.treasury.domain.Product;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public class ProductDeletionListener {
    
    public static void attach() {
        
        FenixFramework.getDomainModel().registerDeletionListener(Product.class, new DeletionListener<Product>() {

            @Override
            public void deleting(final Product product) {
                if(product.getAcademicTax() != null) {
                    throw new AcademicTreasuryDomainException("error.Product.cannot.delete.due.to.academic.tax");
                }
                
                AcademicTreasurySettings.getInstance().removeAcademicalActBlockingProduct(product);
            }
        });
    }
    
}
