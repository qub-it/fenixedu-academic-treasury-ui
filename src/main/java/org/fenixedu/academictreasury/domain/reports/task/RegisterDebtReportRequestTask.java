package org.fenixedu.academictreasury.domain.reports.task;

import org.fenixedu.academictreasury.domain.reports.DebtReportRequest;
import org.fenixedu.academictreasury.dto.reports.DebtReportRequestBean;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.treasury.services.integration.TreasuryPlataformDependentServicesFactory;
import org.joda.time.LocalDate;

@Task(englishTitle = "Register debt report request to generate report", readOnly = true)
public class RegisterDebtReportRequestTask extends CronTask {

    private static final int MAX_REPORTS_DAY = 3;
    
    @Override
    public void runTask() throws Exception {

        final LocalDate now = new LocalDate();
        
        // Avoid automatic creation of many reports if in this day there are some reports created
        
        if(DebtReportRequest.findAll().filter(r -> TreasuryPlataformDependentServicesFactory.implementation().versioningCreationDate(r).toLocalDate().compareTo(now) == 0).count()  >= MAX_REPORTS_DAY) {
            taskLog("Exceeded the report requests %d. Please request explicitly.\n", MAX_REPORTS_DAY);
            return;
        };

        taskLog("Requesting a new debt report");

        final DebtReportRequestBean bean = new DebtReportRequestBean();
        
        bean.setBeginDate(new LocalDate(1950, 1, 1));
        bean.setEndDate(now);
        bean.setDecimalSeparator(",");
        bean.setIncludeAnnuledEntries(true);
        
        DebtReportRequest.create(bean);
        
    }

}
