package org.fenixedu.academictreasury.dto.debtGeneration;

import java.io.Serializable;
import java.util.List;

import org.fenixedu.bennu.IBean;
import org.fenixedu.treasury.domain.Product;

import com.google.common.collect.Lists;

public class AcademicDebtGenerationRuleBean implements Serializable, IBean {

    private static final long serialVersionUID = 1L;

    public static class ProductEntry implements IBean, Serializable {

        private static final long serialVersionUID = 1L;

        private Product product;
        private boolean createDebt;

        public ProductEntry(Product product, boolean createDebt) {
            this.product = product;
            this.createDebt = createDebt;
        }

        public Product getProduct() {
            return product;
        }

        public boolean isCreateDebt() {
            return createDebt;
        }
    }

    private boolean aggregateOnDebitNote;
    private boolean closeDebitNote;
    private boolean createPaymentReferenceCode;
    
    private List<ProductEntry> entries = Lists.newArrayList();
    
    private Product product;
    private boolean createDebt;
    
    
    public void addEntry(final Product product, final boolean createDebt) {
        entries.add(new ProductEntry(product, createDebt));
    }
    
    public void removEntry(final int index) {
        entries.remove(index);
    }
    
    public boolean isAggregateOnDebitNote() {
        return aggregateOnDebitNote;
    }

    public void setAggregateOnDebitNote(boolean aggregateOnDebitNote) {
        this.aggregateOnDebitNote = aggregateOnDebitNote;
    }

    public boolean isCloseDebitNote() {
        return closeDebitNote;
    }

    public void setCloseDebitNote(boolean closeDebitNote) {
        this.closeDebitNote = closeDebitNote;
    }

    public boolean isCreatePaymentReferenceCode() {
        return createPaymentReferenceCode;
    }

    public void setCreatePaymentReferenceCode(boolean createPaymentReferenceCode) {
        this.createPaymentReferenceCode = createPaymentReferenceCode;
    }
    
    public List<ProductEntry> getEntries() {
        return entries;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public boolean isCreateDebt() {
        return createDebt;
    }
    
    public void setCreateDebt(boolean createDebt) {
        this.createDebt = createDebt;
    }

}
