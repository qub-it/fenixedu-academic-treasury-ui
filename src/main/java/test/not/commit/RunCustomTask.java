package test.not.commit;

import java.util.Locale;

import org.fenixedu.academicTreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.FiscalCountryRegion;
import org.fenixedu.treasury.domain.ProductGroup;
import org.fenixedu.treasury.domain.VatType;

import pt.ist.fenixframework.FenixFramework;

public class RunCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
//        final FiscalCountryRegion region = FenixFramework.getDomainObject("285890203090947");
//        final FinantialInstitution institution =
//                FinantialInstitution.create(region, "Instituicao Financeira", "999999991", "999999991", "Instituicao Financeira",
//                        "Instituicao Financeira", "R MORADA", "LISBOA", "1234-123", "PT");
//        AcademicTreasurySettings.getInstance().editEmolumentsProductGroup(ProductGroup.findByCode("EMOLUMENTOS"));

        VatType.create("EXEMPT", new LocalizedString(new Locale("PT", "pt"),  "isento"));
    }

}
