package org.fenixedu.academictreasury.domain.tuition;

import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.util.LocalizedStringUtil;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Strings;

public class TuitionPaymentPlanGroup extends TuitionPaymentPlanGroup_Base {

    protected TuitionPaymentPlanGroup() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected TuitionPaymentPlanGroup(final String code, final LocalizedString name, boolean forRegistration,
            boolean forStandalone, boolean forExtracurricular, final Product currentProduct) {
        this();
        setCode(code);
        setName(name);

        setForRegistration(forRegistration);
        setForStandalone(forStandalone);
        setForExtracurricular(forExtracurricular);
        setCurrentProduct(currentProduct);
        
        checkRules();
    }

    private void checkRules() {
        if (Strings.isNullOrEmpty(getCode())) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.code.required");
        }

        if (LocalizedStringUtil.isTrimmedEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.name.required");
        }
        
        if(!(isForRegistration() ^ isForStandalone() ^ isForExtracurricular())) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.only.one.type.supported");
        }
        
        if(findDefaultGroupForRegistration().count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.for.registration.already.exists");
        }

        if(findDefaultGroupForStandalone().count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.for.standalone.already.exists");
        }

        if(findDefaultGroupForExtracurricular().count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.for.extracurricular.already.exists");
        }
    }

    @Atomic
    public void edit(final String code, final LocalizedString name) {
        setCode(code);
        setName(name);

        checkRules();
    }

    public boolean isForRegistration() {
        return getForRegistration();
    }

    public boolean isForStandalone() {
        return getForStandalone();
    }

    public boolean isForExtracurricular() {
        return getForExtracurricular();
    }

    public boolean isDeletable() {
        return true;
    }

    @Atomic
    public void delete() {
        if (!isDeletable()) {
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

    protected static Stream<TuitionPaymentPlanGroup> findDefaultGroupForRegistration() {
        return findAll().filter(t -> t.isForRegistration());
    }

    protected static Stream<TuitionPaymentPlanGroup> findDefaultGroupForStandalone() {
        return findAll().filter(t -> t.isForStandalone());
    }

    protected static Stream<TuitionPaymentPlanGroup> findDefaultGroupForExtracurricular() {
        return findAll().filter(t -> t.isForExtracurricular());
    }
    
    public static Optional<TuitionPaymentPlanGroup> findUniqueDefaultGroupForRegistration() {
        return findAll().filter(t -> t.isForRegistration()).findFirst();
    }

    public static Optional<TuitionPaymentPlanGroup> findUniqueDefaultGroupForStandalone() {
        return findAll().filter(t -> t.isForStandalone()).findFirst();
    }

    public static Optional<TuitionPaymentPlanGroup> findUniqueDefaultGroupForExtracurricular() {
        return findAll().filter(t -> t.isForExtracurricular()).findFirst();
    }

    @Atomic
    public static TuitionPaymentPlanGroup create(final String code, final LocalizedString name, boolean forRegistration,
            boolean forStandalone, boolean forExtracurricular, final Product currentProduct) {
        return new TuitionPaymentPlanGroup(code, name, forRegistration, forStandalone, forExtracurricular, currentProduct);
    }

}
