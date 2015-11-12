package org.fenixedu.academictreasury.dto.reports;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.domain.exemption.TreasuryExemption;
import org.joda.time.DateTime;

public class TreasuryExemptionReportEntryBean extends AbstractReportEntryBean {

    public static String[] SPREADSHEET_HEADERS = {
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.identification"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.versioningCreator"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.creationDate"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.customerId"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.debtAccountId"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.customerName"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.debitEntryId"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.debitEntryDescription"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.exemptedAmount"),
            Constants.bundle("label.TreasuryExemptionReportEntryBean.header.reason") };

    private String identification;
    private String versioningCreator;
    private DateTime creationDate;
    private String customerId;
    private String customerName;
    private String debtAccountId;
    private String debitEntryId;
    private String debitEntryDescription;
    private String exemptedAmount;
    private String reason;

    private TreasuryExemption treasuryExemption;

    boolean completed = false;

    public TreasuryExemptionReportEntryBean(final TreasuryExemption treasuryExemption, final ErrorsLog errorsLog) {
        try {
            this.treasuryExemption = treasuryExemption;

            this.identification = treasuryExemption.getExternalId();
            this.versioningCreator = treasuryExemption.getVersioningCreator();
            this.creationDate = treasuryExemption.getVersioningCreationDate();
            this.customerId = treasuryExemption.getDebitEntry().getDebtAccount().getCustomer().getExternalId();
            this.customerName = treasuryExemption.getDebitEntry().getDebtAccount().getCustomer().getName();
            this.debtAccountId = treasuryExemption.getDebitEntry().getDebtAccount().getExternalId();
            this.debitEntryId = treasuryExemption.getDebitEntry().getExternalId();
            this.debitEntryDescription = treasuryExemption.getDebitEntry().getDescription();
            this.exemptedAmount =
                    treasuryExemption.getDebitEntry().getCurrency().getValueFor(treasuryExemption.getValueToExempt());
            this.reason = treasuryExemption.getReason();

            this.completed = true;
        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(treasuryExemption, e);
        }

    }

    @Override
    public void writeCellValues(final Row row, final ErrorsLog errorsLog) {
        try {
            row.createCell(0).setCellValue(identification);

            if (!completed) {
                row.createCell(1).setCellValue(Constants.bundle("error.DebtReportEntryBean.report.generation.verify.entry"));
                return;
            }

            int i = 1;

            row.createCell(i++).setCellValue(valueOrEmpty(versioningCreator));
            row.createCell(i++).setCellValue(valueOrEmpty(creationDate));
            row.createCell(i++).setCellValue(valueOrEmpty(customerId));
            row.createCell(i++).setCellValue(valueOrEmpty(debtAccountId));
            row.createCell(i++).setCellValue(valueOrEmpty(customerName));
            row.createCell(i++).setCellValue(valueOrEmpty(debitEntryId));
            row.createCell(i++).setCellValue(valueOrEmpty(debitEntryDescription));
            row.createCell(i++).setCellValue(valueOrEmpty(exemptedAmount));
            row.createCell(i++).setCellValue(valueOrEmpty(reason));

        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(this.treasuryExemption, e);
        }
    }

}
