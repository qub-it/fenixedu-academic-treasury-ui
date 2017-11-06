package org.fenixedu.academictreasury.domain.integration.tuitioninfo;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;

public class ERPTuitionInfoType extends ERPTuitionInfoType_Base {

    public static final Comparator<ERPTuitionInfoType> COMPARE_BY_NAME = new Comparator<ERPTuitionInfoType>() {

        @Override
        public int compare(final ERPTuitionInfoType o1, final ERPTuitionInfoType o2) {
            int c = o1.getErpTuitionInfoProduct().getName().compareTo(o2.getErpTuitionInfoProduct().getName());

            return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
        }

    };

    public ERPTuitionInfoType() {
        super();
        setBennu(Bennu.getInstance());
        setErpTuitionInfoSettings(ERPTuitionInfoSettings.getInstance());
        setActive(true);
    }

    private ERPTuitionInfoType(final ExecutionYear executionYear, final ERPTuitionInfoProduct product,
            final Set<Product> tuitionProducts) {
        this();

        setExecutionYear(executionYear);
        setErpTuitionInfoProduct(product);

        getTuitionProductsSet().addAll(tuitionProducts);

        checkRules();
    }

    private void checkRules() {

        if (getBennu() == null) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.bennu.required");
        }

        if (getExecutionYear() == null) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.executionYear.required");
        }

        if (getTuitionProductsSet().isEmpty()) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.tuitionProducts.required");
        }
        
    }

    public boolean isActive() {
        return getActive();
    }

    public Degree getDegree() {
        return null;
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return null;
    }

    public void delete() {
        if (!getErpTuitionInfosSet().isEmpty()) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.delete.not.possible");
        }

        setBennu(null);
        setErpTuitionInfoProduct(null);
        setErpTuitionInfoSettings(null);
        setExecutionYear(null);
        getTuitionProductsSet().clear();

        while(!getErpTuitionInfoTypeAcademicEntriesSet().isEmpty()) {
            getErpTuitionInfoTypeAcademicEntriesSet().iterator().next().delete();
        }
        
        deleteDomainObject();
    }

    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on

    public static Stream<ERPTuitionInfoType> findAll() {
        return ERPTuitionInfoSettings.getInstance().getErpTuitionInfoTypesSet().stream();
    }

    public static Stream<ERPTuitionInfoType> findActive() {
        return findAll().filter(e -> e.isActive());
    }
    
    public static Stream<? extends ERPTuitionInfoType> findForExecutionYear(final ExecutionYear executionYear) {
        return executionYear.getErpTuitionInfoTypesSet().stream();
    }

    public static Stream<ERPTuitionInfoType> findActiveForExecutionYear(final ExecutionYear executionYear) {
        return executionYear.getErpTuitionInfoTypesSet().stream().filter(e -> e.isActive());
    }

    public static Stream<ERPTuitionInfoType> findByCode(final String code) {
        return findAll().filter(e -> e.getErpTuitionInfoProduct().getCode().equals(code));
    }

    public static Optional<ERPTuitionInfoType> findUniqueByCode(final String code) {
        return findAll().filter(e -> e.getErpTuitionInfoProduct().getCode().equals(code)).findFirst();
    }

    public static ERPTuitionInfoType create(final ExecutionYear executionYear, final ERPTuitionInfoProduct product,
            final Set<Product> tuitionProducts) {
        return new ERPTuitionInfoType(executionYear, product, tuitionProducts);
    }

}
