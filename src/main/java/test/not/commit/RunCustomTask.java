package test.not.commit;

import java.util.Locale;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;

public class RunCustomTask extends CustomTask {

    private static final Locale PT = new Locale("PT",  "pt");

    @Override
    public void runTask() throws Exception {
        new EvaluationSeason(new LocalizedString(PT,  "NM"), new LocalizedString(PT, "Normal"), true, false, false, false);
        new EvaluationSeason(new LocalizedString(PT,  "M"), new LocalizedString(PT, "Melhoria"), false, true, false, false);
        new EvaluationSeason(new LocalizedString(PT,  "SP"), new LocalizedString(PT, "Especial"), false, false, false, true);
    }

}
