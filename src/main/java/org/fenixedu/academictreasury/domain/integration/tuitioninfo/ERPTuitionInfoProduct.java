package org.fenixedu.academictreasury.domain.integration.tuitioninfo;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;

import com.google.common.base.Strings;

public class ERPTuitionInfoProduct extends ERPTuitionInfoProduct_Base {
    
    public ERPTuitionInfoProduct() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    protected ERPTuitionInfoProduct(final String code, final String name) {
        this();
        
        setCode(code);
        setName(name);
        
        checkRules();
    }

    private void checkRules() {
        if (getBennu() == null) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.bennu.required");
        }

        if (Strings.isNullOrEmpty(getCode())) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.code.required");
        }

        if (Strings.isNullOrEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.name.required");
        }

        if (findByCode(getCode()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.ERPTuitionInfoType.code.not.unique");
        }

    }
    
    public static Stream<ERPTuitionInfoProduct> findAll() {
        return Bennu.getInstance().getErpTuitionInfoProductsSet().stream();
    }

    public static Stream<ERPTuitionInfoProduct> findByCode(final String code) {
        return findAll().filter(p -> p.getCode().equals(code));
    }
    
    public static ERPTuitionInfoProduct create(final String code, final String name) {
        return new ERPTuitionInfoProduct(code, name);
    }
    
}
