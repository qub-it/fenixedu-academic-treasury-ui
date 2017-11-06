package org.fenixedu.academictreasury.domain.integration.tuitioninfo;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;

import com.google.common.base.Strings;

public class ERPTuitionInfoType extends ERPTuitionInfoType_Base {

    public static final Comparator<ERPTuitionInfoType> COMPARE_BY_NAME = new Comparator<ERPTuitionInfoType>() {

        @Override
        public int compare(final ERPTuitionInfoType o1, final ERPTuitionInfoType o2) {
            int c = o1.getName().compareTo(o2.getName());

            return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
        }

    };

    public ERPTuitionInfoType() {
        super();
        setBennu(Bennu.getInstance());
        setErpTuitionInfoSettings(ERPTuitionInfoSettings.getInstance());
        setActive(true);
    }

    private ERPTuitionInfoType(final ExecutionYear executionYear, final String code, final String name,
            final Set<Product> tuitionProducts) {
        this();

        setExecutionYear(executionYear);
        setCode(code);
        setName(name);

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

        if (Strings.isNullOrEmpty(getCode())) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.code.required");
        }

        if (Strings.isNullOrEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.name.required");
        }

        if (getTuitionProductsSet().isEmpty()) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.tuitionProducts.required");
        }

        if (findByCode(getCode()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.code.not.unique");
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
        return findAll().filter(e -> e.getCode().equals(code));
    }

    public static Optional<ERPTuitionInfoType> findUniqueByCode(final String code) {
        return findAll().filter(e -> e.getCode().equals(code)).findFirst();
    }

    public static ERPTuitionInfoType create(final ExecutionYear executionYear, final String code, final String name,
            final Set<Product> tuitionProducts) {
        return new ERPTuitionInfoType(executionYear, code, name, tuitionProducts);
    }

}
