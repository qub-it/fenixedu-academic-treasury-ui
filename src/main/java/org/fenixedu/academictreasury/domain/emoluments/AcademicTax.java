package org.fenixedu.academictreasury.domain.emoluments;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;

import pt.ist.fenixframework.Atomic;

public class AcademicTax extends AcademicTax_Base {

    public static final Comparator<AcademicTax> COMPARE_BY_PRODUCT_NAME = new Comparator<AcademicTax>() {
        
        @Override
        public int compare(final AcademicTax o1, final AcademicTax o2) {
            int c = o1.getProduct().getName().getContent().compareTo(o2.getProduct().getName().getContent());
            
            return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

    protected AcademicTax() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected AcademicTax(final Product product, final boolean appliedOnRegistration,
            final boolean appliedOnRegistrationFirstYear, final boolean appliedOnRegistrationSubsequentYears, final boolean appliedAutomatically) {
        this();

        setProduct(product);
        setAppliedOnRegistration(appliedOnRegistration);
        setAppliedOnRegistrationFirstYear(appliedOnRegistrationFirstYear);
        setAppliedOnRegistrationSubsequentYears(appliedOnRegistrationSubsequentYears);
        setAppliedAutomatically(appliedAutomatically);

        checkRules();
    }

    private void checkRules() {
        if (getBennu() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTax.bennu.required");
        }

        if (getProduct() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicTax.product.required");
        }

        if (find(getProduct()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTax.for.product.already.exists");
        }

        if (!isAppliedOnRegistrationFirstYear() && !isAppliedOnRegistrationSubsequentYears()) {
            throw new AcademicTreasuryDomainException("error.AcademicTax.must.be.applied.on.some.registration.enrolment.year");
        }
    }

    public boolean isAppliedOnRegistration() {
        return super.getAppliedOnRegistration();
    }

    public boolean isAppliedOnRegistrationFirstYear() {
        return super.getAppliedOnRegistrationFirstYear();
    }

    public boolean isAppliedOnRegistrationSubsequentYears() {
        return super.getAppliedOnRegistrationSubsequentYears();
    }
    
    public boolean isAppliedAutomatically() {
        return super.getAppliedAutomatically();
    }
    
    @Atomic
    public void edit(boolean appliedOnRegistration, boolean appliedOnRegistrationFirstYear,
            boolean appliedOnRegistrationSubsequentYears, final boolean appliedAutomatically) {
        setAppliedOnRegistration(appliedOnRegistration);
        setAppliedOnRegistrationFirstYear(appliedOnRegistrationFirstYear);
        setAppliedOnRegistrationSubsequentYears(appliedOnRegistrationSubsequentYears);
        setAppliedAutomatically(appliedAutomatically);
        
        checkRules();
    }

    private boolean isDeletable() {
        return getAcademicTreasuryEventSet().isEmpty();
    }

    @Atomic
    public void delete() {
        if(!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.AcademicTax.delete.impossible");
        }
        
        setBennu(null);
        setProduct(null);
        
        super.deleteDomainObject();
    }
    
    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    public static Stream<AcademicTax> findAll() {
        return Bennu.getInstance().getAcademicTaxesSet().stream();
    }

    public static Stream<AcademicTax> find(final Product product) {
        return findAll().filter(a -> a.getProduct() == product);
    }

    public static Optional<AcademicTax> findUnique(final Product product) {
        return find(product).findFirst();
    }

    @Atomic
    public static AcademicTax create(final Product product, final boolean appliedOnRegistration,
            final boolean appliedOnRegistrationFirstYear, final boolean appliedOnRegistrationSubsequentYears, final boolean appliedAutomatically) {
        return new AcademicTax(product, appliedOnRegistration, appliedOnRegistrationFirstYear,
                appliedOnRegistrationSubsequentYears, appliedAutomatically);
    }
}
