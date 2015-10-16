package org.fenixedu.academictreasury.dto.reports;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.paymentcodes.FinantialDocumentPaymentCode;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.joda.time.DateTime;

public class PaymentReferenceCodeEntryBean extends AbstractReportEntryBean {
    
    public static String[] SPREADSHEET_HEADERS = { 
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.identification"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.versioningCreator"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.creationDate"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.customerId"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.debtAccountId"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.name"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.identificationType"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.identificationNumber"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.vatNumber"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.email"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.address"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.countryCode"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.studentNumber"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.entityCode"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.referenceCode"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.finantialDocumentNumber"),
        Constants.bundle("label.PaymentReferenceCodeEntryBean.header.payableAmount") };

    
    private String identification;
    private String versioningCreator;
    private DateTime creationDate;
    private String customerId;
    private String debtAccountId;
    private String name;
    private LocalizedString identificationType;
    private String identificationNumber;
    private String vatNumber;
    private String email;
    private String address;
    private String countryCode;
    private Integer studentNumber;
    private String entityCode;
    private String referenceCode;
    private String finantialDocumentNumber;
    private BigDecimal payableAmount;
    
    private PaymentReferenceCode paymentReferenceCode;
    
    boolean completed = false;
    
    public PaymentReferenceCodeEntryBean(final PaymentReferenceCode paymentReferenceCode, final ErrorsLog errorsLog) {
        try {
            this.paymentReferenceCode = paymentReferenceCode;
            
            this.identification = paymentReferenceCode.getExternalId();
            this.versioningCreator = paymentReferenceCode.getVersioningCreator();
            this.creationDate = paymentReferenceCode.getVersioningCreationDate();
            
            if(paymentReferenceCode.getTargetPayment() != null) {
                DebtAccount referenceDebtAccount = paymentReferenceCode.getTargetPayment().getReferenceDebtAccount();
                this.customerId = referenceDebtAccount.getCustomer().getExternalId();
                this.debtAccountId = referenceDebtAccount.getExternalId();
                this.name = referenceDebtAccount.getCustomer().getName();

                if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getIdDocumentType() != null) {
                    this.identificationType =
                            ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getIdDocumentType().getLocalizedNameI18N();
                }
                
                this.identificationNumber = referenceDebtAccount.getCustomer().getIdentificationNumber();
                this.vatNumber = referenceDebtAccount.getCustomer().getFiscalNumber();

                if (referenceDebtAccount.getCustomer().isPersonCustomer()) {
                    this.email =
                            ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getInstitutionalOrDefaultEmailAddressValue();
                }
                
                this.address = referenceDebtAccount.getCustomer().getAddress();
                this.countryCode = referenceDebtAccount.getCustomer().getCountryCode();

                if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getStudent() != null) {
                    this.studentNumber = ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getStudent().getNumber();
                }
            }

            this.entityCode = paymentReferenceCode.getPaymentCodePool().getEntityReferenceCode();
            this.referenceCode = paymentReferenceCode.getReferenceCode();

            if(paymentReferenceCode.getTargetPayment() != null && paymentReferenceCode.getTargetPayment().isFinantialDocumentPaymentCode()) {
                this.finantialDocumentNumber = ((FinantialDocumentPaymentCode) paymentReferenceCode.getTargetPayment()).getFinantialDocument().getUiDocumentNumber();
            }
            
            this.payableAmount = paymentReferenceCode.getPayableAmount();
            
            completed = true;
        } catch(final Exception e) {
            e.printStackTrace();
            errorsLog.addError(paymentReferenceCode, e);            
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

            row.createCell(i++).setCellValue(versioningCreator);
            row.createCell(i++).setCellValue(valueOrEmpty(creationDate));
            row.createCell(i++).setCellValue(valueOrEmpty(customerId));
            row.createCell(i++).setCellValue(valueOrEmpty(debtAccountId));
            row.createCell(i++).setCellValue(valueOrEmpty(name));
            row.createCell(i++).setCellValue(valueOrEmpty(identificationType));
            row.createCell(i++).setCellValue(valueOrEmpty(identificationNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(vatNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(email));
            row.createCell(i++).setCellValue(valueOrEmpty(address));
            row.createCell(i++).setCellValue(valueOrEmpty(countryCode));
            row.createCell(i++).setCellValue(valueOrEmpty(studentNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(entityCode));            
            row.createCell(i++).setCellValue(valueOrEmpty(referenceCode));
            row.createCell(i++).setCellValue(valueOrEmpty(finantialDocumentNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(payableAmount.toString()));
            
        } catch(final Exception e) {
            e.printStackTrace();
            errorsLog.addError(paymentReferenceCode, e);            
        }
    }

}
