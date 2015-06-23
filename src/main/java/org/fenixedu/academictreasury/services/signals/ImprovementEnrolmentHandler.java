package org.fenixedu.academictreasury.services.signals;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.bennu.signals.DomainObjectEvent;

import com.google.common.eventbus.Subscribe;

public class ImprovementEnrolmentHandler {

    @Subscribe
    public void improvementEnrolment(final DomainObjectEvent<EnrolmentEvaluation> event) {
        TuitionServices.createImprovementTax(event.getInstance(), event.getInstance().getWhenDateTime().toLocalDate());
    }


}
