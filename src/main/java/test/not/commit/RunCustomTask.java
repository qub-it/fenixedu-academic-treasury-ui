package test.not.commit;

import java.math.BigDecimal;
import java.util.Locale;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.FixedTariff;
import org.joda.time.DateTime;

import pt.ist.fenixframework.FenixFramework;

public class RunCustomTask extends CustomTask {

    private static final Locale PT = new Locale("PT",  "pt");

    @Override
    public void runTask() throws Exception {
        final Product product = FenixFramework.getDomainObject("848951825661959");
        final FinantialEntity finantialEntity = FenixFramework.getDomainObject("849063494811650");
        
        FixedTariff.create(product, null, finantialEntity, BigDecimal.ZERO, new DateTime().minusYears(1), null, DueDateCalculationType.NO_DUE_DATE, null, 0, false);
    }

}
