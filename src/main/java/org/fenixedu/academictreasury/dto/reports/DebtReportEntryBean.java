package org.fenixedu.academictreasury.dto.reports;

import static org.fenixedu.academictreasury.util.Constants.academicTreasuryBundle;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent.AcademicTreasuryEventKeys;
import org.fenixedu.academictreasury.domain.reports.DebtReportRequest;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.domain.serviceRequests.ITreasuryServiceRequest;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Currency;
import org.fenixedu.treasury.domain.Customer;
import org.fenixedu.treasury.domain.document.AdvancedPaymentCreditNote;
import org.fenixedu.treasury.domain.document.CreditEntry;
import org.fenixedu.treasury.domain.document.CreditNote;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.Invoice;
import org.fenixedu.treasury.domain.document.InvoiceEntry;
import org.fenixedu.treasury.domain.event.TreasuryEvent;
import org.fenixedu.treasury.services.integration.erp.sap.SAPExporter;
import org.fenixedu.treasury.services.integration.erp.sap.SAPExporterUtils;
import org.fenixedu.treasury.util.streaming.spreadsheet.IErrorsLog;
import org.fenixedu.treasury.util.streaming.spreadsheet.SpreadsheetRow;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;

public class DebtReportEntryBean implements SpreadsheetRow {

    // @formatter:off
    public static String[] SPREADSHEET_DEBIT_HEADERS = { 
            academicTreasuryBundle("label.DebtReportEntryBean.header.identification"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.entryType"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.versioningCreator"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.creationDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.entryDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.dueDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.customerId"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.debtAccountId"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.name"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.identificationType"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.identificationNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.vatNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.email"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.address"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.studentNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.registrationNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.degreeType"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.degreeCode"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.degreeName"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.executionYear"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.executionSemester"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.productCode"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.invoiceEntryDescription"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentExportationPending"),
            "",
            academicTreasuryBundle("label.DebtReportEntryBean.header.amountToPay"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.openAmountToPay"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.payorDebtAcount.vatNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.payorDebtAcount.name"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.agreement"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.ingression"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.firstTimeStudent"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.partialRegime"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.statutes"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.numberOfNormalEnrolments"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.numberOfStandaloneEnrolments"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.numberOfExtracurricularEnrolments"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.tuitionPaymentPlan"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.tuitionPaymentPlanConditions"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentAnnuled"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentAnnuledReason"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.closeDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.openAmountAtERPStartDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.exportedInLegacyERP"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.legacyERPCertificateDocumentReference"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.erpCertificationDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.erpCertificateDocumentReference"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.originSettlementNoteForAdvancedCredit")
            
    };

    public static String[] SPREADSHEET_CREDIT_HEADERS = { 
            academicTreasuryBundle("label.DebtReportEntryBean.header.identification"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.entryType"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.versioningCreator"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.creationDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.entryDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.dueDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.customerId"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.debtAccountId"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.name"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.identificationType"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.identificationNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.vatNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.email"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.address"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.studentNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.registrationNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.degreeType"),            
            academicTreasuryBundle("label.DebtReportEntryBean.header.degreeCode"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.degreeName"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.executionYear"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.executionSemester"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.productCode"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.invoiceEntryDescription"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentExportationPending"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.debitEntry.identification"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.amountToCredit"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.openAmountToCredit"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.payorDebtAcount.vatNumber"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.payorDebtAcount.name"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.agreement"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.ingression"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.firstTimeStudent"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.partialRegime"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.statutes"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.numberOfNormalEnrolments"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.numberOfStandaloneEnrolments"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.numberOfExtracurricularEnrolments"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.tuitionPaymentPlan"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.tuitionPaymentPlanConditions"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentAnnuled"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.documentAnnuledReason"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.closeDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.openAmountAtERPStartDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.exportedInLegacyERP"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.legacyERPCertificateDocumentReference"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.erpCertificationDate"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.erpCertificateDocumentReference"),
            academicTreasuryBundle("label.DebtReportEntryBean.header.originSettlementNoteForAdvancedCredit")
    };
    // @formatter:on

    private InvoiceEntry invoiceEntry;
    private boolean completed = false;

    private String identification;
    private String entryType;
    private String versioningCreator;
    private DateTime creationDate;
    private DateTime entryDate;
    private LocalDate dueDate;
    private String customerId;
    private String debtAccountId;
    private String name;
    private LocalizedString identificationType;
    private String identificationNumber;
    private String vatNumber;
    private String email;
    private String address;
    private Integer studentNumber;
    private Integer registrationNumber;
    private String degreeType;
    private String degreeCode;
    private LocalizedString degreeName;
    private String executionYear;
    private String executionSemester;
    private String productCode;
    private String invoiceEntryDescription;
    private String documentNumber;
    private boolean documentExportationPending;
    private boolean annuled;
    private String annuledReason;
    private String debitEntryIdentification;
    private String baseAmount;
    private String creditedAmount;
    private String amountToPay;
    private String openAmountToPay;
    private String payorDebtAccountVatNumber;
    private String payorDebtAccountName;
    private LocalizedString agreement;
    private LocalizedString ingression;
    private Boolean firstTimeStudent;
    private Boolean partialRegime;
    private String statutes;
    private int numberOfNormalEnrolments;
    private int numberOfStandaloneEnrolments;
    private int numberOfExtracurricularEnrolments;
    private String tuitionPaymentPlan;
    private String tuitionPaymentPlanConditions;

    private DateTime closeDate;
    private String openAmountAtERPStartDate;
    private boolean exportedInLegacyERP;
    private String legacyERPCertificateDocumentReference;

    private LocalDate erpCertificationDate;
    private String erpCertificateDocumentReference;

    private String originSettlementNoteForAdvancedCredit;

    public DebtReportEntryBean(final InvoiceEntry entry, final DebtReportRequest request, final ErrorsLog errorsLog) {
        final String decimalSeparator = request.getDecimalSeparator();

        this.invoiceEntry = entry;

        try {
            this.identification = entry.getExternalId();
            this.entryType = entryType(entry);
            this.creationDate = entry.getVersioningCreationDate();
            this.versioningCreator = entry.getVersioningCreator();
            this.entryDate = entry.getEntryDateTime();
            this.dueDate = entry.getDueDate();

            this.payorDebtAccountVatNumber = "";
            this.payorDebtAccountName = "";
            
            if(entry.getFinantialDocument() != null && ((Invoice) entry.getFinantialDocument()).getPayorDebtAccount() != null) {
                this.payorDebtAccountVatNumber = ((Invoice) entry.getFinantialDocument()).getPayorDebtAccount().getCustomer().getUiFiscalNumber();
                this.payorDebtAccountName = ((Invoice) entry.getFinantialDocument()).getPayorDebtAccount().getCustomer().getName();
            }
            
            fillStudentInformation(entry);

            this.productCode = entry.getProduct().getCode();
            this.invoiceEntryDescription = entry.getDescription();

            if (entry.getFinantialDocument() != null) {
                this.documentNumber = entry.getFinantialDocument().getUiDocumentNumber();
                this.documentExportationPending = entry.getFinantialDocument().isDocumentToExport();
            }

            this.annuled = entry.isAnnulled();

            if (this.annuled && entry.getFinantialDocument() != null) {
                this.annuledReason = entry.getFinantialDocument().getAnnulledReason();
            }

            if (entry.isCreditNoteEntry() && ((CreditEntry) entry).getDebitEntry() != null) {
                this.debitEntryIdentification = ((CreditEntry) entry).getDebitEntry().getExternalId();
            }

            final Currency currency = entry.getDebtAccount().getFinantialInstitution().getCurrency();

            this.baseAmount = currency.getValueWithScale(entry.getAmountWithVat()).toString();

            /* TODO Consider: 
            if (entry.isDebitNoteEntry()) {
                final DebitEntry debitEntry = (DebitEntry) entry;
                this.amountToPay = currency.getValueWithScale(debitEntry.getAmountWithVat()).toString();
            } else if(entry.isCreditNoteEntry()) {
            }
            */

            this.amountToPay = currency.getValueWithScale(entry.getAmountWithVat()).toString();
            this.openAmountToPay = currency.getValueWithScale(entry.getOpenAmount()).toString();

            if (DebtReportRequest.COMMA.equals(decimalSeparator)) {
                this.amountToPay = this.amountToPay.replace(DebtReportRequest.DOT, DebtReportRequest.COMMA);
                this.openAmountToPay = this.openAmountToPay.replace(DebtReportRequest.DOT, DebtReportRequest.COMMA);
            }

            this.closeDate = entry.getFinantialDocument() != null ? entry.getFinantialDocument().getCloseDate() : null;
            this.openAmountAtERPStartDate = currency
                    .getValueWithScale(
                            SAPExporterUtils.openAmountAtDate((InvoiceEntry) entry, SAPExporter.ERP_INTEGRATION_START_DATE))
                    .toString();
            this.exportedInLegacyERP =
                    entry.getFinantialDocument() != null ? entry.getFinantialDocument().isExportedInLegacyERP() : false;

            this.legacyERPCertificateDocumentReference = entry.getFinantialDocument() != null ? entry.getFinantialDocument()
                    .getLegacyERPCertificateDocumentReference() : null;

            this.erpCertificationDate =
                    entry.getFinantialDocument() != null ? entry.getFinantialDocument().getErpCertificationDate() : null;

            this.erpCertificateDocumentReference = entry.getFinantialDocument() != null ? entry.getFinantialDocument()
                    .getErpCertificateDocumentReference() : null;

            if (DebtReportRequest.COMMA.equals(decimalSeparator)) {
                this.openAmountAtERPStartDate =
                        this.openAmountAtERPStartDate.replace(DebtReportRequest.DOT, DebtReportRequest.COMMA);
            }

            if (entry.getFinantialDocument() != null && entry.getFinantialDocument().isCreditNote()
                    && ((CreditNote) entry.getFinantialDocument()).isAdvancePayment()) {
                final AdvancedPaymentCreditNote advancedCreditNote = (AdvancedPaymentCreditNote) entry.getFinantialDocument();
                this.originSettlementNoteForAdvancedCredit =
                        advancedCreditNote.getAdvancedPaymentSettlementNote() != null ? advancedCreditNote
                                .getAdvancedPaymentSettlementNote().getUiDocumentNumber() : "";
            }

            this.completed = true;
        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(entry, e);
        }

    }

    private void fillStudentInformation(final InvoiceEntry entry) {
        final Customer customer = entry.getDebtAccount().getCustomer();

        this.customerId = customer.getExternalId();
        this.debtAccountId = entry.getDebtAccount().getExternalId();

        this.name = customer.getName();

        if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPerson() != null
                && ((PersonCustomer) customer).getPerson().getIdDocumentType() != null) {
            this.identificationType = ((PersonCustomer) customer).getPerson().getIdDocumentType().getLocalizedNameI18N();
        } else if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPersonForInactivePersonCustomer() != null
                && ((PersonCustomer) customer).getPersonForInactivePersonCustomer().getIdDocumentType() != null) {

        }

        this.identificationNumber = customer.getIdentificationNumber();
        this.vatNumber = customer.getUiFiscalNumber();

        if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPerson() != null) {
            this.email = ((PersonCustomer) customer).getPerson().getInstitutionalOrDefaultEmailAddressValue();
        } else if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPersonForInactivePersonCustomer() != null) {
            this.email =
                    ((PersonCustomer) customer).getPersonForInactivePersonCustomer().getInstitutionalOrDefaultEmailAddressValue();
        }

        this.address = customer.getUiCompleteAddress();

        if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPerson() != null
                && ((PersonCustomer) customer).getPerson().getStudent() != null) {
            this.studentNumber = ((PersonCustomer) customer).getPerson().getStudent().getNumber();
        } else if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPersonForInactivePersonCustomer() != null
                && ((PersonCustomer) customer).getPersonForInactivePersonCustomer().getStudent() != null) {
            this.studentNumber = ((PersonCustomer) customer).getPersonForInactivePersonCustomer().getStudent().getNumber();
        }

        final DebitEntry debitEntry = entry.isDebitNoteEntry() ? (DebitEntry) entry : ((CreditEntry) entry).getDebitEntry();

        if (debitEntry != null) {

            // Degree && ExecutionYear && ExecutionSemester
            if (debitEntry.getTreasuryEvent() != null && debitEntry.getTreasuryEvent() instanceof AcademicTreasuryEvent) {
                final AcademicTreasuryEvent academicTreasuryEvent = (AcademicTreasuryEvent) debitEntry.getTreasuryEvent();

                if (academicTreasuryEvent.isForRegistrationTuition()) {
                    this.registrationNumber = academicTreasuryEvent.getRegistration().getNumber();
                    this.degreeType = academicTreasuryEvent.getRegistration().getDegree().getDegreeType().getName().getContent();
                    this.degreeCode = academicTreasuryEvent.getRegistration().getDegree().getCode();
                    this.degreeName = academicTreasuryEvent.getRegistration().getDegree().getPresentationNameI18N();
                    this.executionYear = academicTreasuryEvent.getExecutionYear().getQualifiedName();
                    this.tuitionPaymentPlan =
                            AcademicTreasuryEventKeys.valueFor(debitEntry, AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN);
                    this.tuitionPaymentPlanConditions = AcademicTreasuryEventKeys.valueFor(debitEntry,
                            AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN_CONDITIONS);

                    fillStudentConditionsInformation(academicTreasuryEvent.getRegistration(),
                            academicTreasuryEvent.getExecutionYear());

                } else if (academicTreasuryEvent.isForStandaloneTuition()
                        || academicTreasuryEvent.isForExtracurricularTuition()) {
                    if (debitEntry.getCurricularCourse() != null) {
                        this.degreeType = debitEntry.getCurricularCourse().getDegree().getDegreeType().getName().getContent();
                        this.degreeCode = debitEntry.getCurricularCourse().getDegree().getCode();
                        this.degreeName = debitEntry.getCurricularCourse().getDegree().getPresentationNameI18N();
                    }

                    if (debitEntry.getExecutionSemester() != null) {
                        this.executionYear = debitEntry.getExecutionSemester().getExecutionYear().getQualifiedName();
                        this.executionSemester = debitEntry.getExecutionSemester().getQualifiedName();
                    }
                    
                    this.tuitionPaymentPlan =
                            AcademicTreasuryEventKeys.valueFor(debitEntry, AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN);
                    this.tuitionPaymentPlanConditions = AcademicTreasuryEventKeys.valueFor(debitEntry,
                            AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN_CONDITIONS);

                    if (academicTreasuryEvent.getRegistration() != null && academicTreasuryEvent.getExecutionYear() != null) {
                        fillStudentConditionsInformation(academicTreasuryEvent.getRegistration(),
                                academicTreasuryEvent.getExecutionYear());

                    }

                } else if (academicTreasuryEvent.isForImprovementTax()) {
                    if (debitEntry.getCurricularCourse() != null) {
                        this.degreeType = debitEntry.getCurricularCourse().getDegree().getDegreeType().getName().getContent();
                        this.degreeCode = debitEntry.getCurricularCourse().getDegree().getCode();
                        this.degreeName = debitEntry.getCurricularCourse().getDegree().getPresentationNameI18N();
                    }

                    if (debitEntry.getExecutionSemester() != null) {
                        this.executionYear = debitEntry.getExecutionSemester().getExecutionYear().getQualifiedName();
                        this.executionSemester = debitEntry.getExecutionSemester().getQualifiedName();
                    }

                    if (academicTreasuryEvent.getRegistration() != null && academicTreasuryEvent.getExecutionYear() != null) {
                        fillStudentConditionsInformation(academicTreasuryEvent.getRegistration(),
                                academicTreasuryEvent.getExecutionYear());
                    }
                } else if (academicTreasuryEvent.isForAcademicTax()) {

                    this.registrationNumber = academicTreasuryEvent.getRegistration().getNumber();
                    this.degreeType = academicTreasuryEvent.getRegistration().getDegree().getDegreeType().getName().getContent();
                    this.degreeCode = academicTreasuryEvent.getRegistration().getDegree().getCode();
                    this.degreeName = academicTreasuryEvent.getRegistration().getDegree().getPresentationNameI18N();
                    this.executionYear = academicTreasuryEvent.getExecutionYear().getQualifiedName();

                    fillStudentConditionsInformation(academicTreasuryEvent.getRegistration(),
                            academicTreasuryEvent.getExecutionYear());

                } else if (academicTreasuryEvent.isForAcademicServiceRequest()) {

                    final ITreasuryServiceRequest iTreasuryServiceRequest = academicTreasuryEvent.getITreasuryServiceRequest();

                    this.registrationNumber = iTreasuryServiceRequest.getRegistration().getNumber();
                    this.degreeType =
                            iTreasuryServiceRequest.getRegistration().getDegree().getDegreeType().getName().getContent();
                    this.degreeCode = iTreasuryServiceRequest.getRegistration().getDegree().getCode();
                    this.degreeName = iTreasuryServiceRequest.getRegistration().getDegree().getPresentationNameI18N();

                    if (iTreasuryServiceRequest.hasExecutionYear()) {
                        this.executionYear = iTreasuryServiceRequest.getExecutionYear().getQualifiedName();
                        fillStudentConditionsInformation(iTreasuryServiceRequest.getRegistration(),
                                iTreasuryServiceRequest.getExecutionYear());
                    }
                }
            } else if (debitEntry.getTreasuryEvent() != null) {
                final TreasuryEvent treasuryEvent = debitEntry.getTreasuryEvent();

                if (!Strings.isNullOrEmpty(treasuryEvent.getDegreeCode())) {
                    this.degreeCode = treasuryEvent.getDegreeCode();
                }

                if (!Strings.isNullOrEmpty(treasuryEvent.getDegreeName())) {
                    this.degreeName = new LocalizedString(I18N.getLocale(), treasuryEvent.getDegreeName());
                }

                if (!Strings.isNullOrEmpty(treasuryEvent.getExecutionYearName())) {
                    this.executionYear = treasuryEvent.getExecutionYearName();
                }
            }

            if (Strings.isNullOrEmpty(this.degreeCode)) {
                this.degreeCode = debitEntry.getDegreeCode();
            }

            if (Strings.isNullOrEmpty(this.executionYear)) {
                this.executionYear = debitEntry.getExecutionYearName();
            }
        }
    }

    private String entryType(final InvoiceEntry entry) {
        if (entry.isDebitNoteEntry()) {
            return academicTreasuryBundle("label.DebtReportEntryBean.debitNoteEntry");
        } else if (entry.isCreditNoteEntry()) {
            return academicTreasuryBundle("label.DebtReportEntryBean.creditNoteEntry");
        }

        return null;
    }

    private void fillStudentConditionsInformation(final Registration registration, final ExecutionYear executionYear) {
        this.firstTimeStudent = registration.isFirstTime(executionYear);
        this.partialRegime = registration.isPartialRegime(executionYear);
        this.statutes = statutes(registration, executionYear);
        this.agreement = registration.getRegistrationProtocol().getDescription();
        this.ingression = registration.getIngressionType().getDescription();

        this.numberOfNormalEnrolments = TuitionServices.normalEnrolments(registration, executionYear).size();
        this.numberOfStandaloneEnrolments = TuitionServices.standaloneEnrolments(registration, executionYear).size();
        this.numberOfExtracurricularEnrolments = TuitionServices.extracurricularEnrolments(registration, executionYear).size();
    }

    private String statutes(final Registration registration, final ExecutionYear executionYear) {
        return registration.getStudent().getStatutesTypesValidOnAnyExecutionSemesterFor(executionYear).stream()
                .map(s -> s != null ? s.getName().getContent() : "").reduce((a, c) -> c + ", " + a).orElse(null);
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

            if (invoiceEntry.isDebitNoteEntry()) {
                int i = 1;

                row.createCell(i++).setCellValue(entryType);
                row.createCell(i++).setCellValue(versioningCreator);
                row.createCell(i++).setCellValue(valueOrEmpty(creationDate));
                row.createCell(i++).setCellValue(entryDate.toString(Constants.DATE_TIME_FORMAT_YYYY_MM_DD));
                row.createCell(i++).setCellValue(dueDate.toString(Constants.DATE_FORMAT_YYYY_MM_DD));
                row.createCell(i++).setCellValue(customerId);
                row.createCell(i++).setCellValue(debtAccountId);
                row.createCell(i++).setCellValue(name);
                row.createCell(i++).setCellValue(valueOrEmpty(identificationType));
                row.createCell(i++).setCellValue(valueOrEmpty(identificationNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(vatNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(email));
                row.createCell(i++).setCellValue(valueOrEmpty(address));
                row.createCell(i++).setCellValue(valueOrEmpty(studentNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(registrationNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(degreeType));
                row.createCell(i++).setCellValue(valueOrEmpty(degreeCode));
                row.createCell(i++).setCellValue(valueOrEmpty(degreeName));
                row.createCell(i++).setCellValue(valueOrEmpty(executionYear));
                row.createCell(i++).setCellValue(valueOrEmpty(executionSemester));
                row.createCell(i++).setCellValue(productCode);
                row.createCell(i++).setCellValue(invoiceEntryDescription);
                row.createCell(i++).setCellValue(valueOrEmpty(documentNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(documentExportationPending));
                i++;
                row.createCell(i++).setCellValue(valueOrEmpty(amountToPay));
                row.createCell(i++).setCellValue(valueOrEmpty(openAmountToPay));
                row.createCell(i++).setCellValue(valueOrEmpty(payorDebtAccountVatNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(payorDebtAccountName));
                row.createCell(i++).setCellValue(valueOrEmpty(agreement));
                row.createCell(i++).setCellValue(valueOrEmpty(ingression));
                row.createCell(i++).setCellValue(valueOrEmpty(firstTimeStudent));
                row.createCell(i++).setCellValue(valueOrEmpty(partialRegime));
                row.createCell(i++).setCellValue(valueOrEmpty(statutes));
                row.createCell(i++).setCellValue(valueOrEmpty(numberOfNormalEnrolments));
                row.createCell(i++).setCellValue(valueOrEmpty(numberOfStandaloneEnrolments));
                row.createCell(i++).setCellValue(valueOrEmpty(numberOfExtracurricularEnrolments));
                row.createCell(i++).setCellValue(valueOrEmpty(tuitionPaymentPlan));
                row.createCell(i++).setCellValue(valueOrEmpty(tuitionPaymentPlanConditions));
                row.createCell(i++).setCellValue(valueOrEmpty(annuled));
                row.createCell(i++).setCellValue(valueOrEmpty(annuledReason));
                row.createCell(i++).setCellValue(valueOrEmpty(closeDate));
                row.createCell(i++).setCellValue(valueOrEmpty(openAmountAtERPStartDate));
                row.createCell(i++).setCellValue(valueOrEmpty(exportedInLegacyERP));
                row.createCell(i++).setCellValue(valueOrEmpty(legacyERPCertificateDocumentReference));
                row.createCell(i++).setCellValue(valueOrEmpty(erpCertificationDate));
                row.createCell(i++).setCellValue(valueOrEmpty(erpCertificateDocumentReference));
                row.createCell(i++).setCellValue(valueOrEmpty(originSettlementNoteForAdvancedCredit));

            } else if (invoiceEntry.isCreditNoteEntry()) {
                int i = 1;
                row.createCell(i++).setCellValue(entryType);
                row.createCell(i++).setCellValue(versioningCreator);
                row.createCell(i++).setCellValue(valueOrEmpty(creationDate));
                row.createCell(i++).setCellValue(entryDate.toString(Constants.DATE_TIME_FORMAT_YYYY_MM_DD));
                row.createCell(i++).setCellValue(dueDate.toString(Constants.DATE_FORMAT_YYYY_MM_DD));
                row.createCell(i++).setCellValue(customerId);
                row.createCell(i++).setCellValue(debtAccountId);
                row.createCell(i++).setCellValue(name);
                row.createCell(i++).setCellValue(valueOrEmpty(identificationType));
                row.createCell(i++).setCellValue(valueOrEmpty(identificationNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(vatNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(email));
                row.createCell(i++).setCellValue(valueOrEmpty(address));
                row.createCell(i++).setCellValue(valueOrEmpty(studentNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(registrationNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(degreeType));
                row.createCell(i++).setCellValue(valueOrEmpty(degreeCode));
                row.createCell(i++).setCellValue(valueOrEmpty(degreeName));
                row.createCell(i++).setCellValue(valueOrEmpty(executionYear));
                row.createCell(i++).setCellValue(valueOrEmpty(executionSemester));
                row.createCell(i++).setCellValue(productCode);
                row.createCell(i++).setCellValue(invoiceEntryDescription);
                row.createCell(i++).setCellValue(valueOrEmpty(documentNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(documentExportationPending));
                row.createCell(i++).setCellValue(valueOrEmpty(debitEntryIdentification));
                row.createCell(i++).setCellValue(valueOrEmpty(amountToPay));
                row.createCell(i++).setCellValue(valueOrEmpty(openAmountToPay));
                row.createCell(i++).setCellValue(valueOrEmpty(payorDebtAccountVatNumber));
                row.createCell(i++).setCellValue(valueOrEmpty(payorDebtAccountName));
                row.createCell(i++).setCellValue(valueOrEmpty(agreement));
                row.createCell(i++).setCellValue(valueOrEmpty(ingression));
                row.createCell(i++).setCellValue(valueOrEmpty(firstTimeStudent));
                row.createCell(i++).setCellValue(valueOrEmpty(partialRegime));
                row.createCell(i++).setCellValue(valueOrEmpty(statutes));
                row.createCell(i++).setCellValue(valueOrEmpty(numberOfNormalEnrolments));
                row.createCell(i++).setCellValue(valueOrEmpty(numberOfStandaloneEnrolments));
                row.createCell(i++).setCellValue(valueOrEmpty(numberOfExtracurricularEnrolments));
                row.createCell(i++).setCellValue(valueOrEmpty(tuitionPaymentPlan));
                row.createCell(i++).setCellValue(valueOrEmpty(tuitionPaymentPlanConditions));
                row.createCell(i++).setCellValue(valueOrEmpty(annuled));
                row.createCell(i++).setCellValue(valueOrEmpty(annuledReason));
                row.createCell(i++).setCellValue(valueOrEmpty(closeDate));
                row.createCell(i++).setCellValue(valueOrEmpty(openAmountAtERPStartDate));
                row.createCell(i++).setCellValue(valueOrEmpty(exportedInLegacyERP));
                row.createCell(i++).setCellValue(valueOrEmpty(legacyERPCertificateDocumentReference));
                row.createCell(i++).setCellValue(valueOrEmpty(erpCertificationDate));
                row.createCell(i++).setCellValue(valueOrEmpty(erpCertificateDocumentReference));
                row.createCell(i++).setCellValue(valueOrEmpty(originSettlementNoteForAdvancedCredit));

            }

        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(invoiceEntry, e);
        }
    }

    private String valueOrEmpty(final LocalDate value) {
        if (value == null) {
            return "";
        }

        return value.toString(Constants.DATE_FORMAT_YYYY_MM_DD);
    }

    private String valueOrEmpty(final DateTime value) {
        if (value == null) {
            return "";
        }

        return value.toString(Constants.DATE_TIME_FORMAT_YYYY_MM_DD);
    }

    private String valueOrEmpty(final Boolean value) {
        if (value == null) {
            return "";
        }

        return academicTreasuryBundle(value ? "label.true" : "label.false");
    }

    private String valueOrEmpty(final Integer value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    private String valueOrEmpty(final LocalizedString value) {
        if (value == null) {
            return "";
        }

        if (StringUtils.isEmpty(value.getContent())) {
            return "";
        }

        return value.getContent();
    }

    private String valueOrEmpty(final String value) {
        if (!StringUtils.isEmpty(value)) {
            return value;
        }

        return "";
    }

}
