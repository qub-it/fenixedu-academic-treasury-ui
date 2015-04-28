package org.fenixedu.academictreasury.domain.settings;

import java.util.Optional;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.ProductGroup;

import pt.ist.fenixframework.Atomic;

public class AcademicTreasurySettings extends AcademicTreasurySettings_Base {

    protected AcademicTreasurySettings() {
        super();
        setBennu(Bennu.getInstance());
    }

    @Atomic
    public void editEmolumentsProductGroup(final ProductGroup productGroup) {
        setEmolumentsProductGroup(productGroup);
    }

    protected static Optional<AcademicTreasurySettings> find() {
        return Bennu.getInstance().getAcademicTreasurySettingsSet().stream().findFirst();
    }

    @Atomic
    public static AcademicTreasurySettings getInstance() {
        if (!find().isPresent()) {
            return new AcademicTreasurySettings();
        }

        return find().get();
    }

}
