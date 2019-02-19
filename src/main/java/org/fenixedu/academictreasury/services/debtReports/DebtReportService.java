package org.fenixedu.academictreasury.services.debtReports;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.academicalAct.AcademicActBlockingSuspension;
import org.fenixedu.academictreasury.domain.reports.DebtReportRequest;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.dto.reports.AcademicActBlockingSuspensionReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtAccountReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.DebtReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.PaymentReferenceCodeEntryBean;
import org.fenixedu.academictreasury.dto.reports.PaymentReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.ProductReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.ReimbursementReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.SettlementReportEntryBean;
import org.fenixedu.academictreasury.dto.reports.SibsTransactionDetailEntryBean;
import org.fenixedu.academictreasury.dto.reports.TreasuryExemptionReportEntryBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.CreditEntry;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.PaymentEntry;
import org.fenixedu.treasury.domain.document.ReimbursementEntry;
import org.fenixedu.treasury.domain.document.SettlementEntry;
import org.fenixedu.treasury.domain.exemption.TreasuryExemption;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.domain.paymentcodes.SibsTransactionDetail;
import org.fenixedu.treasury.services.integration.TreasuryPlataformDependentServicesFactory;

public class DebtReportService {

    public static Stream<DebtReportEntryBean> debitEntriesReport(final DebtReportRequest request, final ErrorsLog log) {
        return DebitEntry.findAll()
                .filter(i -> Constants.isDateBetween(request.getBeginDate(), request.getEndDate(), TreasuryPlataformDependentServicesFactory.implementation().versioningCreationDate(i)))
                .filter(i -> request.isIncludeAnnuledEntries() || !i.isAnnulled())
                .map(i -> new DebtReportEntryBean(i, request, log));
    }

    public static Stream<DebtReportEntryBean> creditEntriesReport(final DebtReportRequest request, final ErrorsLog log) {
        return CreditEntry.findAll()
                .filter(i -> Constants.isDateBetween(request.getBeginDate(), request.getEndDate(), TreasuryPlataformDependentServicesFactory.implementation().versioningCreationDate(i)))
                .filter(i -> request.isIncludeAnnuledEntries() || !i.isAnnulled())
                .map(i -> new DebtReportEntryBean(i, request, log));
    }

    public static Stream<SettlementReportEntryBean> settlementEntriesReport(final DebtReportRequest request, final ErrorsLog log) {
        return SettlementEntry.findAll()
                .filter(i -> Constants.isDateBetween(request.getBeginDate(), request.getEndDate(), i.getFinantialDocument().getDocumentDate()))
                .filter(i -> request.isIncludeAnnuledEntries() || !i.isAnnulled())
                .map(i -> new SettlementReportEntryBean(i, request, log));
    }

    public static Stream<PaymentReportEntryBean> paymentEntriesReport(final DebtReportRequest request, final ErrorsLog log) {
        return PaymentEntry.findAll()
                .filter(i -> Constants.isDateBetween(request.getBeginDate(), request.getEndDate(), i.getSettlementNote().getDocumentDate()))
                .filter(i -> request.isIncludeAnnuledEntries() || !i.getSettlementNote().isAnnulled())
                .map(i -> new PaymentReportEntryBean(i, request, log));
    }

    public static Stream<ReimbursementReportEntryBean> reimbursementEntriesReport(final DebtReportRequest request,
            final ErrorsLog log) {
        return ReimbursementEntry.findAll()
                .filter(i -> Constants.isDateBetween(request.getBeginDate(), request.getEndDate(), i.getSettlementNote().getDocumentDate()))
                .filter(i -> request.isIncludeAnnuledEntries() || !i.getSettlementNote().isAnnulled())
                .map(i -> new ReimbursementReportEntryBean(i, request, log));
    }

    public static Stream<DebtAccountReportEntryBean> debtAccountEntriesReport(final DebtReportRequest request,
            final ErrorsLog log) {
        return DebtAccount.findAll().map(i -> new DebtAccountReportEntryBean(i, request, log));
    }

    public static Stream<AcademicActBlockingSuspensionReportEntryBean> academicActBlockingSuspensionReport(
            final DebtReportRequest request, final ErrorsLog log) {
        return AcademicActBlockingSuspension.findAll().map(i -> new AcademicActBlockingSuspensionReportEntryBean(i, log));
    }

    public static Stream<PaymentReferenceCodeEntryBean> paymentReferenceCodeReport(final DebtReportRequest request,
            final ErrorsLog log) {
        return PaymentReferenceCode.findAll()
                .filter(i -> request.isIncludeAnnuledEntries() || !i.isAnnulled())
                .map(i -> new PaymentReferenceCodeEntryBean(i, request, log));
    }

    public static Stream<SibsTransactionDetailEntryBean> sibsTransactionDetailReport(final DebtReportRequest request,
            final ErrorsLog log) {
        return SibsTransactionDetail.findAll()
                .map(i -> new SibsTransactionDetailEntryBean(i, request, log));
    }

    public static Stream<TreasuryExemptionReportEntryBean> treasuryExemptionReport(final DebtReportRequest request,
            final ErrorsLog log) {
        return TreasuryExemption.findAll()
                .filter(i -> i.getDebitEntry() != null && Constants.isDateBetween(request.getBeginDate(), request.getEndDate(),
                        i.getDebitEntry().getEntryDateTime()))
                .map(i -> new TreasuryExemptionReportEntryBean(i, request, log));
    }

    public static Stream<ProductReportEntryBean> productReport(final DebtReportRequest request, final ErrorsLog log) {
        return Product.findAll().map(i -> new ProductReportEntryBean(i, request, log));
    }

}
