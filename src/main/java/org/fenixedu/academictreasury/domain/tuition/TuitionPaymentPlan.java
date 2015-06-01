package org.fenixedu.academictreasury.domain.tuition;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
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

import com.google.common.collect.Sets;

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
        setProduct(tuitionPaymentPlanBean.getTuitionPaymentPlanGroup().getCurrentProduct());
        setExecutionYear(tuitionPaymentPlanBean.getExecutionYear());
        setDegreeCurricularPlan(degreeCurricularPlan);

        setDefaultPaymentPlan(tuitionPaymentPlanBean.isDefaultPaymentPlan());
        setRegistrationRegimeType(tuitionPaymentPlanBean.getRegistrationRegimeType());
        setRegistrationProtocol(tuitionPaymentPlanBean.getRegistrationProtocol());
        setIngression(tuitionPaymentPlanBean.getIngression());
        setCurricularYear(tuitionPaymentPlanBean.getCurricularYear());
        setSemester(tuitionPaymentPlanBean.getExecutionSemester() != null ? tuitionPaymentPlanBean.getExecutionSemester().getSemester() : null);
        setFirstTimeStudent(tuitionPaymentPlanBean.isFirstTimeStudent());
        setCustomized(tuitionPaymentPlanBean.isCustomized());
        setCustomizedName(tuitionPaymentPlanBean.getName());
        setWithLaboratorialClasses(tuitionPaymentPlanBean.isWithLaboratorialClasses());
        setPaymentPlanOrder((int) find(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear()).count() + 1);

        createInstallments(tuitionPaymentPlanBean);

        checkRules();
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

        if (isDefaultPaymentPlan()
                && getTuitionPaymentPlanGroup() != TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get()) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.default.payment.plan.must.be.for.registration");
        }

        if (isDefaultPaymentPlan()) {
            for (final TuitionInstallmentTariff tuitionInstallmentTariff : getTuitionInstallmentTariffsSet()) {
                if (!tuitionInstallmentTariff.getTuitionCalculationType().isFixedAmount()) {
                    throw new AcademicTreasuryDomainException(
                            "error.TuitionPaymentPlan.default.payment.plan.tariffs.calculation.type.not.fixed.amount");
                }
            }
        }

        if (findDefaultPaymentPlans(getDegreeCurricularPlan(), getExecutionYear()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.defaultPaymentPlan.not.unique");
        }

        if (find(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear(), getPaymentPlanOrder()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.TuitionPaymentPlan.paymentPlan.with.order.already.exists");
        }
    }

    private void createInstallments(final TuitionPaymentPlanBean tuitionPaymentPlanBean) {
        for (final AcademicTariffBean academicTariffBean : tuitionPaymentPlanBean.getTuitionInstallmentBeans()) {
            TuitionInstallmentTariff.create(tuitionPaymentPlanBean.getFinantialEntity(), this, academicTariffBean);
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

    public LocalizedString installmentName(final TuitionInstallmentTariff installmentTariff) {
        String label = "label.TuitionInstallmentTariff.debitEntry.name.";

        if (getTuitionPaymentPlanGroup().isForRegistration()) {
            label += "registration";
        } else if (getTuitionPaymentPlanGroup().isForStandalone()) {
            label += "standalone";
        } else if (getTuitionPaymentPlanGroup().isForExtracurricular()) {
            label += "extracurricular";
        }

        LocalizedString result = new LocalizedString();
        for (final Locale locale : CoreConfiguration.supportedLocales()) {
            final String installmentName =
                    BundleUtil.getString(Constants.BUNDLE, label, getDegreeCurricularPlan().getDegree().getPresentationNameI18N()
                            .getContent(locale), getExecutionYear().getQualifiedName());
            
            result = result.with(locale, installmentName);
        }

        return result;
    }

    public boolean isCustomized() {
        return getCustomized();
    }

    public boolean isDefaultPaymentPlan() {
        return getDefaultPaymentPlan();
    }

    public boolean isFirst() {
        return findSortedByPaymentPlanOrder(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear()).collect(
                Collectors.toList()).get(0) == this;
    }

    public boolean isLast() {
        final List<TuitionPaymentPlan> list =
                findSortedByPaymentPlanOrder(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear())
                        .collect(Collectors.toList());

        return list.get(list.size() - 1) == this;
    }

    @Atomic
    public void orderUp() {
        if (isFirst()) {
            return;
        }

        final TuitionPaymentPlan previous =
                findUnique(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear(), getPaymentPlanOrder() - 1)
                        .get();

        int order = getPaymentPlanOrder() - 1;
        previous.setPaymentPlanOrder(getPaymentPlanOrder());
        setPaymentPlanOrder(order);
    }

    @Atomic
    public void orderDown() {
        if (isLast()) {
            return;
        }

        final TuitionPaymentPlan next =
                findUnique(getTuitionPaymentPlanGroup(), getDegreeCurricularPlan(), getExecutionYear(), getPaymentPlanOrder() + 1)
                        .get();

        int order = getPaymentPlanOrder() + 1;
        next.setPaymentPlanOrder(getPaymentPlanOrder());
        setPaymentPlanOrder(order);
    }

    public boolean createDebitEntries(final PersonCustomer personCustomer, final AcademicTreasuryEvent academicTreasuryEvent) {

        boolean createdDebitEntries = false;
        for (final TuitionInstallmentTariff tariff : getTuitionInstallmentTariffsSet()) {
            if (!academicTreasuryEvent.isChargedWithDebitEntry(tariff)) {
                tariff.createDebitEntry(personCustomer, academicTreasuryEvent);
                createdDebitEntries = true;
            }
        }
        
        return createdDebitEntries;
    }

    public boolean isDeletable() {
        return true;
    }

    public BigDecimal tuitionTotalAmount() {
        return getTuitionInstallmentTariffsSet().stream().map(t -> t.getFixedAmount()).reduce((a, c) -> a.add(c))
                .orElse(BigDecimal.ZERO);
    }

    @Atomic
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

    public static Stream<TuitionPaymentPlan> findSortedByPaymentPlanOrder(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionYear executionYear) {
        return find(tuitionPaymentPlanGroup, degreeCurricularPlan, executionYear).sorted(
                (e1, e2) -> Integer.compare(e1.getPaymentPlanOrder(), e2.getPaymentPlanOrder()));
    }

    protected static Stream<TuitionPaymentPlan> find(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionYear executionYear, final int paymentPlanOrder) {
        return find(tuitionPaymentPlanGroup, degreeCurricularPlan, executionYear).filter(
                t -> t.getPaymentPlanOrder() == paymentPlanOrder);
    }

    protected static Optional<TuitionPaymentPlan> findUnique(final TuitionPaymentPlanGroup tuitionPaymentPlanGroup,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionYear executionYear, final int paymentPlanOrder) {
        return find(tuitionPaymentPlanGroup, degreeCurricularPlan, executionYear, paymentPlanOrder).findFirst();
    }

    private static Stream<TuitionPaymentPlan> findDefaultPaymentPlans(final DegreeCurricularPlan degreeCurricularPlan,
            final ExecutionYear executionYear) {
        return find(TuitionPaymentPlanGroup.findUniqueDefaultGroupForRegistration().get(), degreeCurricularPlan, executionYear)
                .filter(t -> t.isDefaultPaymentPlan());
    }

    public static Optional<TuitionPaymentPlan> findUniqueDefaultPaymentPlan(final DegreeCurricularPlan degreeCurricularPlan,
            final ExecutionYear executionYear) {
        return findDefaultPaymentPlans(degreeCurricularPlan, executionYear).findFirst();
    }

    public static boolean isDefaultPaymentPlanDefined(final DegreeCurricularPlan degreeCurricularPlan,
            final ExecutionYear executionYear) {
        return findUniqueDefaultPaymentPlan(degreeCurricularPlan, executionYear).isPresent();
    }

    @Atomic
    public static Set<TuitionPaymentPlan> create(final TuitionPaymentPlanBean tuitionPaymentPlanBean) {
        final Set<TuitionPaymentPlan> result = Sets.newHashSet();

        for (final DegreeCurricularPlan degreeCurricularPlan : tuitionPaymentPlanBean.getDegreeCurricularPlans()) {
            result.add(new TuitionPaymentPlan(degreeCurricularPlan, tuitionPaymentPlanBean));
        }

        return result;
    }

}