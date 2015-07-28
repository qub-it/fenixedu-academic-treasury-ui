package org.fenixedu.academictreasury.dto.debtGeneration;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.paymentcodes.pool.PaymentCodePool;

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
    
    private DegreeType degreeType;

    private List<DegreeCurricularPlan> degreeCurricularPlans = Lists.newArrayList();
    private List<DegreeCurricularPlan> degreeCurricularPlansToAdd = Lists.newArrayList();

    private Product product;
    private boolean createDebt;
    private PaymentCodePool paymentCodePool;

    private List<TupleDataSourceBean> executionYearDataSource = Lists.newArrayList();
    private List<TupleDataSourceBean> productDataSource = Lists.newArrayList();
    private List<TupleDataSourceBean> paymentCodePoolDataSource = Lists.newArrayList();

    private List<TupleDataSourceBean> degreeTypeDataSource = Lists.newArrayList();
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource = Lists.newArrayList();

    public AcademicDebtGenerationRuleBean() {
        executionYearDataSource =
                ExecutionYear.readNotClosedExecutionYears().stream()
                        .sorted(Collections.reverseOrder(ExecutionYear.COMPARATOR_BY_BEGIN_DATE))
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getQualifiedName())).collect(Collectors.toList());

        final List<Product> availableProducts = Lists.newArrayList();

        availableProducts.addAll(AcademicTax.findAll().filter(AcademicTax::isAppliedAutomatically).map(l -> l.getProduct())
                .collect(Collectors.toList()));
        availableProducts.addAll(AcademicTreasurySettings.getInstance().getTuitionProductGroup().getProductsSet());

        productDataSource =
                availableProducts.stream().sorted(Product.COMPARE_BY_NAME)
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName().getContent()))
                        .collect(Collectors.toList());

        paymentCodePoolDataSource =
                PaymentCodePool.findAll().filter(PaymentCodePool::getActive)
                        .map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName())).collect(Collectors.toList());

        degreeTypeDataSource =
                DegreeType.all().map(l -> new TupleDataSourceBean(l.getExternalId(), l.getName().getContent()))
                        .sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());

        
        
        this.aggregateOnDebitNote = false;
        this.aggregateAllOrNothing = false;
        this.closeDebitNote = false;
        this.createPaymentReferenceCode = false;

    }
    
    public void chooseDegreeType() {
        if (getExecutionYear() == null) {
            degreeCurricularPlanDataSource = Collections.<TupleDataSourceBean> emptyList();
        }

        if (getDegreeType() == null) {
            degreeCurricularPlanDataSource = Collections.<TupleDataSourceBean> emptyList();
        }

        
        final List<TupleDataSourceBean> result =
                ExecutionDegree.getAllByExecutionYearAndDegreeType(getExecutionYear(), getDegreeType()).stream()
                        .map(e -> e.getDegreeCurricularPlan())
                        .map((dcp) -> new TupleDataSourceBean(dcp.getExternalId(), dcp.getPresentationName(getExecutionYear())))
                        .collect(Collectors.toList());

        degreeCurricularPlanDataSource = result.stream().sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
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

    public void addDegreeCurricularPlans() {
        degreeCurricularPlans.addAll(degreeCurricularPlansToAdd);
        
        degreeCurricularPlansToAdd = Lists.newArrayList();
    }

    public void removeDegreeCurricularPlan(int entryIndex) {
        degreeCurricularPlans.remove(entryIndex);
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

    public DegreeType getDegreeType() {
        return degreeType;
    }
    
    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }
    
    public boolean isAggregateAllOrNothing() {
        return aggregateAllOrNothing;
    }

    public void setAggregateAllOrNothing(boolean aggregateAllOrNothing) {
        this.aggregateAllOrNothing = aggregateAllOrNothing;
    }

    public PaymentCodePool getPaymentCodePool() {
        return paymentCodePool;
    }

    public void setPaymentCodePool(PaymentCodePool paymentCodePool) {
        this.paymentCodePool = paymentCodePool;
    }
    
    public List<DegreeCurricularPlan> getDegreeCurricularPlans() {
        return degreeCurricularPlans;
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        return executionYearDataSource;
    }

    public List<TupleDataSourceBean> getProductDataSource() {
        return productDataSource;
    }

    public List<TupleDataSourceBean> getPaymentCodePoolDataSource() {
        return paymentCodePoolDataSource;
    }
    
    public List<TupleDataSourceBean> getDegreeTypeDataSource() {
        return degreeTypeDataSource;
    }
    
    public List<TupleDataSourceBean> getDegreeCurricularPlanDataSource() {
        return degreeCurricularPlanDataSource;
    }
    
}
