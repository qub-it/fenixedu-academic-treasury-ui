package org.fenixedu.academictreasury.domain.debtGeneration.task;

import java.util.stream.Collectors;

import org.fenixedu.academictreasury.domain.debtGeneration.AcademicDebtGenerationRule;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

@Task(englishTitle = "Create academic debts", readOnly = true)
public class AcademicDebtGenerationRuleCronTask extends CronTask {

    @Override
    public void runTask() throws Exception {
        
        for (final AcademicDebtGenerationRule academicDebtGenerationRule : AcademicDebtGenerationRule.findActive().collect(Collectors.toSet())) {
            taskLog("academicDebtGenerationRuleCronTask: start");
            academicDebtGenerationRule.process();
            taskLog("academicDebtGenerationRuleCronTask: start");
        }
        
    }
}
