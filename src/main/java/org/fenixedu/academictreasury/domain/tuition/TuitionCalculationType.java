package org.fenixedu.academictreasury.domain.tuition;

import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.commons.i18n.LocalizedString;

public enum TuitionCalculationType {
    FIXED_AMOUNT,
    ECTS,
    UNITS;
    
    public boolean isFixedAmount() {
        return this == FIXED_AMOUNT;
    }
    
    public boolean isEcts() {
        return this == ECTS;
    }
    
    public boolean isUnits() {
        return this == UNITS;
    }
    
    public LocalizedString getDescriptionI18N() {
        return Constants.academicTreasuryBundleI18N(getClass().getSimpleName() + "." + name());
    }
}
