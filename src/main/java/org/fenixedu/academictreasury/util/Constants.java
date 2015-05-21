package org.fenixedu.academictreasury.util;

import java.math.BigDecimal;
import java.util.Locale;

import org.fenixedu.commons.i18n.I18N;

public class Constants {
    public static final String BUNDLE = "resources.AcademicTreasuryResources";
    
    public static final BigDecimal HUNDRED_PERCENT = new BigDecimal("100.00");

    public static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);
    
    public static boolean isForeignLanguage(final Locale language) {
        return !language.getLanguage().equals(I18N.getLocale());
    }
    
}
