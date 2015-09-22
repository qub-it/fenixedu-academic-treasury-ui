package org.fenixedu.academictreasury.services.debtReports;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.dto.reports.DebtReportEntryBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.treasury.domain.document.InvoiceEntry;
import org.joda.time.LocalDate;

public class DebtReportService {

    public static Stream<DebtReportEntryBean> debtsReport(final LocalDate begin, final LocalDate end) {
        return InvoiceEntry.findAll().filter(i -> Constants.isDateBetween(begin, end, i.getEntryDateTime()))
                .map(i -> new DebtReportEntryBean(i));
    }
}
