package org.fenixedu.academictreasury.domain.tuition;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.util.LocalizedStringUtil;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Strings;

public class TuitionPaymentPlanGroup extends TuitionPaymentPlanGroup_Base {
    
    protected TuitionPaymentPlanGroup() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    protected TuitionPaymentPlanGroup(final String code, final LocalizedString name) {
        this();
        setCode(code);
        setName(name);
        
        checkRules();
    }

    private void checkRules() {
        if(Strings.isNullOrEmpty(getCode())) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.code.required");
        }
        
        if(LocalizedStringUtil.isTrimmedEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.name.required");
        }
    }
    
    @Atomic
    public void edit(final String code, final LocalizedString name) {
        setCode(code);
        setName(name);
        
        checkRules();
    }
    
    public boolean isDeletable() {
        
        
        return true;
    }
    
    @Atomic
    public void delete() {
        if(!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.delete.impossible");
        }
        
        setBennu(null);
        
        super.deleteDomainObject();
    }
    
    /* --------
     * SERVICES
     * --------
     */
    
    public static Stream<TuitionPaymentPlanGroup> findAll() {
        return Bennu.getInstance().getTuitionPaymentPlanGroupsSet().stream();
    }
    
    public static TuitionPaymentPlanGroup findDefaultGroupForRegistration() {
        return AcademicTreasurySettings.getInstance().getTuitionPaymentPlanGroupForRegistration();
    }
    
    public static TuitionPaymentPlanGroup findDefaultGroupForStandalone() {
        return AcademicTreasurySettings.getInstance().getTuitionPaymentPlanGroupForStandalone();
    }
    
    public static TuitionPaymentPlanGroup findDefaultGroupForExtracurricular() {
        return AcademicTreasurySettings.getInstance().getTuitionPaymentPlanGroupForExtracurricular();
    }
    
    @Atomic
    public static TuitionPaymentPlanGroup create(final String code, final LocalizedString name) {
        return new TuitionPaymentPlanGroup(code, name);
    }
    
}
