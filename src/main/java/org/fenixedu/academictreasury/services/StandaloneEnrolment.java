package org.fenixedu.academictreasury.services;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.bennu.signals.DomainObjectEvent;

import com.google.common.eventbus.Subscribe;

public class StandaloneEnrolment {

    @Subscribe
    public void standaloneEnrolment(final DomainObjectEvent<Enrolment> event) {
        TuitionServices.createInferedTuitionForStandalone(event.getInstance(), event.getInstance().getCreationDateDateTime()
                .toLocalDate());
    }
    
}
