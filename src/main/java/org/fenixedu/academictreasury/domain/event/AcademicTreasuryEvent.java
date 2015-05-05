package org.fenixedu.academictreasury.domain.event;

import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.treasury.domain.event.TreasuryEvent;

public class AcademicTreasuryEvent extends AcademicTreasuryEvent_Base {

    protected AcademicTreasuryEvent(final AcademicServiceRequest academicServiceRequest) {
        super();

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

}
