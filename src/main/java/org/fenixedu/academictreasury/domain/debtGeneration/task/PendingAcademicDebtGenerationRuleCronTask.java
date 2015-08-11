package org.fenixedu.academictreasury.domain.debtGeneration.task;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.debtGeneration.AcademicDebtGenerationRule;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

@Task(englishTitle = "Create academic debts", readOnly = true)
public class PendingAcademicDebtGenerationRuleCronTask extends CronTask {

    private static final int MAX_REGISTRATIONS = 100;

    @Override
    public void runTask() throws Exception {
        
        int count = 0;
        for(final Registration registration : Bennu.getInstance().getPendingRegistrationsForDebtCreationSet()) {
            AcademicDebtGenerationRule.runAllActiveForRegistration(registration);
            count++;
            
            if(count > MAX_REGISTRATIONS) {
                return;
            }
        }
    }
}
