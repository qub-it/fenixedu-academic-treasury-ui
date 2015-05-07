package org.fenixedu.academictreasury.domain.event;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.event.TreasuryEvent;
import org.joda.time.LocalDate;

public class AcademicTreasuryEvent extends AcademicTreasuryEvent_Base {

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
        if(isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getNumberOfUnits();
        }
        
        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.numberOfUnits.not.applied");
    }
    
    public int getNumberOfPages() {
        if(isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getNumberOfPages();
        }

        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.numberOfPages.not.applied");
    }
    
    public boolean isUrgentRequest() {
        if(isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().isUrgentRequest();
        }
        
        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.urgentRequest.not.applied");
    }
    
    public LocalDate getRequestDate() {
        if(isForAcademicServiceRequest()) {
            return getAcademicServiceRequest().getRequestDate().toLocalDate();
        }
        
        throw new AcademicTreasuryDomainException("error.AcademicTreasuryEvent.requestDate.not.applied");
    }
    
    public Locale getLanguage() {
        if(isForAcademicServiceRequest()) {
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

    /* --- Academic Service Requests */

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

}
