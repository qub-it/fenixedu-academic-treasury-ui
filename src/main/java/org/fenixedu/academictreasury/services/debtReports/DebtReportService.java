package org.fenixedu.academictreasury.services.debtReports;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.academicalAct.AcademicActBlockingSuspension;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.dto.reports.AcademicActBlockingSuspensionReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtAccountReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.PaymentReferenceCodeEntryBean;
import org.fenixedu.academictreasury.dto.reports.PaymentReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.ReimbursementReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.SettlementReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.SibsTransactionDetailEntryBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.CreditEntry;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.PaymentEntry;
import org.fenixedu.treasury.domain.document.ReimbursementEntry;
import org.fenixedu.treasury.domain.document.SettlementEntry;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.domain.paymentcodes.SibsTransactionDetail;
import org.joda.time.LocalDate;

public class DebtReportService {

    public static Stream<DebtReportEntryBean> debitEntriesReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return DebitEntry.findAll().filter(i -> Constants.isDateBetween(begin, end, i.getEntryDateTime()))
                .map(i -> new DebtReportEntryBean(i, log));
    }
    
    public static Stream<DebtReportEntryBean> creditEntriesReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return CreditEntry.findAll().filter(i -> Constants.isDateBetween(begin, end, i.getEntryDateTime()))
                .map(i -> new DebtReportEntryBean(i, log));
    }
    
    public static Stream<SettlementReportEntryBean> settlementEntriesReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return SettlementEntry.findAll().filter(i -> Constants.isDateBetween(begin, end, i.getEntryDateTime()))
                .map(i -> new SettlementReportEntryBean(i, log));
    }
    
    public static Stream<PaymentReportEntryBean> paymentEntriesReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return PaymentEntry.findAll().filter(i -> Constants.isDateBetween(begin, end, i.getSettlementNote().getPaymentDate()))
                .map(i -> new PaymentReportEntryBean(i, log));
    }
    
    public static Stream<ReimbursementReportEntryBean> reimbursementEntriesReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return ReimbursementEntry.findAll().filter(i -> Constants.isDateBetween(begin, end, i.getSettlementNote().getPaymentDate()))
                .map(i -> new ReimbursementReportEntryBean(i, log));
    }
    
    public static Stream<DebtAccountReportEntryBean> debtAccountEntriesReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return DebtAccount.findAll().map(i -> new DebtAccountReportEntryBean(i, log));
    }
    
    public static Stream<AcademicActBlockingSuspensionReportEntryBean> academicActBlockingSuspensionReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return AcademicActBlockingSuspension.findAll().map(i -> new AcademicActBlockingSuspensionReportEntryBean(i, log));
    }
    
    public static Stream<PaymentReferenceCodeEntryBean> paymentReferenceCodeReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return PaymentReferenceCode.findAll().map(i -> new PaymentReferenceCodeEntryBean(i, log));
    } 
    
    public static Stream<SibsTransactionDetailEntryBean> sibsTransactionDetailReport(final LocalDate begin, final LocalDate end, final ErrorsLog log) {
        return SibsTransactionDetail.findAll().map(i -> new SibsTransactionDetailEntryBean(i, log));
    }
    
}
