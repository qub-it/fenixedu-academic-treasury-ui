package org.fenixedu.academictreasury.dto.reports;

import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academictreasury.domain.reports.ErrorsLog;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.util.streaming.spreadsheet.IErrorsLog;

public class ProductReportEntryBean extends AbstractReportEntryBean {

    public static final String[] SPREADSHEET_HEADERS = { 
            Constants.bundle("label.ProductReportEntryBean.header.identification"),
            Constants.bundle("label.ProductReportEntryBean.header.code"),
            Constants.bundle("label.ProductReportEntryBean.header.name"), };

    private String identification;
    private String code;
    private String name;

    private Product product;

    boolean completed = false;

    public ProductReportEntryBean(final Product p, final String decimalSeparator, final ErrorsLog errorsLog) {
        this.product = p;
        
        try {
            this.identification = p.getExternalId();
            this.code = p.getCode();
            this.name = p.getName().getContent();

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
                row.createCell(1).setCellValue(Constants.bundle("error.DebtReportEntryBean.report.generation.verify.entry"));
                return;
            }

            int i = 1;

            row.createCell(i++).setCellValue(valueOrEmpty(code));
            row.createCell(i++).setCellValue(valueOrEmpty(name));
        } catch (final Exception e) {
            e.printStackTrace();
            errorsLog.addError(this.product, e);
        }

    }

}
