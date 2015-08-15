package org.fenixedu.academictreasury.services.signals;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.debtGeneration.task.PendingAcademicDebtGenerationRuleCronTask;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.TaskRunner;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.signals.DomainObjectEvent;

import pt.ist.fenixframework.Atomic;

import com.google.common.eventbus.Subscribe;

public class NormalEnrolmentHandler {

    @Subscribe
    public void improvementEnrolment(final DomainObjectEvent<Registration> registration) {
        if (AcademicTreasurySettings.getInstance().isRunAcademicDebtGenerationRuleOnNormalEnrolment()) {
            registration.getInstance().setBennuForPendingRegistrationsDebtCreation(Bennu.getInstance());

            new Thread() {

                @Atomic
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
