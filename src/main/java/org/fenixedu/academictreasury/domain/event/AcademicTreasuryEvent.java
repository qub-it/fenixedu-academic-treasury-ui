package org.fenixedu.academictreasury.domain.event;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.treasury.IAcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.event.TreasuryEvent;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;

public class AcademicTreasuryEvent extends AcademicTreasuryEvent_Base implements IAcademicTreasuryEvent {

    protected AcademicTreasuryEvent(final AcademicServiceRequest academicServiceRequest) {
        init(academicServiceRequest, ServiceRequestMapEntry.findProduct(academicServiceRequest));

        checkRules();
    }

    @Override
    protected void init(final Product product) {
        throw new RuntimeException("wrong call");
    }

    protected void init(final AcademicServiceRequest academicServiceRequest, final Product product) {
        super.init(product);

        setAcademicServiceRequest(academicServiceRequest);
        setPropertiesJsonMap(propertiesMapToJson(fillPropertiesMap()));

        checkRules();
    }

    @Override
    protected void checkRules() {
        super.checkRules();

        if (getAcademicServiceRequest() != null && find(getAcademicServiceRequest()).count() > 1) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.event.for.academicServiceRequest.duplicate");
        }
    }

    public boolean isChargedWithDebitEntry() {
        return getDebitEntriesSet().stream().filter(d -> !d.isEventAnnuled()).count() > 0;
    }

    public int getNumberOfUnits() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getNumberOfUnits();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.numberOfUnits.not.applied");
    }

    public int getNumberOfPages() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getNumberOfPages();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.numberOfPages.not.applied");
    }

    public boolean isUrgentRequest() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().isUrgentRequest();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.urgentRequest.not.applied");
    }

    public LocalDate getRequestDate() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getRequestDate().toLocalDate();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.requestDate.not.applied");
    }

    public Locale getLanguage() {
        if (isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getLanguage();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.language.not.applied");
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    public static Stream<? extends AcademicTreasuryEvent> findAll() {
        return TreasuryEvent.findAll().filter(e -> e instanceof AcademicTreasuryEvent).map(AcademicTreasuryEvent.class::cast);
    }

    /* --- Academic Service Requests --- */

    public static Stream<? extends AcademicTreasuryEvent> find(final AcademicServiceRequest academicServiceRequest) {
        if (academicServiceRequest == null) {
            throw new RuntimeException("wrong call");
        }

        return findAll().filter(e -> e.getAcademicServiceRequest() == academicServiceRequest);
    }

    public static Optional<? extends AcademicTreasuryEvent> findUnique(final AcademicServiceRequest academicServiceRequest) {
        return find(academicServiceRequest).findFirst();
    }

    public static AcademicTreasuryEvent createForAcademicServiceRequest(final AcademicServiceRequest academicServiceRequest) {
        return new AcademicTreasuryEvent(academicServiceRequest);
    }

    /* -----
     * UTILS
     * -----
     */

    public boolean isForAcademicServiceRequest() {
        return getAcademicServiceRequest() != null;
    }

    public boolean isForTuiton() {
        return false;
    }

    // @formatter:off
    public static enum AcademicTreasuryEventKeys {
        ACADEMIC_SERVICE_REQUEST_NAME, 
        EXECUTION_YEAR, 
        DETAILED, 
        URGENT, 
        LANGUAGE, 
        BASE_AMOUNT, 
        UNITS_FOR_BASE, 
        UNIT_AMOUNT,
        ADDITIONAL_UNITS, 
        CALCULATED_UNITS_AMOUNT,
        PAGE_AMOUNT,
        NUMBER_OF_PAGES,
        CALCULATED_PAGES_AMOUNT,
        MAXIMUM_AMOUNT,
        AMOUNT_WITHOUT_RATES,
        FOREIGN_LANGUAGE_RATE,
        CALCULATED_FOREIGN_LANGUAGE_RATE,
        URGENT_PERCENTAGE,
        CALCULATED_URGENT_AMOUNT,
        FINAL_AMOUNT;

        public LocalizedString getDescriptionI18N() {
            return BundleUtil
                    .getLocalizedString(Constants.BUNDLE, "label." + AcademicTreasuryEventKeys.class.getSimpleName() + "." + name());
        }

    }
    // @formatter:on

    private Map<String, String> fillPropertiesMap() {
        final Map<String, String> propertiesMap = Maps.newHashMap();

        propertiesMap.put(AcademicTreasuryEventKeys.ACADEMIC_SERVICE_REQUEST_NAME.getDescriptionI18N().getContent(),
                ServiceRequestType.findUnique(getAcademicServiceRequest()).getName().getContent());

        if (getAcademicServiceRequest().hasExecutionYear()) {
            propertiesMap.put(AcademicTreasuryEventKeys.EXECUTION_YEAR.getDescriptionI18N().getContent(),
                    getAcademicServiceRequest().getExecutionYear().getQualifiedName());
        }

        propertiesMap.put(AcademicTreasuryEventKeys.DETAILED.getDescriptionI18N().getContent(),
                booleanLabel(getAcademicServiceRequest().isDetailed()).getContent());
        propertiesMap.put(AcademicTreasuryEventKeys.URGENT.getDescriptionI18N().getContent(),
                booleanLabel(getAcademicServiceRequest().isUrgentRequest()).getContent());
        propertiesMap.put(AcademicTreasuryEventKeys.LANGUAGE.getDescriptionI18N().getContent(), getAcademicServiceRequest()
                .getLanguage().getLanguage());
        propertiesMap.put(AcademicTreasuryEventKeys.BASE_AMOUNT.getDescriptionI18N().getContent(), getAcademicServiceRequest()
                .getLanguage().getLanguage());

        return propertiesMap;
    }

    private LocalizedString booleanLabel(final boolean detailed) {
        return BundleUtil.getLocalizedString(Constants.BUNDLE, detailed ? "label.yes" : "label.no");
    }

    @Override
    public boolean isWithDebitEntry() {
        return DebitEntry.findActive(this).count() > 0;
    }

    @Override
    public BigDecimal getAmountToPay() {
        return DebitEntry.amountToPay(this);
    }
    
    public BigDecimal getPayedAmount() {
        return DebitEntry.payedAmount(this);
    }

    @Override
    public BigDecimal getRemainingAmountToPay() {
        return DebitEntry.remainingAmountToPay(this);
    }

    @Override
    public BigDecimal getBaseAmount() {
        if(!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.baseAmount.unavailable");
        }
        
        return super.getBaseAmount();
    }

    @Override
    public BigDecimal getAdditionalUnitsAmount() {
        if(!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.additionalUnitsAmount.unavailable");
        }
        
        return super.getAmountForAdditionalUnits();
    }

    @Override
    public BigDecimal getMaximumAmount() {
        if(!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.maximumAmount.unavailable");
        }
        
        return super.getMaximumAmount();
    }

    @Override
    public BigDecimal getPagesAmount() {
        if(!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.pagesAmount.unavailable");
        }
        
        return super.getAmountForPages();
    }

    @Override
    public BigDecimal getAmountForLanguageTranslationRate() {
        if(!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.amountForLanguageTranslationRate.unavailable");
        }
        
        return super.getAmountForLanguageTranslationRate();
    }

    @Override
    public BigDecimal getAmountForUrgencyRate() {
        if(!isChargedWithDebitEntry()) {
            throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.amountForUrgencyRate.unavailable");
        }
        
        return super.getAmountForUrgencyRate();
    }

    public void updatePricingFields(final BigDecimal baseAmount, final BigDecimal amountForAdditionalUnits,
            final BigDecimal amountForPages, final BigDecimal maximumAmount, final BigDecimal amountForLanguageTranslationRate,
            final BigDecimal amountForUrgencyRate) {
        
        super.setBaseAmount(baseAmount); 
        super.setAmountForAdditionalUnits(amountForAdditionalUnits);
        super.setAmountForPages(amountForPages);
        super.setMaximumAmount(maximumAmount); 
        super.setAmountForLanguageTranslationRate(amountForLanguageTranslationRate);
        super.setAmountForUrgencyRate(amountForUrgencyRate);
    }

}
