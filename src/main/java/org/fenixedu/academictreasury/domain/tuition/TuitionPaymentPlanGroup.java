package org.fenixedu.academictreasury.domain.tuition;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.util.LocalizedStringUtil;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

public class TuitionPaymentPlanGroup extends TuitionPaymentPlanGroup_Base {

    public static final Comparator<TuitionPaymentPlanGroup> COMPARE_BY_NAME = new Comparator<TuitionPaymentPlanGroup>() {

        @Override
        public int compare(TuitionPaymentPlanGroup o1, TuitionPaymentPlanGroup o2) {
            int c = o1.getName().getContent().compareTo(o2.getName().getContent());

            return c != 0 ? c : o1.getExternalId().compareTo(o2.getExternalId());
        }
    };

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

        if (!(isForRegistration() ^ isForStandalone() ^ isForExtracurricular())) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.only.one.type.supported");
        }

        if (findDefaultGroupForRegistration().count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.for.registration.already.exists");
        }

        if (findDefaultGroupForStandalone().count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.for.standalone.already.exists");
        }

        if (findDefaultGroupForExtracurricular().count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.for.extracurricular.already.exists");
        }

        if (findByCode(getCode()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.code.already.exists");
        }
    }

    @Atomic
    public void edit(final String code, final LocalizedString name, final boolean forRegistration, final boolean forStandalone,
            final boolean forExtracurricular, final Product currentProduct) {
        setCode(code);
        setName(name);
        setForRegistration(forRegistration);
        setForStandalone(forStandalone);
        setForExtracurricular(forExtracurricular);
        setCurrentProduct(currentProduct);

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

    public boolean isForImprovement() {
        return getForImprovement();
    }

    public boolean isDeletable() {
        // ACFSILVA
        return getAcademicTreasuryEventSet().isEmpty() && getTuitionPaymentPlansSet().isEmpty();
    }

    @Atomic
    public void delete() {
        if (!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlanGroup.delete.impossible");
        }

        setBennu(null);

        super.deleteDomainObject();
    }

    public static Stream<TuitionPaymentPlanGroup> findAll() {
        return Bennu.getInstance().getTuitionPaymentPlanGroupsSet().stream();
    }

    protected static Stream<TuitionPaymentPlanGroup> findDefaultGroupForRegistration() {
        return findAll().filter(t -> t.isForRegistration());
    }

    protected static Stream<TuitionPaymentPlanGroup> findDefaultGroupForStandalone() {
        return findAll().filter(t -> t.isForStandalone());
    }

    protected static Stream<TuitionPaymentPlanGroup> findDefaultGroupForImprovement() {
        return findAll().filter(t -> t.isForImprovement());
    }

    protected static Stream<TuitionPaymentPlanGroup> findDefaultGroupForExtracurricular() {
        return findAll().filter(t -> t.isForExtracurricular());
    }

    protected static Stream<TuitionPaymentPlanGroup> findByCode(final String code) {
        return findAll()
                .filter(l -> StringNormalizer.normalize(l.getCode().toLowerCase()).equals(
                        StringNormalizer.normalize(code).toLowerCase()));
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

    public static Optional<TuitionPaymentPlanGroup> findUniqueDefaultGroupForImprovement() {
        return findAll().filter(t -> t.isForImprovement()).findFirst();
    }

    @Atomic
    public static TuitionPaymentPlanGroup create(final String code, final LocalizedString name, boolean forRegistration,
            boolean forStandalone, boolean forExtracurricular, final Product currentProduct) {
        return new TuitionPaymentPlanGroup(code, name, forRegistration, forStandalone, forExtracurricular, currentProduct);
    }

}
