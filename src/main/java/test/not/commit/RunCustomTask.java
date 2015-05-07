package test.not.commit;

import java.math.BigDecimal;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Vat;
import org.fenixedu.treasury.domain.VatType;
import org.joda.time.DateTime;

public class RunCustomTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        Vat.create(VatType.findByCode("EXEMPT"), FinantialInstitution.findByCode("Instituicao Financeira").findFirst().get(), null, new BigDecimal("1"),
                new DateTime().minusDays(10), null);
    }

}
