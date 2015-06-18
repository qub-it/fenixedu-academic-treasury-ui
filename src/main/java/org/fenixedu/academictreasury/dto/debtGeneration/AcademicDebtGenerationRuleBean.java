package org.fenixedu.academictreasury.dto.debtGeneration;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
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

    private ExecutionYear executionYear;
    private boolean aggregateOnDebitNote;
    private boolean aggregateAllOrNothing;
    private boolean closeDebitNote;
    private boolean createPaymentReferenceCode;

    private List<ProductEntry> entries = Lists.newArrayList();

    private Product product;
    private boolean createDebt;

    private List<TupleDataSourceBean> executionYearDataSource = Lists.newArrayList();
    private List<TupleDataSourceBean> productDataSource = Lists.newArrayList();

    public AcademicDebtGenerationRuleBean() {
        executionYearDataSource =
                ExecutionYear.readNotClosedExecutionYears().stream()
                        .sorted(Collections.reverseOrder(ExecutionYear.COMPARATOR_BY_BEGIN_DATE))
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getQualifiedName())).collect(Collectors.toList());

        final List<Product> availableProducts = Lists.newArrayList();

        availableProducts.addAll(AcademicTax.findAll().map(l -> l.getProduct()).collect(Collectors.toList()));
        availableProducts.addAll(AcademicTreasurySettings.getInstance().getTuitionProductGroup().getProductsSet());

        productDataSource =
                availableProducts.stream().sorted(Product.COMPARE_BY_NAME)
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName().getContent()))
                        .collect(Collectors.toList());

        this.aggregateOnDebitNote = false;
        this.aggregateAllOrNothing = false;
        this.closeDebitNote = false;
        this.createPaymentReferenceCode = false;

    }

    public void addEntry() {
        if (product == null) {
            return;
        }

        if (entries.stream().filter(l -> l.getProduct() == product).count() > 0) {
            return;
        }

        entries.add(new ProductEntry(this.product, this.createDebt));

        this.product = null;
        this.createDebt = false;
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

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public boolean isAggregateAllOrNothing() {
        return aggregateAllOrNothing;
    }

    public void setAggregateAllOrNothing(boolean aggregateAllOrNothing) {
        this.aggregateAllOrNothing = aggregateAllOrNothing;
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        return executionYearDataSource;
    }

    public List<TupleDataSourceBean> getProductDataSource() {
        return productDataSource;
    }

}
