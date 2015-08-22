package org.fenixedu.academictreasury.services.signals;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.debtGeneration.task.PendingAcademicDebtGenerationRuleCronTask;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.bennu.scheduler.TaskRunner;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.signals.DomainObjectEvent;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.eventbus.Subscribe;

public class NormalEnrolmentHandler {

    @Subscribe
    public void improvementEnrolment(final DomainObjectEvent<Registration> registration) {
        if (AcademicTreasurySettings.getInstance().isRunAcademicDebtGenerationRuleOnNormalEnrolment()) {
            
            TuitionServices.enqueueRegistrationForAcademicDebtRuleExecution(registration.getInstance().getExternalId());
            
            new Thread() {

                @Atomic(mode=TxMode.READ)
                public void run() {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }

                    SchedulerSystem.queue(new TaskRunner(new PendingAcademicDebtGenerationRuleCronTask()));
                };

            }.start();
            
        }
    }
}
