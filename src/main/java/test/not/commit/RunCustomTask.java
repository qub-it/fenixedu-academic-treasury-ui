package test.not.commit;

import java.util.Locale;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.ProductGroup;
import org.fenixedu.treasury.domain.VatType;

public class RunCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        Product.create(ProductGroup.findByCode("PROPINA"), "PROPINA_MATRICULA", new LocalizedString(new Locale("PT", "pt"),
                "Propina de Matrícula"), new LocalizedString(new Locale("PT", "pt"), "Un"), true, VatType.findByCode("ISE"));
    }

}
