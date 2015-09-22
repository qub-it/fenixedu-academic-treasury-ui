package org.fenixedu.academictreasury.dto.reports;

import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.serviceRequests.RegistrationAcademicServiceRequest;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent.AcademicTreasuryEventKeys;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.academictreasury.util.streaming.spreadsheet.SpreadsheetRow;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Currency;
import org.fenixedu.treasury.domain.Customer;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.InvoiceEntry;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.util.StringUtils;

public class DebtReportEntryBean implements SpreadsheetRow {

    public static String[] SPREADSHEET_HEADERS = {
        Constants.bundle("label.DebtReportEntryBean.header.identification"),
        Constants.bundle("label.DebtReportEntryBean.header.entryType"),
        Constants.bundle("label.DebtReportEntryBean.header.versioningCreator"),
        Constants.bundle("label.DebtReportEntryBean.header.creationDate"),
        Constants.bundle("label.DebtReportEntryBean.header.entryDate"),
        Constants.bundle("label.DebtReportEntryBean.header.dueDate"),
        Constants.bundle("label.DebtReportEntryBean.header.name"),
        Constants.bundle("label.DebtReportEntryBean.header.identificationType"),
        Constants.bundle("label.DebtReportEntryBean.header.identificationNumber"),
        Constants.bundle("label.DebtReportEntryBean.header.vatNumber"),
        Constants.bundle("label.DebtReportEntryBean.header.email"),
        Constants.bundle("label.DebtReportEntryBean.header.address"),
        Constants.bundle("label.DebtReportEntryBean.header.studentNumber"),
        Constants.bundle("label.DebtReportEntryBean.header.degreeCode"),
        Constants.bundle("label.DebtReportEntryBean.header.degreeName"),
        Constants.bundle("label.DebtReportEntryBean.header.executionYear"),
        Constants.bundle("label.DebtReportEntryBean.header.executionSemester"),
        Constants.bundle("label.DebtReportEntryBean.header.productCode"),
        Constants.bundle("label.DebtReportEntryBean.header.invoiceEntryDescription"),
        Constants.bundle("label.DebtReportEntryBean.header.documentNumber"),
        Constants.bundle("label.DebtReportEntryBean.header.baseAmount"),
        Constants.bundle("label.DebtReportEntryBean.header.creditedAmount"),
        Constants.bundle("label.DebtReportEntryBean.header.amountToPay"),
        Constants.bundle("label.DebtReportEntryBean.header.openAmountToPay"),
        Constants.bundle("label.DebtReportEntryBean.header.agreement"),
        Constants.bundle("label.DebtReportEntryBean.header.ingression"),
        Constants.bundle("label.DebtReportEntryBean.header.firstTimeStudent"),
        Constants.bundle("label.DebtReportEntryBean.header.partialRegime"),
        Constants.bundle("label.DebtReportEntryBean.header.statutes"),
        Constants.bundle("label.DebtReportEntryBean.header.tuitionPaymentPlan"),
        Constants.bundle("label.DebtReportEntryBean.header.tuitionPaymentPlanConditions")
    };
    
    private String identification;
    private String entryType;
    private String versioningCreator;
    private DateTime creationDate;
    private DateTime entryDate;
    private LocalDate dueDate;
    private String name;
    private LocalizedString identificationType;
    private String identificationNumber;
    private String vatNumber;
    private String email;
    private String address;
    private Integer studentNumber;
    private String degreeCode;
    private LocalizedString degreeName;
    private String executionYear;
    private String executionSemester;
    private String productCode;
    private String invoiceEntryDescription;
    private String documentNumber;
    private String baseAmount;
    private String creditedAmount;
    private String amountToPay;
    private String openAmountToPay;
    private LocalizedString agreement;
    private LocalizedString ingression;
    private Boolean firstTimeStudent;
    private Boolean partialRegime;
    private String statutes;
    private String tuitionPaymentPlan;
    private String tuitionPaymentPlanConditions;

    public DebtReportEntryBean(final InvoiceEntry entry) {
        this.identification = entry.getExternalId();
        this.entryType = entryType(entry);
        this.creationDate = entry.getVersioningCreationDate();
        this.versioningCreator = entry.getVersioningCreator();
        this.entryDate = entry.getEntryDateTime();
        this.dueDate = entry.getDueDate();

        fillStudentInformation(entry);

        this.productCode = entry.getProduct().getCode();
        this.invoiceEntryDescription = entry.getDescription();

        if (entry.getFinantialDocument() != null) {
            this.documentNumber = entry.getFinantialDocument().getUiDocumentNumber();
        }

        final Currency currency = entry.getDebtAccount().getFinantialInstitution().getCurrency();

        this.baseAmount = currency.getValueWithScale(entry.getAmountWithVat()).toString();
        if (entry.isDebitNoteEntry()) {
            final DebitEntry debitEntry = (DebitEntry) entry;
            this.creditedAmount = currency.getValueWithScale(debitEntry.getTotalCreditedAmount()).toString();
            this.amountToPay = currency.getValueWithScale(debitEntry.getAvailableAmountForCredit()).toString();
            this.openAmountToPay = currency.getValueWithScale(entry.getOpenAmount()).toString();
        }

        this.openAmountToPay = currency.getValueWithScale(entry.getOpenAmount()).toString();
    }

    private void fillStudentInformation(final InvoiceEntry entry) {
        if (entry.isDebitNoteEntry()) {
            final DebitEntry debitEntry = (DebitEntry) entry;

            final Customer customer = debitEntry.getDebtAccount().getCustomer();

            this.name = customer.getName();

            if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPerson().getIdDocumentType() != null) {
                this.identificationType = ((PersonCustomer) customer).getPerson().getIdDocumentType().getLocalizedNameI18N();
            }

            this.identificationNumber = customer.getIdentificationNumber();
            this.vatNumber = customer.getFiscalNumber();

            if (customer.isPersonCustomer()) {
                this.email = ((PersonCustomer) customer).getPerson().getInstitutionalOrDefaultEmailAddressValue();
            }

            this.address = customer.getAddress();

            if (customer.isPersonCustomer() && ((PersonCustomer) customer).getPerson().getStudent() != null) {
                this.studentNumber = ((PersonCustomer) customer).getPerson().getStudent().getNumber();
            }

            // Degree && ExecutionYear && ExecutionSemester
            if (debitEntry.getTreasuryEvent() != null && debitEntry.getTreasuryEvent() instanceof AcademicTreasuryEvent) {
                final AcademicTreasuryEvent academicTreasuryEvent = (AcademicTreasuryEvent) debitEntry.getTreasuryEvent();

                if (academicTreasuryEvent.isForRegistrationTuition()) {
                    this.degreeCode = academicTreasuryEvent.getRegistration().getDegree().getCode();
                    this.degreeName = academicTreasuryEvent.getRegistration().getDegree().getPresentationNameI18N();
                    this.executionYear = academicTreasuryEvent.getExecutionYear().getQualifiedName();
                    this.tuitionPaymentPlan =
                            AcademicTreasuryEventKeys.valueFor(debitEntry, AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN);
                    this.tuitionPaymentPlanConditions =
                            AcademicTreasuryEventKeys.valueFor(debitEntry,
                                    AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN_CONDITIONS);

                    fillStudentConditionsInformation(academicTreasuryEvent.getRegistration(),
                            academicTreasuryEvent.getExecutionYear());
                } else if ((academicTreasuryEvent.isForStandaloneTuition() || academicTreasuryEvent.isForExtracurricularTuition())) {
                    if (debitEntry.getCurricularCourse() != null) {
                        this.degreeCode = debitEntry.getCurricularCourse().getDegree().getCode();
                        this.degreeName = debitEntry.getCurricularCourse().getDegree().getPresentationNameI18N();
                    }

                    if (debitEntry.getExecutionSemester() != null) {
                        this.executionYear = debitEntry.getExecutionSemester().getExecutionYear().getQualifiedName();
                        this.executionSemester = debitEntry.getExecutionSemester().getQualifiedName();
                    }

                    this.tuitionPaymentPlan =
                            AcademicTreasuryEventKeys.valueFor(debitEntry, AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN);
                    this.tuitionPaymentPlanConditions =
                            AcademicTreasuryEventKeys.valueFor(debitEntry,
                                    AcademicTreasuryEventKeys.TUITION_PAYMENT_PLAN_CONDITIONS);

                    if (academicTreasuryEvent.getRegistration() != null && academicTreasuryEvent.getExecutionYear() != null) {
                        fillStudentConditionsInformation(academicTreasuryEvent.getRegistration(),
                                academicTreasuryEvent.getExecutionYear());
                    }

                } else if (academicTreasuryEvent.isForImprovementTax()) {
                    if (debitEntry.getCurricularCourse() != null) {
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

                    this.degreeCode = academicTreasuryEvent.getRegistration().getDegree().getCode();
                    this.degreeName = academicTreasuryEvent.getRegistration().getDegree().getPresentationNameI18N();
                    this.executionYear = academicTreasuryEvent.getExecutionYear().getQualifiedName();

                    fillStudentConditionsInformation(academicTreasuryEvent.getRegistration(),
                            academicTreasuryEvent.getExecutionYear());

                } else if (academicTreasuryEvent.isForAcademicServiceRequest()
                        && academicTreasuryEvent.getAcademicServiceRequest().isRequestForRegistration()) {
                    
                    final RegistrationAcademicServiceRequest registrationAcademicServiceRequest =
                            (RegistrationAcademicServiceRequest) academicTreasuryEvent.getAcademicServiceRequest();
                    this.degreeCode = registrationAcademicServiceRequest.getRegistration().getDegree().getCode();
                    this.degreeName = registrationAcademicServiceRequest.getRegistration().getDegree().getPresentationNameI18N();

                    if (academicTreasuryEvent.getAcademicServiceRequest().getExecutionYear() != null) {
                        this.executionYear =
                                academicTreasuryEvent.getAcademicServiceRequest().getExecutionYear().getQualifiedName();
                    }

                    if (academicTreasuryEvent.getAcademicServiceRequest().getExecutionYear() != null) {
                        fillStudentConditionsInformation(registrationAcademicServiceRequest.getRegistration(),
                                academicTreasuryEvent.getAcademicServiceRequest().getExecutionYear());
                    }
                }
            }
        }
    }

    private String entryType(final InvoiceEntry entry) {
        if(entry.isDebitNoteEntry()) {
            return Constants.bundle("label.DebtReportEntryBean.debitNoteEntry");
        } else if(entry.isCreditNoteEntry()) {
            return Constants.bundle("label.DebtReportEntryBean.creditNoteEntry");            
        }
        
        return null;
    }

    private void fillStudentConditionsInformation(final Registration registration, final ExecutionYear executionYear) {
        this.firstTimeStudent = registration.isFirstTime(executionYear);
        this.partialRegime = registration.isPartialRegime(executionYear);
        this.statutes = statutes(registration, executionYear);
        this.agreement = registration.getRegistrationProtocol().getDescription();
        this.ingression = registration.getIngressionType().getDescription();
    }

    private String statutes(final Registration registration, final ExecutionYear executionYear) {
        return registration.getStudent().getStatutesTypesValidOnAnyExecutionSemesterFor(executionYear).stream()
                .map(s -> s.getName().getContent()).reduce((a, c) -> c + ", " + a).orElse(null);
    }

    
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    
    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getVersioningCreator() {
        return versioningCreator;
    }

    public void setVersioningCreator(String versioningCreator) {
        this.versioningCreator = versioningCreator;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public DateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(DateTime entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalizedString getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(LocalizedString identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getDegreeCode() {
        return degreeCode;
    }

    public void setDegreeCode(String degreeCode) {
        this.degreeCode = degreeCode;
    }

    public LocalizedString getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(LocalizedString degreeName) {
        this.degreeName = degreeName;
    }

    public String getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(String executionYear) {
        this.executionYear = executionYear;
    }

    public String getExecutionSemester() {
        return executionSemester;
    }

    public void setExecutionSemester(String executionSemester) {
        this.executionSemester = executionSemester;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getInvoiceEntryDescription() {
        return invoiceEntryDescription;
    }

    public void setInvoiceEntryDescription(String invoiceEntryDescription) {
        this.invoiceEntryDescription = invoiceEntryDescription;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(String baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getCreditedAmount() {
        return creditedAmount;
    }

    public void setCreditedAmount(String creditedAmount) {
        this.creditedAmount = creditedAmount;
    }

    public String getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(String amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getOpenAmountToPay() {
        return openAmountToPay;
    }

    public void setOpenAmountToPay(String openAmountToPay) {
        this.openAmountToPay = openAmountToPay;
    }

    public LocalizedString getAgreement() {
        return agreement;
    }

    public void setAgreement(LocalizedString agreement) {
        this.agreement = agreement;
    }

    public LocalizedString getIngression() {
        return ingression;
    }

    public void setIngression(LocalizedString ingression) {
        this.ingression = ingression;
    }

    public Boolean getFirstTimeStudent() {
        return firstTimeStudent;
    }

    public Boolean getPartialRegime() {
        return partialRegime;
    }

    public String getStatutes() {
        return statutes;
    }

    public String getTuitionPaymentPlan() {
        return tuitionPaymentPlan;
    }

    public String getTuitionPaymentPlanConditions() {
        return tuitionPaymentPlanConditions;
    }

    @Override
    public void writeCellValues(final Row row) {
        row.createCell(0).setCellValue(identification);
        row.createCell(1).setCellValue(entryType);
        row.createCell(2).setCellValue(versioningCreator);
        row.createCell(3).setCellValue(valueOrEmpty(creationDate));
        row.createCell(4).setCellValue(entryDate.toString(Constants.DATE_TIME_FORMAT));
        row.createCell(5).setCellValue(dueDate.toString(Constants.DATE_FORMAT));
        row.createCell(6).setCellValue(name);
        row.createCell(7).setCellValue(valueOrEmpty(identificationType));
        row.createCell(8).setCellValue(valueOrEmpty(identificationNumber));
        row.createCell(9).setCellValue(valueOrEmpty(vatNumber));
        row.createCell(10).setCellValue(valueOrEmpty(email));
        row.createCell(11).setCellValue(valueOrEmpty(address));
        row.createCell(12).setCellValue(valueOrEmpty(studentNumber));
        row.createCell(13).setCellValue(valueOrEmpty(degreeCode));
        row.createCell(14).setCellValue(valueOrEmpty(degreeName));
        row.createCell(15).setCellValue(valueOrEmpty(executionYear));
        row.createCell(16).setCellValue(valueOrEmpty(executionSemester));
        row.createCell(17).setCellValue(productCode);
        row.createCell(18).setCellValue(invoiceEntryDescription);
        row.createCell(19).setCellValue(valueOrEmpty(documentNumber));
        row.createCell(20).setCellValue(baseAmount);
        row.createCell(21).setCellValue(valueOrEmpty(creditedAmount));
        row.createCell(22).setCellValue(valueOrEmpty(amountToPay));
        row.createCell(23).setCellValue(openAmountToPay);
        row.createCell(24).setCellValue(valueOrEmpty(agreement));
        row.createCell(25).setCellValue(valueOrEmpty(ingression));
        row.createCell(26).setCellValue(valueOrEmpty(firstTimeStudent));
        row.createCell(27).setCellValue(valueOrEmpty(partialRegime));
        row.createCell(28).setCellValue(valueOrEmpty(statutes));
        row.createCell(29).setCellValue(valueOrEmpty(tuitionPaymentPlan));
        row.createCell(30).setCellValue(valueOrEmpty(tuitionPaymentPlanConditions));
    }

    private String valueOrEmpty(final DateTime value) {
        if(value == null) {
            return "";
        }
        
        return value.toString(Constants.DATE_TIME_FORMAT);
    }

    private String valueOrEmpty(final Boolean value) {
        if(value == null) {
            return "";
        }
        
        return Constants.bundle(value ? "label.true" : "label.false");
    }

    private String valueOrEmpty(final Integer value) {
        if(value == null) {
            return null;
        }
        
        return "";
    }

    private String valueOrEmpty(final LocalizedString value) {
        if(value == null) {
            return "";
        }
        
        if(StringUtils.isEmpty(value.getContent())) {
            return "";
        }
        
        return value.getContent();
    }

    private String valueOrEmpty(final String value) {
        if(!StringUtils.isEmpty(value)) {
            return value;
        }
        
        return "";
    }
    
    
}
