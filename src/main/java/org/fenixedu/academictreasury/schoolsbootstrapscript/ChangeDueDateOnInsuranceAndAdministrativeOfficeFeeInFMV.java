package org.fenixedu.academictreasury.schoolsbootstrapscript;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.joda.time.LocalDate;

public class ChangeDueDateOnInsuranceAndAdministrativeOfficeFeeInFMV extends CustomTask {

    @Override
    public void runTask() throws Exception {
        
        final LocalDate correctDueDate = new LocalDate(2015, 9, 30);
        
        //DebitEntry.findAll().filter(l -> !l.getDueDate().isEqual(correctDueDate)).forEach(l -> getLogger().info(l.getDue););
        
    }

}
