package org.fenixedu.academictreasury.domain.treasury;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.treasury.ITreasuryBridgeAPI;
import org.fenixedu.academictreasury.domain.serviceRequests.CreateEmolumentForAcademicServiceRequest;
import org.fenixedu.bennu.signals.Signal;

public class AcademicTreasuryBridgeImpl implements ITreasuryBridgeAPI {

    @Override
    public void registerNewAcademicServiceRequestSituationHandler() {
        Signal.register(ACADEMIC_SERVICE_REQUEST_NEW_SITUATION_EVENT, new CreateEmolumentForAcademicServiceRequest());
    }

    @Override
    public void newAcademicServiceRequestSituationHandler(AcademicServiceRequest academicServiceRequest) {
        new CreateEmolumentForAcademicServiceRequest().newAcademicServiceRequestSituationEvent(academicServiceRequest);
    }
    
}
