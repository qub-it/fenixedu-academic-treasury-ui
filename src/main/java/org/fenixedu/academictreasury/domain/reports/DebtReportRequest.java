package org.fenixedu.academictreasury.domain.reports;

import java.util.List;
import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.reports.task.PendingDebtReportRequestsCronTask;
import org.fenixedu.academictreasury.dto.reports.DebtReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtReportRequestBean;
import org.fenixedu.academictreasury.services.debtReports.DebtReportService;
import org.fenixedu.academictreasury.util.streaming.spreadsheet.Spreadsheet;
import org.fenixedu.academictreasury.util.streaming.spreadsheet.SpreadsheetRow;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.TaskRunner;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class DebtReportRequest extends DebtReportRequest_Base {
    
    protected DebtReportRequest() {
        super();
        setBennu(Bennu.getInstance());
        setBennuForPendingReportRequests(Bennu.getInstance());
    }
    
    protected DebtReportRequest(final DebtReportRequestBean bean) {
        this();
        
        this.setBeginDate(bean.getBeginDate());
        this.setEndDate(bean.getEndDate());
        this.setType(bean.getType());
        
        checkRules();
    }

    private void checkRules() {
        
        if(getBeginDate() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequest.beginDate.required");
        }
        
        if(getEndDate() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequest.endDate.required");
        }
        
        if(getType() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequest.type.required");
        }
        
    }
    
    public boolean isPending() {
        return getBennuForPendingReportRequests() != null;
    }
    
    @Atomic(mode=TxMode.WRITE)
    public void processRequest() {
        
        if(getType().isRequestForInvoiceEntries()) {
            final Stream<DebtReportEntryBean> reportResult = DebtReportService.debtsReport(getBeginDate(), getEndDate());
            
            final byte[] content = Spreadsheet.buildSpreadsheetContent(new Spreadsheet() {

                @Override
                public String[] getHeaders() {
                    return DebtReportEntryBean.SPREADSHEET_HEADERS;
                }

                @Override
                public Stream<? extends SpreadsheetRow> getRows() {
                    return reportResult;
                }
            });
            
            DebtReportRequestResultFile.create(this, content);
        }
        
        setBennuForPendingReportRequests(null);
    }
    
    public static Stream<DebtReportRequest> findAll() {
        return Bennu.getInstance().getDebtReportRequestsSet().stream();
    }
    
    public static Stream<DebtReportRequest> findPending() {
        return Bennu.getInstance().getDebtReportRequestsSet().stream().filter(i -> i.isPending());
    }
    
    @Atomic
    public static DebtReportRequest create(final DebtReportRequestBean bean) {
        final DebtReportRequest request = new DebtReportRequest(bean);
        
        new Thread() {

            @Atomic(mode=TxMode.READ)
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                SchedulerSystem.queue(new TaskRunner(new PendingDebtReportRequestsCronTask()));
            };

        }.start();
        
        return request;
    }
    
}
