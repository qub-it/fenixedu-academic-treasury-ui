package org.fenixedu.academictreasury.domain.treasury;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.treasury.IAcademicTreasuryEvent;
import org.fenixedu.academic.domain.treasury.ITreasuryBridgeAPI;
import org.fenixedu.academictreasury.services.EmolumentServices;
import org.fenixedu.academictreasury.services.PersonServices;
import org.fenixedu.academictreasury.services.StandaloneEnrolment;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.bennu.signals.Signal;

public class AcademicTreasuryBridgeImpl implements ITreasuryBridgeAPI {

    @Override
    public void registerNewAcademicServiceRequestSituationHandler() {
        Signal.register(ACADEMIC_SERVICE_REQUEST_NEW_SITUATION_EVENT, new EmolumentServices());
        Signal.register(Person.PERSON_CREATE_SIGNAL, new PersonServices());
    }

    @Override
    public IAcademicTreasuryEvent academicTreasuryEventForAcademicServiceRequest(final AcademicServiceRequest academicServiceRequest) {
        return academicServiceRequest.getAcademicTreasuryEvent();
    }

    @Override

    public void registerStandaloneEnrolmentHandler() {
        Signal.register(STANDALONE_ENROLMENT, new StandaloneEnrolment());
    }

    @Override
    public void registerStandaloneUnenrolmentHandler() {
        Signal.register(STANDALONE_UNENROLMENT, new StandaloneUnrenrolment());
    }
    
    @Override
    public void standaloneUnenrolment(final Enrolment standaloneEnrolment) {
        TuitionServices.removeDebitEntryForStandaloneEnrolment(standaloneEnrolment);
    }

}
