package org.fenixedu.academictreasury.dto.reports;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academictreasury.domain.reports.DebtReportRequest;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.domain.paymentcodes.SibsTransactionDetail;
import org.fenixedu.treasury.util.streaming.spreadsheet.IErrorsLog;
import org.joda.time.DateTime;

public class SibsTransactionDetailEntryBean extends AbstractReportEntryBean {

    public static String[] SPREADSHEET_HEADERS = { 
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.identification"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.versioningCreator"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.creationDate"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.whenProcessed"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.whenRegistered"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.amountPayed"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.sibsEntityReferenceCode"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.sibsPaymentReferenceCode"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.sibsTransactionId"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.debtAccountId"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.customerId"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.businessIdentification"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.fiscalNumber"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.customerName"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.settlementDocumentNumber"),
        Constants.bundle("label.SibsTransactionDetailEntryBean.header.comments") };

    
    private String identification;
    private String versioningCreator;
    private DateTime creationDate;
    private DateTime whenProcessed;
    private DateTime whenRegistered;
    private String amountPayed;
    private String sibsEntityReferenceCode;
    private String sibsPaymentReferenceCode;
    private String sibsTransactionId;
    private String debtAccountId;
    private String customerId;
    private String businessIdentification;
    private String fiscalNumber;
    private String customerName; 
    private String settlementDocumentNumber;
    private String comments;
    
    private SibsTransactionDetail sibsTransactionDetail;
    
    boolean completed = false;

    public SibsTransactionDetailEntryBean(final SibsTransactionDetail detail, final String decimalSeparator, final ErrorsLog errorsLog) {
        
        try {
            this.sibsTransactionDetail = detail;
            
            this.identification = detail.getExternalId();
            this.versioningCreator = detail.getVersioningCreator();
            this.creationDate = detail.getVersioningCreationDate();
            this.whenProcessed = detail.getWhenProcessed();
            this.whenRegistered = detail.getWhenRegistered();
            this.amountPayed = detail.getAmountPayed() != null ? detail.getAmountPayed().toString() : "";
            this.sibsEntityReferenceCode = detail.getSibsEntityReferenceCode();
            this.sibsPaymentReferenceCode = detail.getSibsPaymentReferenceCode();
            this.sibsTransactionId = detail.getSibsTransactionId();
            this.debtAccountId = detail.getDebtAccountId();
            this.customerId = detail.getCustomerId();
            this.businessIdentification = detail.getBusinessIdentification();
            this.fiscalNumber = detail.getFiscalNumber();
            this.customerName = detail.getCustomerName();
            this.settlementDocumentNumber = detail.getSettlementDocumentNumber();
            this.comments = detail.getComments();
            
            if(DebtReportRequest.COMMA.equals(decimalSeparator)) {
                this.amountPayed = this.amountPayed.replace(DebtReportRequest.DOT, DebtReportRequest.COMMA);
            }
            
            this.completed = true;
        } catch(final Exception e) {
            e.printStackTrace();
            errorsLog.addError(sibsTransactionDetail, e);            
        }
        
    }
    
    @Override
    public void writeCellValues(final Row row, final IErrorsLog ierrorsLog) {
        final ErrorsLog errorsLog = (ErrorsLog) ierrorsLog;
        
        try {
            row.createCell(0).setCellValue(identification);
            
            if (!completed) {
                row.createCell(1).setCellValue(Constants.bundle("error.DebtReportEntryBean.report.generation.verify.entry"));
                return;
            }

            int i = 1;

            row.createCell(i++).setCellValue(valueOrEmpty(versioningCreator));
            row.createCell(i++).setCellValue(valueOrEmpty(creationDate));
            row.createCell(i++).setCellValue(valueOrEmpty(whenProcessed));
            row.createCell(i++).setCellValue(valueOrEmpty(whenRegistered));
            row.createCell(i++).setCellValue(valueOrEmpty(amountPayed));
            row.createCell(i++).setCellValue(valueOrEmpty(sibsEntityReferenceCode));
            row.createCell(i++).setCellValue(valueOrEmpty(sibsPaymentReferenceCode));
            row.createCell(i++).setCellValue(valueOrEmpty(sibsTransactionId));
            row.createCell(i++).setCellValue(valueOrEmpty(debtAccountId));
            row.createCell(i++).setCellValue(valueOrEmpty(customerId));
            row.createCell(i++).setCellValue(valueOrEmpty(businessIdentification));
            row.createCell(i++).setCellValue(valueOrEmpty(fiscalNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(customerName));
            row.createCell(i++).setCellValue(valueOrEmpty(settlementDocumentNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(comments));
            
        } catch(final Exception e) {
            e.printStackTrace();
            errorsLog.addError(sibsTransactionDetail, e);            
        }
    }

}
