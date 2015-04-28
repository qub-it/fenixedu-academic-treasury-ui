package org.fenixedu.academictreasury.dto.tariff;

import java.io.Serializable;
import java.math.BigDecimal;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class AcademicTariffBean implements IBean, Serializable {

    private static final long serialVersionUID = 1L;

    /* Tariff */
    private DateTime beginDate;
    private DateTime endDate;
    private DueDateCalculationType dueDateCalculationType;
    private DateTime fixedDueDate;
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

    public AcademicTariffBean() {
        setBeginDate(new DateTime());
        setEndDate(null);
        
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
    
    
    public void resetFields() {
        if(getDueDateCalculationType() == null || !getDueDateCalculationType().isDaysAfterCreation()) {
            setNumberOfDaysAfterCreationForDueDate(0);
        } 
        
        if(getDueDateCalculationType() == null || !getDueDateCalculationType().isFixedDate()) {
            setFixedDueDate(null);
        }
        
        if(getInterestType() == null) {
            setNumberOfDaysAfterCreationForDueDate(0);
            setApplyInFirstWorkday(false);
            setRate(BigDecimal.ZERO);
        }

        if(getInterestType() == null || !getInterestType().isDaily()) {
            setMaximumDaysToApplyPenalty(0);
        }
        
        if(getInterestType() == null || !getInterestType().isMonthly()) {
            setMaximumMonthsToApplyPenalty(0);
        }
        
        if(getInterestType() == null || !getInterestType().isFixedAmount()) {
            setInterestFixedAmount(BigDecimal.ZERO);
        }
        
        if(getDegree() == null) {
            setCycleType(null);
        }
        
        if(getDegreeType() == null) {
            setDegree(null);
        }
        
        if(!isApplyUnitsAmount()) {
            setUnitAmount(BigDecimal.ZERO);
        }
        
        if(!isApplyPagesAmount()) {
            setPageAmount(BigDecimal.ZERO);
        }
        
        if(!isApplyMaximumAmount()) {
            setMaximumAmount(BigDecimal.ZERO);
        }
    }
    
    /*
     * GETTERS & SETTERS
     */
    
    public DateTime getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(DateTime beginDate) {
        this.beginDate = beginDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public DueDateCalculationType getDueDateCalculationType() {
        return dueDateCalculationType;
    }

    public void setDueDateCalculationType(DueDateCalculationType dueDateCalculationType) {
        this.dueDateCalculationType = dueDateCalculationType;
    }

    public DateTime getFixedDueDate() {
        return fixedDueDate;
    }

    public void setFixedDueDate(DateTime fixedDueDate) {
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

}
