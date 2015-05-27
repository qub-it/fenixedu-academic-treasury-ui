package org.fenixedu.academictreasury.domain.tuition;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.dto.tariff.TuitionPaymentPlanBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.academictreasury.util.LocalizedStringUtil;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;

import pt.ist.fenixframework.Atomic;

public class TuitionPaymentPlan extends TuitionPaymentPlan_Base {

    protected TuitionPaymentPlan() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected TuitionPaymentPlan(final DegreeCurricularPlan degreeCurricularPlan,
            final TuitionPaymentPlanBean tuitionPaymentPlanBean) {
        this();

        setTuitionPaymentPlanGroup(tuitionPaymentPlanBean.getTuitionPaymentPlanGroup());
        setExecutionYear(tuitionPaymentPlanBean.getExecutionYear());
        setDegreeCurricularPlan(degreeCurricularPlan);

        setDefaultPaymentPlan(tuitionPaymentPlanBean.isDefaultPaymentPlan());
        setRegistrationRegimeType(tuitionPaymentPlanBean.getRegistrationRegimeType());
        setRegistrationProtocol(tuitionPaymentPlanBean.getRegistrationProtocol());
        setIngression(tuitionPaymentPlanBean.getIngression());
        setCurricularYear(tuitionPaymentPlanBean.getCurricularYear());
        setCurricularSemester(tuitionPaymentPlanBean.getCurricularSemester());
        setFirstTimeStudent(tuitionPaymentPlanBean.isFirstTimeStudent());
        setCustomized(tuitionPaymentPlanBean.isCustomized());
        setCustomizedName(tuitionPaymentPlanBean.getName());
        setWithLaboratorialClasses(tuitionPaymentPlanBean.isWithLaboratorialClasses());

        checkRules();

        createInstallments(tuitionPaymentPlanBean);
    }

    private void checkRules() {
        if (getTuitionPaymentPlanGroup() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.tuitionPaymentPlanGroup.required");
        }

        if (getExecutionYear() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.executionYear.required");
        }

        if (getDegreeCurricularPlan() == null) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.degreeCurricularPlan.required");
        }

        if (isCustomized() && LocalizedStringUtil.isTrimmedEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.customized.required.name");
        }

        if (findDefaultPaymentPlans(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.defaultPaymentPlan.not.unique");
        }

    }

    private void createInstallments(final TuitionPaymentPlanBean tuitionPaymentPlanBean) {
        for (final AcademicTariffBean academicTariffBean : tuitionPaymentPlanBean.getTuitionInstallmentBeans()) {
            TuitionInstallmentTariff.create(tuitionPaymentPlanBean.getFinantialEntity(), tuitionPaymentPlanBean.getProduct(),
                    academicTariffBean);
        }
    }

    public LocalizedString getName() {
        LocalizedString result = new LocalizedString();

        for (final Locale locale : CoreConfiguration.supportedLocales()) {
            final String paymentPlanLabel =
                    isCustomized() ? "label.TuitionPaymentPlan.paymentPlanName.customized" : "label.TuitionPaymentPlan.paymentPlanName";

            result =
                    result.with(locale, BundleUtil.getString(Constants.BUNDLE, paymentPlanLabel, getDegreeCurricularPlan()
                            .getDegree().getPresentationNameI18N().getContent(locale), getExecutionYear().getQualifiedName(),
                            getCustomizedName().getContent(locale)));
        }

        return result;
    }

    public boolean isCustomized() {
        return getCustomized();
    }

    public Optional<TuitionPaymentPlan> defaultPaymentPlan() {
        return findUniqueDefaultPaymentPlan(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear());
    }

    public boolean isDefaultPaymentPlan() {
        return getDefaultPaymentPlan();
    }

    public boolean isDefaultPaymentPlanDefined() {
        return defaultPaymentPlan().isPresent();
    }

    public boolean isDeletable() {
        return true;
    }

    public void delete() {
        if (!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.delete.impossible");
        }

        setBennu(null);

        while (!getTuitionInstallmentTariffsSet().isEmpty()) {
            getTuitionInstallmentTariffsSet().iterator().next().delete();
        }

        super.setTuitionPaymentPlanGroup(null);
        super.setExecutionYear(null);
        super.setDegreeCurricularPlan(null);
        super.setRegistrationProtocol(null);

        super.deleteDomainObject();
    }

    // @formatter:off
    /* -------------
     * OTHER METHODS
     * -------------
     */
    // @formatter:on

    protected FinantialEntity finantialEntity() {
        // TODO ANIL
        return FinantialEntity.findAll().findFirst().get();
    }

    // @formatter:off
    /* --------
     * SERVICES
     * --------
     */
    // @formatter:on

    public static Stream<TuitionPaymentPlan> findAll() {
        return Bennu.getInstance().getTuitionPaymentPlansSet().stream();
    }

    public static Stream<TuitionPaymentPlan> find(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup) {
        return findAll().filter(t -> t.getTuitionPaymentPlanGroup() == tuitionPaymentPlanGroup);
    }

    public static Stream<TuitionPaymentPlan> find(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final FinantialEntity finantialEntity, final ExecutionYear executionYear) {
        return find(tuitionPaymentPlanGroup).filter(t -> t.finantialEntity() == finantialEntity);
    }

    public static Stream<TuitionPaymentPlan> find(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionYear executionYear) {

        return find(tuitionPaymentPlanGroup).filter(
                t -> t.getExecutionYear() == executionYear && t.getDegreeCurricularPlan() == degreeCurricularPlan);
    }

    private static Stream<TuitionPaymentPlan> findDefaultPaymentPlans(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionYear executionYear) {
        return find(tuitionPaymentPlanGroup, degreeCurricularPlan, executionYear).filter(t -> t.isDefaultPaymentPlan());
    }

    public static Optional<TuitionPaymentPlan> findUniqueDefaultPaymentPlan(
            final TuitionPaymentPlanGroup tuitionPaymentPlanGroup, final DegreeCurricularPlan degreeCurricularPlan,
            final ExecutionYear executionYear) {
        return findDefaultPaymentPlans(tuitionPaymentPlanGroup, degreeCurricularPlan, executionYear).findFirst();
    }

    @Atomic
    public static TuitionPaymentPlan create(final DegreeCurricularPlan degreeCurricularPlan,
            final TuitionPaymentPlanBean tuitionPaymentPlanBean) {
        return new TuitionPaymentPlan(degreeCurricularPlan, tuitionPaymentPlanBean);
    }

}