package org.fenixedu.academictreasury.domain.tuition;

import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
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
        return AcademicTreasuryConstants.academicTreasuryBundleI18N(getClass().getSimpleName() + "." + name());
    }
}
