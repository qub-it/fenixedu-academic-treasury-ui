package org.fenixedu.academictreasury.dto.reports;

import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.util.streaming.spreadsheet.SpreadsheetRow;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.util.StringUtils;

public abstract class AbstractReportEntryBean implements SpreadsheetRow {

    protected String valueOrEmpty(final LocalDate value) {
        if (value == null) {
            return "";
        }

        return value.toString(Constants.DATE_FORMAT_YYYY_MM_DD);        
    }
    
    protected String valueOrEmpty(final DateTime value) {
        if (value == null) {
            return "";
        }

        return value.toString(Constants.DATE_TIME_FORMAT_YYYY_MM_DD);
    }

    protected String valueOrEmpty(final Boolean value) {
        if (value == null) {
            return "";
        }

        return Constants.bundle(value ? "label.true" : "label.false");
    }

    protected String valueOrEmpty(final Integer value) {
        if (value == null) {
            return null;
        }

        return value.toString();
    }

    protected String valueOrEmpty(final LocalizedString value) {
        if (value == null) {
            return "";
        }

        if (StringUtils.isEmpty(value.getContent())) {
            return "";
        }

        return value.getContent();
    }

    protected String valueOrEmpty(final String value) {
        if (!StringUtils.isEmpty(value)) {
            return value;
        }

        return "";
    }
    
}
