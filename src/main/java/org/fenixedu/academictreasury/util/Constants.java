package org.fenixedu.academictreasury.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.I18N;

public class Constants {

    private static final int SCALE = 20;

    public static final String BUNDLE = "resources.AcademicTreasuryResources";

    public static final BigDecimal HUNDRED_PERCENT = new BigDecimal("100.00");

    public static final BigDecimal DEFAULT_QUANTITY = new BigDecimal(1);

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final TupleDataSourceBean SELECT_OPTION = new TupleDataSourceBean("", BundleUtil.getString(Constants.BUNDLE,
            "label.TupleDataSourceBean.select.description"));

    public static boolean isForeignLanguage(final Locale language) {
        return !language.getLanguage().equals(I18N.getLocale());
    }

    public static BigDecimal defaultScale(final BigDecimal v) {
        return v.setScale(20, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal divide(final BigDecimal a, BigDecimal b) {
        return a.divide(b, SCALE, RoundingMode.HALF_EVEN);
    }

}
