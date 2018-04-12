package org.fenixedu.academictreasury.dto.reports;

import static org.fenixedu.academictreasury.util.Constants.academicTreasuryBundle;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.reports.DebtReportRequest;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Currency;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.paymentcodes.FinantialDocumentPaymentCode;
import org.fenixedu.treasury.domain.paymentcodes.MultipleEntriesPaymentCode;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.util.streaming.spreadsheet.IErrorsLog;
import org.joda.time.DateTime;

public class PaymentReferenceCodeEntryBean extends AbstractReportEntryBean {

    private static final String TARGET_TYPE_FINANTIAL_DOCUMENT = "F";
    private static final String TARGET_TYPE_MULTIPLE_ENTRIES = "M";
    private static final String TARGET_TYPE_NOT_DEFINED = "N";

    public static String[] SPREADSHEET_HEADERS = { academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.identification"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.versioningCreator"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.creationDate"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.customerId"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.debtAccountId"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.name"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.identificationType"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.identificationNumber"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.vatNumber"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.email"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.address"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.addressCountryCode"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.studentNumber"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.entityCode"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.referenceCode"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.finantialDocumentNumber"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.payableAmount"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.description"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.target.type"),
            academicTreasuryBundle("label.PaymentReferenceCodeEntryBean.header.state") };

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
    private String addressCountryCode;
    private Integer studentNumber;
    private String entityCode;
    private String referenceCode;
    private String finantialDocumentNumber;
    private String payableAmount;
    private String description;
    private String targetType;
    private String state;

    private PaymentReferenceCode paymentReferenceCode;

    boolean completed = false;

    public PaymentReferenceCodeEntryBean(final PaymentReferenceCode paymentReferenceCode, final DebtReportRequest request,
            final ErrorsLog errorsLog) {
        final String decimalSeparator = request.getDecimalSeparator();

        try {
            this.paymentReferenceCode = paymentReferenceCode;

            this.identification = paymentReferenceCode.getExternalId();
            this.versioningCreator = paymentReferenceCode.getVersioningCreator();
            this.creationDate = paymentReferenceCode.getVersioningCreationDate();

            if (paymentReferenceCode.getTargetPayment() != null) {
                DebtAccount referenceDebtAccount = paymentReferenceCode.getTargetPayment().getDebtAccount();
                this.customerId = referenceDebtAccount.getCustomer().getExternalId();
                this.debtAccountId = referenceDebtAccount.getExternalId();
                this.name = referenceDebtAccount.getCustomer().getName();

                if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson() != null
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getIdDocumentType() != null) {
                    this.identificationType = ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson()
                            .getIdDocumentType().getLocalizedNameI18N();
                } else if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPersonForInactivePersonCustomer() != null
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPersonForInactivePersonCustomer()
                                .getIdDocumentType() != null) {
                    this.identificationType = ((PersonCustomer) referenceDebtAccount.getCustomer())
                            .getPersonForInactivePersonCustomer().getIdDocumentType().getLocalizedNameI18N();
                }

                this.identificationNumber = referenceDebtAccount.getCustomer().getIdentificationNumber();
                this.vatNumber = referenceDebtAccount.getCustomer().getUiFiscalNumber();

                if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson() != null) {
                    this.email = ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson()
                            .getInstitutionalOrDefaultEmailAddressValue();
                } else if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPersonForInactivePersonCustomer() != null) {
                    this.email = ((PersonCustomer) referenceDebtAccount.getCustomer()).getPersonForInactivePersonCustomer()
                            .getInstitutionalOrDefaultEmailAddressValue();
                }

                this.address = referenceDebtAccount.getCustomer().getAddress();
                this.addressCountryCode = referenceDebtAccount.getCustomer().getAddressCountryCode();

                if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson() != null
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getStudent() != null) {
                    this.studentNumber =
                            ((PersonCustomer) referenceDebtAccount.getCustomer()).getPerson().getStudent().getNumber();
                } else if (referenceDebtAccount.getCustomer().isPersonCustomer()
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPersonForInactivePersonCustomer() != null
                        && ((PersonCustomer) referenceDebtAccount.getCustomer()).getPersonForInactivePersonCustomer()
                                .getStudent() != null) {
                    this.studentNumber = ((PersonCustomer) referenceDebtAccount.getCustomer())
                            .getPersonForInactivePersonCustomer().getStudent().getNumber();
                }
            }

            this.entityCode = paymentReferenceCode.getPaymentCodePool().getEntityReferenceCode();
            this.referenceCode = paymentReferenceCode.getReferenceCode();

            if (paymentReferenceCode.getTargetPayment() != null
                    && paymentReferenceCode.getTargetPayment().isFinantialDocumentPaymentCode()) {
                this.finantialDocumentNumber = ((FinantialDocumentPaymentCode) paymentReferenceCode.getTargetPayment())
                        .getFinantialDocument().getUiDocumentNumber();
            } else if (paymentReferenceCode.getTargetPayment() != null
                    && paymentReferenceCode.getTargetPayment().isMultipleEntriesPaymentCode()) {
                final MultipleEntriesPaymentCode multipleEntriesPaymentCode =
                        (MultipleEntriesPaymentCode) paymentReferenceCode.getTargetPayment();
                this.finantialDocumentNumber =
                        String.join(", ",
                                multipleEntriesPaymentCode.getInvoiceEntriesSet().stream()
                                        .filter(i -> i.getFinantialDocument() != null)
                                        .map(i -> i.getFinantialDocument().getUiDocumentNumber()).collect(Collectors.toList()));
            }

            if (paymentReferenceCode.getPayableAmount() != null) {
                final Currency currency = paymentReferenceCode.getPaymentCodePool().getFinantialInstitution().getCurrency();

                this.payableAmount = currency.getValueWithScale(paymentReferenceCode.getPayableAmount()).toString();

                if (DebtReportRequest.COMMA.equals(decimalSeparator)) {
                    this.payableAmount = this.payableAmount.replace(DebtReportRequest.DOT, DebtReportRequest.COMMA);
                }
            }

            if (paymentReferenceCode.getTargetPayment() != null) {
                this.description = paymentReferenceCode.getTargetPayment().getDescription();
            }

            if (paymentReferenceCode.getTargetPayment() != null
                    && paymentReferenceCode.getTargetPayment().isFinantialDocumentPaymentCode()) {
                this.targetType = TARGET_TYPE_FINANTIAL_DOCUMENT;
            } else if (paymentReferenceCode.getTargetPayment() != null
                    && paymentReferenceCode.getTargetPayment().isMultipleEntriesPaymentCode()) {
                this.targetType = TARGET_TYPE_MULTIPLE_ENTRIES;
            } else if (paymentReferenceCode.getTargetPayment() == null) {
                this.targetType = TARGET_TYPE_NOT_DEFINED;
            }

            if (paymentReferenceCode.getState() != null) {
                this.state = paymentReferenceCode.getState().getDescriptionI18N().getContent();
            }

            completed = true;
        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(paymentReferenceCode, e);
        }
    }

    @Override
    public void writeCellValues(final Row row, final IErrorsLog ierrorsLog) {
        final ErrorsLog errorsLog = (ErrorsLog) ierrorsLog;

        try {
            row.createCell(0).setCellValue(identification);

            if (!completed) {
                row.createCell(1).setCellValue(academicTreasuryBundle("error.DebtReportEntryBean.report.generation.verify.entry"));
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
            row.createCell(i++).setCellValue(valueOrEmpty(addressCountryCode));
            row.createCell(i++).setCellValue(valueOrEmpty(studentNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(entityCode));
            row.createCell(i++).setCellValue(valueOrEmpty(referenceCode));
            row.createCell(i++).setCellValue(valueOrEmpty(finantialDocumentNumber));
            row.createCell(i++).setCellValue(valueOrEmpty(payableAmount));
            row.createCell(i++).setCellValue(valueOrEmpty(description));
            row.createCell(i++).setCellValue(valueOrEmpty(targetType));
            row.createCell(i++).setCellValue(valueOrEmpty(state));

        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(paymentReferenceCode, e);
        }
    }

}
