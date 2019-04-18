package org.fenixedu.academictreasury.dto.reports;

import static org.fenixedu.academictreasury.util.AcademicTreasuryConstants.academicTreasuryBundle;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academictreasury.domain.reports.DebtReportRequest;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.util.streaming.spreadsheet.IErrorsLog;

public class ProductReportEntryBean extends AbstractReportEntryBean {

    // @formatter:off
    public static final String[] SPREADSHEET_HEADERS = { 
            academicTreasuryBundle("label.ProductReportEntryBean.header.identification"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.group.code"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.group"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.code"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.description.pt"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.description.en"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.unitOfMeasure.pt"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.unitOfMeasure.en"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.active"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.legacy"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.tuitionInstallmentOrder"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.vatType.code"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.vatType"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.exemptionReason.code"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.exemptionReason"),
            academicTreasuryBundle("label.ProductReportEntryBean.header.finantialInstitution") };
    // @formatter:on

    private String identification;
    private String groupCode;
    private String group;
    private String code;
    private String descriptionPt;
    private String descriptionEn;
    private String unitOfMeasurePt;
    private String unitOfMeasureEn;
    private boolean active;
    private boolean legacy;
    private int tuitionInstallmentOrder;
    private String vatTypeCode;
    private String vatType;
    private String exemptionReasonCode;
    private String exemptionReason;
    private String finantialInstitution;

    private Product product;

    boolean completed = false;

    public ProductReportEntryBean(final Product p, final DebtReportRequest request, final ErrorsLog errorsLog) {
        this.product = p;

        try {
            this.identification = p.getExternalId();
            this.groupCode = p.getProductGroup() != null ? p.getProductGroup().getCode() : "";
            this.group = p.getProductGroup().getName().getContent();
            this.code = p.getCode();
            this.descriptionPt = p.getName().getContent(AcademicTreasuryConstants.DEFAULT_LANGUAGE);
            this.descriptionEn = p.getName().getContent(AcademicTreasuryConstants.ENGLISH_LANGUAGE);
            this.unitOfMeasurePt = p.getUnitOfMeasure().getContent(AcademicTreasuryConstants.DEFAULT_LANGUAGE);
            this.unitOfMeasureEn = p.getUnitOfMeasure().getContent(AcademicTreasuryConstants.ENGLISH_LANGUAGE);
            this.active = p.isActive();
            this.legacy = p.isLegacy();
            this.tuitionInstallmentOrder = p.getTuitionInstallmentOrder();
            this.vatTypeCode = p.getVatType() != null ? p.getVatType().getCode() : "";
            this.vatType = p.getVatType() != null ? p.getVatType().getName().getContent() : "";
            this.exemptionReasonCode = p.getVatExemptionReason() != null ? p.getVatExemptionReason().getCode() : "";
            this.exemptionReason = p.getVatExemptionReason() != null ? p.getVatExemptionReason().getName().getContent() : "";
            this.finantialInstitution = !p.getFinantialInstitutionsSet().isEmpty() ? p.getFinantialInstitutionsSet().iterator()
                    .next().getFiscalNumber() : "";

            this.completed = true;
        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(this.product, e);
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

            row.createCell(i++).setCellValue(valueOrEmpty(groupCode));
            row.createCell(i++).setCellValue(valueOrEmpty(group));
            row.createCell(i++).setCellValue(valueOrEmpty(code));
            row.createCell(i++).setCellValue(valueOrEmpty(descriptionPt));
            row.createCell(i++).setCellValue(valueOrEmpty(descriptionEn));
            row.createCell(i++).setCellValue(valueOrEmpty(unitOfMeasurePt));
            row.createCell(i++).setCellValue(valueOrEmpty(unitOfMeasureEn));
            row.createCell(i++).setCellValue(valueOrEmpty(active));
            row.createCell(i++).setCellValue(valueOrEmpty(legacy));
            row.createCell(i++).setCellValue(valueOrEmpty(tuitionInstallmentOrder));
            row.createCell(i++).setCellValue(valueOrEmpty(vatTypeCode));
            row.createCell(i++).setCellValue(valueOrEmpty(vatType));
            row.createCell(i++).setCellValue(valueOrEmpty(exemptionReasonCode));
            row.createCell(i++).setCellValue(valueOrEmpty(exemptionReason));
            row.createCell(i++).setCellValue(valueOrEmpty(finantialInstitution));
            
        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(this.product, e);
        }
    }

}
