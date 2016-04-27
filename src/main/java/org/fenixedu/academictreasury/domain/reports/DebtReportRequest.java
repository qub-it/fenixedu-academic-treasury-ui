package org.fenixedu.academictreasury.domain.reports;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.reports.task.PendingDebtReportRequestsCronTask;
import org.fenixedu.academictreasury.dto.reports.AcademicActBlockingSuspensionReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtAccountReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtReportRequestBean;
import org.fenixedu.academictreasury.dto.reports.PaymentReferenceCodeEntryBean;
import org.fenixedu.academictreasury.dto.reports.PaymentReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.SettlementReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.SibsTransactionDetailEntryBean;
import org.fenixedu.academictreasury.dto.reports.TreasuryExemptionReportEntryBean;
import org.fenixedu.academictreasury.services.debtReports.DebtReportService;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.TaskRunner;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.treasury.util.streaming.spreadsheet.ExcelSheet;
import org.fenixedu.treasury.util.streaming.spreadsheet.Spreadsheet;

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

        if (getBeginDate() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequest.beginDate.required");
        }

        if (getEndDate() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequest.endDate.required");
        }

        if (getType() == null) {
            throw new AcademicTreasuryDomainException("error.DebtReportRequest.type.required");
        }

    }

    public boolean isPending() {
        return getBennuForPendingReportRequests() != null;
    }

    @Atomic(mode = TxMode.WRITE)
    public void processRequest() {

        final ErrorsLog errorsLog = new ErrorsLog();
        
        if (getType().isRequestForInvoiceEntries()) {

            final byte[] content = Spreadsheet.buildSpreadsheetContent(new Spreadsheet() {

                @Override
                public ExcelSheet[] getSheets() {
                    return new ExcelSheet[] {
                            ExcelSheet.create(debitEntriesSheetName(), DebtReportEntryBean.SPREADSHEET_DEBIT_HEADERS,
                                    DebtReportService.debitEntriesReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(creditEntriesSheetName(), DebtReportEntryBean.SPREADSHEET_CREDIT_HEADERS,
                                    DebtReportService.creditEntriesReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(settlementEntriesSheetName(), SettlementReportEntryBean.SPREADSHEET_HEADERS,
                                    DebtReportService.settlementEntriesReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(paymentEntriesSheetName(), PaymentReportEntryBean.SPREADSHEET_HEADERS,
                                    DebtReportService.paymentEntriesReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(reimbursementEntriesSheetName(), PaymentReportEntryBean.SPREADSHEET_HEADERS,
                                    DebtReportService.reimbursementEntriesReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(debtAccountEntriesSheetName(), DebtAccountReportEntryBean.SPREADSHEET_HEADERS,
                                            DebtReportService.debtAccountEntriesReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(academicActBlockingSuspensionSheetName(), AcademicActBlockingSuspensionReportEntryBean.SPREADSHEET_HEADERS,
                                    DebtReportService.academicActBlockingSuspensionReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(paymentReferenceCodeSheetName(), PaymentReferenceCodeEntryBean.SPREADSHEET_HEADERS,
                                    DebtReportService.paymentReferenceCodeReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(sibsTransactionDetailSheetName(), SibsTransactionDetailEntryBean.SPREADSHEET_HEADERS,
                                    DebtReportService.sibsTransactionDetailReport(getBeginDate(), getEndDate(), errorsLog)),
                            ExcelSheet.create(treasuryExemptionSheetName(), TreasuryExemptionReportEntryBean.SPREADSHEET_HEADERS,
                                    DebtReportService.treasuryExemptionReport(getBeginDate(), getEndDate(), errorsLog))
                    };
                }
            }, errorsLog);

            DebtReportRequestResultFile.create(this, content);
            DebtReportRequestResultErrorsFile.create(this, errorsLog.getLog().getBytes());
        }

        setBennuForPendingReportRequests(null);
    }

    @Atomic
    public void cancelRequest() {
        setBennuForPendingReportRequests(null);
    }
    
    protected String treasuryExemptionSheetName() {
        return Constants.bundle("label.DebtReportRequest.treasuryExemptionSheetName");
    }
    
    protected String sibsTransactionDetailSheetName() {
        return Constants.bundle("label.DebtReportRequest.sibsTransactionDetailSheetName");
    }
    
    protected String paymentReferenceCodeSheetName() {
        return Constants.bundle("label.DebtReportRequest.paymentReferenceCodeSheetName");
    }

    private String academicActBlockingSuspensionSheetName() {
        return Constants.bundle("label.DebtReportRequest.academicActBlockingSuspensionSheetName");
    }

    private String debitEntriesSheetName() {
        return Constants.bundle("label.DebtReportRequest.debitEntriesSheetName");
    }

    private String creditEntriesSheetName() {
        return Constants.bundle("label.DebtReportRequest.creditEntriesSheetName");
    }

    private String paymentEntriesSheetName() {
        return Constants.bundle("label.DebtReportRequest.paymentEntriesSheetName");
    }

    private String settlementEntriesSheetName() {
        return Constants.bundle("label.DebtReportRequest.settlementEntriesSheetName");
    }

    private String reimbursementEntriesSheetName() {
        return Constants.bundle("label.DebtReportRequest.reimbursementEntriesSheetName");
    }
    
    private String debtAccountEntriesSheetName() {
        return Constants.bundle("label.DebtReportRequest.debtAccountEntriesSheetName");
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

            @Atomic(mode = TxMode.READ)
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
