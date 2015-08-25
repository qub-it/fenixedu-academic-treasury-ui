package org.fenixedu.academictreasury.dto.tariff;

import java.io.Serializable;
import java.math.BigDecimal;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.academictreasury.domain.tuition.EctsCalculationType;
import org.fenixedu.academictreasury.domain.tuition.TuitionCalculationType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.joda.time.LocalDate;

public class AcademicTariffBean implements IBean, Serializable {

    private static final long serialVersionUID = 1L;

    /* Tariff */
    private LocalDate beginDate;
    private LocalDate endDate;
    private DueDateCalculationType dueDateCalculationType;
    private LocalDate fixedDueDate;
    private int numberOfDaysAfterCreationForDueDate;

    /* InterestRate */
    private boolean applyInterests;
    private InterestType interestType;
    private int numberOfDaysAfterDueDate;
    private boolean applyInFirstWorkday;
    private int maximumDaysToApplyPenalty;
    private int maximumMonthsToApplyPenalty;
    private BigDecimal interestFixedAmount;
    private BigDecimal rate;

    /* AcademicTariff */
    private AdministrativeOffice administrativeOffice;
    private DegreeType degreeType;
    private Degree degree;
    private CycleType cycleType;

    private BigDecimal baseAmount;
    private int unitsForBase;
    private boolean applyUnitsAmount;
    private BigDecimal unitAmount;
    private boolean applyPagesAmount;
    private BigDecimal pageAmount;
    private boolean applyMaximumAmount;
    private BigDecimal maximumAmount;
    private BigDecimal urgencyRate;
    private BigDecimal languageTranslationRate;

    /* TuitionInstallment */
    private Product tuitionInstallmentProduct;
    private int installmentOrder;
    private TuitionCalculationType tuitionCalculationType;
    private BigDecimal fixedAmount;
    private EctsCalculationType ectsCalculationType;
    private boolean academicalActBlockingOff;
    private BigDecimal factor;
    private BigDecimal totalEctsOrUnits;

    public AcademicTariffBean() {
        setBeginDate(new LocalDate());
        setEndDate(new LocalDate().plusYears(1));

        setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
        setFixedDueDate(null);
        setNumberOfDaysAfterCreationForDueDate(0);
        setApplyInterests(false);

        setInterestType(InterestType.DAILY);
        setNumberOfDaysAfterDueDate(0);
        setApplyInFirstWorkday(false);
        setMaximumDaysToApplyPenalty(0);
        setMaximumMonthsToApplyPenalty(0);
        setInterestFixedAmount(BigDecimal.ZERO);
        setRate(BigDecimal.ZERO);

        setAdministrativeOffice(null);
        setDegreeType(null);
        setDegree(null);
        setCycleType(null);

        setBaseAmount(BigDecimal.ZERO);
        setUnitsForBase(0);
        setApplyUnitsAmount(false);
        setUnitAmount(BigDecimal.ZERO);
        setApplyPagesAmount(false);
        setPageAmount(BigDecimal.ZERO);
        setApplyMaximumAmount(false);
        setMaximumAmount(BigDecimal.ZERO);
        setUrgencyRate(BigDecimal.ZERO);
        setLanguageTranslationRate(BigDecimal.ZERO);

        resetFields();
    }

    public AcademicTariffBean(final int installmentOrder) {
        setInstallmentOrder(installmentOrder);
    }

    public AcademicTariffBean(final AcademicTariff academicTariff) {
        setBeginDate(academicTariff.getBeginDate().toLocalDate());
        setEndDate(academicTariff.getEndDate() != null ? academicTariff.getEndDate().toLocalDate() : null);

        setDueDateCalculationType(academicTariff.getDueDateCalculationType());
        setFixedDueDate(academicTariff.getFixedDueDate());
        setNumberOfDaysAfterCreationForDueDate(academicTariff.getNumberOfDaysAfterCreationForDueDate());
        setApplyInterests(academicTariff.getApplyInterests());

        setInterestType(academicTariff.isApplyInterests() ? academicTariff.getInterestRate().getInterestType() : null);
        setNumberOfDaysAfterDueDate(academicTariff.isApplyInterests() ? academicTariff.getInterestRate()
                .getNumberOfDaysAfterDueDate() : 1);
        setApplyInFirstWorkday(academicTariff.isApplyInterests() ? academicTariff.getInterestRate().getApplyInFirstWorkday() : false);
        setMaximumDaysToApplyPenalty(academicTariff.isApplyInterests() ? academicTariff.getInterestRate()
                .getMaximumDaysToApplyPenalty() : 0);
        setMaximumMonthsToApplyPenalty(academicTariff.isApplyInterests() ? academicTariff.getInterestRate()
                .getMaximumMonthsToApplyPenalty() : 0);
        setInterestFixedAmount(academicTariff.isApplyInterests() ? academicTariff.getInterestRate().getInterestFixedAmount() : null);
        setRate(academicTariff.isApplyInterests() ? academicTariff.getInterestRate().getRate() : null);

        setAdministrativeOffice(academicTariff.getAdministrativeOffice());
        setDegreeType(academicTariff.getDegreeType());
        setDegree(academicTariff.getDegree());
        setCycleType(academicTariff.getCycleType());

        setBaseAmount(academicTariff.getBaseAmount());
        setUnitsForBase(academicTariff.getUnitsForBase());
        setApplyUnitsAmount(academicTariff.isApplyUnitsAmount());
        setUnitAmount(academicTariff.getUnitAmount());
        setApplyPagesAmount(academicTariff.isApplyPagesAmount());
        setPageAmount(academicTariff.getPageAmount());
        setApplyMaximumAmount(academicTariff.isApplyMaximumAmount());
        setMaximumAmount(academicTariff.getMaximumAmount());
        setUrgencyRate(academicTariff.getUrgencyRate());
        setLanguageTranslationRate(academicTariff.getLanguageTranslationRate());
    }

    public void resetFields() {
        if (getDueDateCalculationType() == null || !getDueDateCalculationType().isDaysAfterCreation()) {
            setNumberOfDaysAfterCreationForDueDate(0);
        }

        if (getDueDateCalculationType() == null || !getDueDateCalculationType().isFixedDate()) {
            setFixedDueDate(null);
        }

        if (getInterestType() == null) {
            setNumberOfDaysAfterCreationForDueDate(0);
            setApplyInFirstWorkday(false);
            setRate(BigDecimal.ZERO);
        }

        if (getInterestType() == null || !getInterestType().isDaily()) {
            setMaximumDaysToApplyPenalty(0);
        }

        if (getInterestType() == null || !getInterestType().isMonthly()) {
            setMaximumMonthsToApplyPenalty(0);
        }

        if (getInterestType() == null || !getInterestType().isFixedAmount()) {
            setInterestFixedAmount(BigDecimal.ZERO);
        }

        if (getDegree() == null) {
            setCycleType(null);
        }

        if (getDegreeType() == null) {
            setDegree(null);
        }

        if (!isApplyUnitsAmount()) {
            setUnitAmount(BigDecimal.ZERO);
        }

        if (!isApplyPagesAmount()) {
            setPageAmount(BigDecimal.ZERO);
        }

        if (!isApplyMaximumAmount()) {
            setMaximumAmount(BigDecimal.ZERO);
        }
        
        if (getDueDateCalculationType() != null && (getDueDateCalculationType().isFixedDate() || 
                getDueDateCalculationType().isBestOfFixedDateAndDaysAfterCreation()) && getFixedDueDate() == null) {
            setFixedDueDate(new LocalDate());
        }
    }

    public BigDecimal getAmountPerEctsOrUnit() {
        if (getTuitionCalculationType().isFixedAmount()) {
            throw new RuntimeException("invalid call");
        }

        if (getEctsCalculationType().isFixedAmount()) {
            return getFixedAmount();
        }

        return BigDecimal.ZERO;
    }

    public boolean isMaximumDaysToApplyPenaltyApplied() {
        return getMaximumDaysToApplyPenalty() > 0;
    }

    public boolean isMaximumMonthsToApplyPenaltyApplied() {
        return getMaximumMonthsToApplyPenalty() > 0;
    }

    /*
     * GETTERS & SETTERS
     */

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public DueDateCalculationType getDueDateCalculationType() {
        return dueDateCalculationType;
    }

    public void setDueDateCalculationType(DueDateCalculationType dueDateCalculationType) {
        this.dueDateCalculationType = dueDateCalculationType;
    }

    public LocalDate getFixedDueDate() {
        return fixedDueDate;
    }

    public void setFixedDueDate(LocalDate fixedDueDate) {
        this.fixedDueDate = fixedDueDate;
    }

    public int getNumberOfDaysAfterCreationForDueDate() {
        return numberOfDaysAfterCreationForDueDate;
    }

    public void setNumberOfDaysAfterCreationForDueDate(int numberOfDaysAfterCreationForDueDate) {
        this.numberOfDaysAfterCreationForDueDate = numberOfDaysAfterCreationForDueDate;
    }

    public boolean isApplyInterests() {
        return applyInterests;
    }

    public void setApplyInterests(boolean applyInterests) {
        this.applyInterests = applyInterests;
    }

    public boolean isApplyInFirstWorkday() {
        return applyInFirstWorkday;
    }

    public void setApplyInFirstWorkday(boolean applyInFirstWorkday) {
        this.applyInFirstWorkday = applyInFirstWorkday;
    }

    public AdministrativeOffice getAdministrativeOffice() {
        return administrativeOffice;
    }

    public void setAdministrativeOffice(AdministrativeOffice administrativeOffice) {
        this.administrativeOffice = administrativeOffice;
    }

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public CycleType getCycleType() {
        return cycleType;
    }

    public void setCycleType(CycleType cycleType) {
        this.cycleType = cycleType;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public int getUnitsForBase() {
        return unitsForBase;
    }

    public void setUnitsForBase(int unitsForBase) {
        this.unitsForBase = unitsForBase;
    }

    public boolean isApplyUnitsAmount() {
        return applyUnitsAmount;
    }

    public void setApplyUnitsAmount(boolean applyUnitsAmount) {
        this.applyUnitsAmount = applyUnitsAmount;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(BigDecimal unitAmount) {
        this.unitAmount = unitAmount;
    }

    public boolean isApplyPagesAmount() {
        return applyPagesAmount;
    }

    public void setApplyPagesAmount(boolean applyPagesAmount) {
        this.applyPagesAmount = applyPagesAmount;
    }

    public BigDecimal getPageAmount() {
        return pageAmount;
    }

    public void setPageAmount(BigDecimal pageAmount) {
        this.pageAmount = pageAmount;
    }

    public boolean isApplyMaximumAmount() {
        return applyMaximumAmount;
    }

    public void setApplyMaximumAmount(boolean applyMaximumAmount) {
        this.applyMaximumAmount = applyMaximumAmount;
    }

    public BigDecimal getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(BigDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public BigDecimal getUrgencyRate() {
        return urgencyRate;
    }

    public void setUrgencyRate(BigDecimal urgencyRate) {
        this.urgencyRate = urgencyRate;
    }

    public BigDecimal getLanguageTranslationRate() {
        return languageTranslationRate;
    }

    public void setLanguageTranslationRate(BigDecimal languageTranslationRate) {
        this.languageTranslationRate = languageTranslationRate;
    }

    /* InterestRate */

    public InterestType getInterestType() {
        return interestType;
    }

    public void setInterestType(InterestType interestType) {
        this.interestType = interestType;
    }

    public int getNumberOfDaysAfterDueDate() {
        return numberOfDaysAfterDueDate;
    }

    public void setNumberOfDaysAfterDueDate(int numberOfDaysAfterDueDate) {
        this.numberOfDaysAfterDueDate = numberOfDaysAfterDueDate;
    }

    public int getMaximumDaysToApplyPenalty() {
        return maximumDaysToApplyPenalty;
    }

    public void setMaximumDaysToApplyPenalty(int maximumDaysToApplyPenalty) {
        this.maximumDaysToApplyPenalty = maximumDaysToApplyPenalty;
    }

    public int getMaximumMonthsToApplyPenalty() {
        return maximumMonthsToApplyPenalty;
    }

    public void setMaximumMonthsToApplyPenalty(int maximumMonthsToApplyPenalty) {
        this.maximumMonthsToApplyPenalty = maximumMonthsToApplyPenalty;
    }

    public BigDecimal getInterestFixedAmount() {
        return interestFixedAmount;
    }

    public void setInterestFixedAmount(BigDecimal interestFixedAmount) {
        this.interestFixedAmount = interestFixedAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /* TuitionInstallmentTariff */

    public Product getTuitionInstallmentProduct() {
        return tuitionInstallmentProduct;
    }

    public void setTuitionInstallmentProduct(Product tuitionInstallmentProduct) {
        this.tuitionInstallmentProduct = tuitionInstallmentProduct;
    }

    public int getInstallmentOrder() {
        return installmentOrder;
    }

    public void setInstallmentOrder(int installmentOrder) {
        this.installmentOrder = installmentOrder;
    }

    public TuitionCalculationType getTuitionCalculationType() {
        return tuitionCalculationType;
    }

    public void setTuitionCalculationType(TuitionCalculationType tuitionCalculationType) {
        this.tuitionCalculationType = tuitionCalculationType;
    }

    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public EctsCalculationType getEctsCalculationType() {
        return ectsCalculationType;
    }

    public void setEctsCalculationType(EctsCalculationType ectsCalculationType) {
        this.ectsCalculationType = ectsCalculationType;
    }

    public boolean isAcademicalActBlockingOff() {

        return academicalActBlockingOff;
    }

    public void setAcademicalActBlockingOff(boolean academicalActBlockingOff) {
        this.academicalActBlockingOff = academicalActBlockingOff;
    }

    public BigDecimal getFactor() {
        return factor;
    }

    public void setFactor(BigDecimal factor) {
        this.factor = factor;
    }

    public BigDecimal getTotalEctsOrUnits() {
        return totalEctsOrUnits;
    }

    public void setTotalEctsOrUnits(BigDecimal totalEctsOrUnits) {
        this.totalEctsOrUnits = totalEctsOrUnits;
    }
}
