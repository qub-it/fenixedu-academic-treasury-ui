package org.fenixedu.academictreasury.domain.reports.task;

import java.util.stream.Collectors;

import org.fenixedu.academictreasury.domain.reports.DebtReportRequest;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Task(englishTitle = "Create debt reports for pending requests", readOnly = true)
public class PendingDebtReportRequestsCronTask extends CronTask {

    @Override
    public void runTask() throws Exception {
        Thread thread = new Thread() {

            @Override
            @Atomic(mode=TxMode.READ)
            public void run() {
                for (DebtReportRequest debtReportRequest : DebtReportRequest.findPending().collect(Collectors.toSet())) {
                    debtReportRequest.processRequest();
                }
            }
        };
        
        thread.start();
        try {
            thread.join();
        } catch(IllegalStateException e) {
        }
    }

}
